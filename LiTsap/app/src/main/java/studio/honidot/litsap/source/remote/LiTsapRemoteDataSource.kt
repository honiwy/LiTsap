package studio.honidot.litsap.source.remote

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapDataSource
import studio.honidot.litsap.util.Logger
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object LiTsapRemoteDataSource : LiTsapDataSource {

    private const val PATH_USERS = "users"
    private const val PATH_TASKS = "tasks"
    private const val PATH_MODULES = "modules"
    private const val PATH_HISTORY = "history"

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

    override suspend fun getTasks(taskIdList: List<String>): Result<List<Task>> =
        suspendCoroutine { continuation ->
            val tasks = mutableListOf<Task>()
            FirebaseFirestore.getInstance().collection(PATH_TASKS).whereIn("taskId", taskIdList)
                .get().addOnCompleteListener { findTask ->
                    if (findTask.isSuccessful) {
                        for (documentT in findTask.result!!) {
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

    override suspend fun getHistory(taskId: String,passNday:Long): Result<List<History>> =
        suspendCoroutine { continuation ->
            val timeMin =  LocalDateTime.now().minusDays(passNday).toEpochSecond(ZoneOffset.MIN)*1000
            val listH = mutableListOf<History>()
            FirebaseFirestore.getInstance().collection(PATH_TASKS)
                .document(taskId).collection(PATH_HISTORY).whereGreaterThan("recordDate",timeMin)
                .get().addOnCompleteListener { findHistory ->
                    Logger.d("hi $timeMin")
                    if (findHistory.isSuccessful) {
                        for (documentH in findHistory.result!!) {
                            val history =documentH.toObject(History::class.java)
                                Logger.d("one history: ${history.recordDate}")
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

    override suspend fun createTaskModules(taskId: String, modules: Module): Result<Boolean> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
                .collection(PATH_MODULES)
                .document().set(modules).addOnCompleteListener { addModule ->
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
                        Logger.d("Update task id in user collection success!")
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


}