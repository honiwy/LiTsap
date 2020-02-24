package studio.honidot.litsap.source.remote

import android.net.Uri
import android.text.format.DateFormat
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapDataSource
import studio.honidot.litsap.util.Logger
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object LiTsapRemoteDataSource : LiTsapDataSource {

    private const val PATH_USERS = "users"
    private const val PATH_TASKS = "tasks"
    private const val PATH_MODULES = "modules"
    private const val PATH_HISTORY = "history"

    override suspend fun findUser(firebaseUserId: String): Result<User?>  = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance().collection(PATH_USERS).document(firebaseUserId)
            .get()
            .addOnCompleteListener { findUser ->
                if (findUser.isSuccessful) {
                    findUser.result?.let {documentU->
                        val user = documentU.toObject(User::class.java)
                        continuation.resume(Result.Success(user))
                    }
                } else {
                    findUser.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun createUser(user : User): Result<Boolean> =
    suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance().collection(PATH_USERS).document(user.userId).set(user)
            .addOnCompleteListener { addUser ->
                if (addUser.isSuccessful) {

                    continuation.resume(Result.Success(true))
                } else {
                    addUser.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
                    continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override fun getUser(userId: String): LiveData<User> {
        val user = MutableLiveData<User>()
        FirebaseFirestore.getInstance().collection(PATH_USERS).document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    user.value = snapshot.toObject(User::class.java)!!

                } else {
                    Logger.d("Current data: null")
                }
            }

        return user
    }

    override suspend fun deleteUserOngoingTask(userId: String, taskId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(userId)
                .update("ongoingTasks", FieldValue.arrayRemove(taskId))
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun deleteTask(taskId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            Logger.e("You choose delete $taskId")
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
                .delete()
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        Logger.d(" override suspend fun deleteTask!")
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getTasks(taskIdList: List<String>): Result<List<Task>> =
        suspendCoroutine { continuation ->
            val tasks = mutableListOf<Task>()
            FirebaseFirestore.getInstance().collection(PATH_TASKS).whereIn("taskId", taskIdList).orderBy("todayDone")
                .get().addOnCompleteListener { findTask ->
                    if (findTask.isSuccessful) {
                        for (documentT in findTask.result!!) {
                            Logger.i("taskFound: ${documentT.id}")
                            val taskFound = documentT.toObject(Task::class.java)
                            tasks.add(taskFound)
                        }
                        continuation.resume(Result.Success(tasks))
                    } else {
                        findTask.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getModules(taskId: String): Result<List<Module>> =
        suspendCoroutine { continuation ->
            val modules = mutableListOf<Module>()
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
                .collection(PATH_MODULES)
                .get().addOnCompleteListener { findModule ->
                    if (findModule.isSuccessful) {
                        for (documentM in findModule.result!!) {
                            val moduleFound = documentM.toObject(Module::class.java)
                            modules.add(moduleFound)
                        }
                        continuation.resume(Result.Success(modules))
                    } else {
                        findModule.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getHistory(taskIdList: List<String>,passNday:Int): Result<List<History>> =
        suspendCoroutine { continuation ->
            val timeMin =  LocalDateTime.now().minusDays(passNday.toLong()).toEpochSecond(ZoneOffset.MAX)*1000
            Logger.i("timeMin: $timeMin")
            val listH = mutableListOf<History>()
            FirebaseFirestore.getInstance().collectionGroup("history").whereIn("taskId",taskIdList).whereGreaterThan("recordDate",timeMin).get()
                .addOnCompleteListener { findHistory ->
                    if (findHistory.isSuccessful) {
                        for (documentH in findHistory.result!!) {
                            val history = documentH.toObject(History::class.java)
                                Logger.d("one history: ${history.taskName}, note ${history.note}, point ${history.achieveCount}")
                            listH.add(documentH.toObject(History::class.java))
                        }
                        continuation.resume(Result.Success(listH))
                    } else {
                        findHistory.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getHistoryOnThatDay(taskIdList: List<String>,dateString:String): Result<List<History>> =
        suspendCoroutine { continuation ->
            val startDate = SimpleDateFormat("dd/MM/yyyy").parse(dateString).time
            Logger.i("startDate: $dateString -> $startDate")
            Logger.i("endDate: $dateString -> ${startDate + 86400000}")
            val listH = mutableListOf<History>()
            FirebaseFirestore.getInstance().collectionGroup("history").whereIn("taskId",taskIdList)
                .whereGreaterThan("recordDate",startDate).whereLessThan("recordDate",startDate + 86400000).get()
                .addOnCompleteListener { findHistory ->
                    if (findHistory.isSuccessful) {
                        for (documentH in findHistory.result!!) {
                            val history = documentH.toObject(History::class.java)
                            Logger.d("one history: ${history.taskName}, note ${history.note}, point ${history.achieveCount}")
                            listH.add(documentH.toObject(History::class.java))
                        }
                        continuation.resume(Result.Success(listH))
                    } else {
                        findHistory.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun createTask(task: Task): Result<String> = suspendCoroutine { continuation ->
        val newTaskDocument = FirebaseFirestore.getInstance().collection(PATH_TASKS).document()
        task.taskId = newTaskDocument.id
        newTaskDocument.set(task).addOnCompleteListener { addTask ->
            if (addTask.isSuccessful) {
                Toast.makeText(
                    LiTsapApplication.appContext,
                    instance.getString(R.string.create_task_success),
                    Toast.LENGTH_SHORT
                ).show()
                continuation.resume(Result.Success(newTaskDocument.id))
            } else {
                addTask.exception?.let {
                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                    continuation.resume(Result.Error(it))
                }
                continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
            }
        }
    }

    override suspend fun createTaskModules(taskId: String, module: Module): Result<Boolean> =
        suspendCoroutine { continuation ->
            val newModuleDocument = FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
                .collection(PATH_MODULES).document()
            module.moduleId = newModuleDocument.id
            newModuleDocument.set(module).addOnCompleteListener { addModule ->
                    if (addModule.isSuccessful) {
                        Logger.d("Add module success!")
                        continuation.resume(Result.Success(true))
                    } else {
                        addModule.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }



    override suspend fun addUserOngoingList(userId: String, taskId: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(userId)
                .update("ongoingTasks", FieldValue.arrayUnion(taskId))
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun updateTaskStatus(taskId: String, accumulationPoints: Long): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
                .update(mapOf("todayDone" to true, "accumCount" to FieldValue.increment(accumulationPoints.toLong())))
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }


    //If user did the task that is done today will be something wrong with following code
    override suspend fun updateUserStatus(workout: Workout): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_USERS).document(workout.userId)
                .update(mapOf("experience" to FieldValue.increment(workout.achieveSectionCount*workout.achieveSectionCount.toLong()),
                    "todayDoneCount" to FieldValue.increment(1L)))
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        Logger.w("updateUserStatus: ${workout.achieveSectionCount} is ${workout.achieveSectionCount*workout.achieveSectionCount}")
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun updateTaskModule(workout: Workout): Result<Boolean> =
        suspendCoroutine { continuation ->
            Logger.w("suspend fun updateTaskModule: ${workout.taskId}")
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(workout.taskId).collection(
                PATH_MODULES).document(workout.moduleId)
                .update("achieveSection", FieldValue.increment(workout.achieveSectionCount.toLong()))
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun createFirstTaskHistory(history: History): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(history.taskId)
                .collection(PATH_HISTORY)
                .document().set(history).addOnCompleteListener { addModule ->
                    if (addModule.isSuccessful) {
                        Logger.d("Add first history success!")
                        continuation.resume(Result.Success(true))
                    } else {
                        addModule.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }


    override suspend fun createTaskHistory(history: History): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(history.taskId).collection(
                PATH_HISTORY).document().set(history)
                .addOnCompleteListener { addId ->
                    if (addId.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        addId.exception?.let {

                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun uploadImage(imageUri: Uri): Result<Uri> =
        suspendCoroutine { continuation ->

            val ref = FirebaseStorage.getInstance().reference.child("uploads/" + UUID.randomUUID().toString())
            ref.putFile(imageUri).continueWithTask(Continuation<UploadTask.TaskSnapshot, com.google.android.gms.tasks.Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
                    continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                continuation.resume(Result.Success(task.result!!))
            }.addOnFailureListener {
                Logger.w("Upload fail!")
            }
        }



}