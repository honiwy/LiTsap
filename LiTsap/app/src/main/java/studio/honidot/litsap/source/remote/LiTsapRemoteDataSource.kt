package studio.honidot.litsap.source.remote

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapDataSource
import studio.honidot.litsap.util.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object LiTsapRemoteDataSource : LiTsapDataSource {

    private const val PATH_USERS = "users"
    private const val PATH_TASKS = "tasks"
    private const val PATH_MODULES = "modules"
    private const val PATH_HISTORY = "history"

    override suspend fun getUser(userId : String): Result<User> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance().collection(PATH_USERS).document(userId)
            .get().addOnCompleteListener { findUser ->
                if (findUser.isSuccessful) {
                    val user = findUser.result!!.toObject(User::class.java)!!
                    continuation.resume(Result.Success(user))
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

    override suspend fun getTasks(taskIdList: List<String>): Result<List<Task>> = suspendCoroutine { continuation ->
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

    override suspend fun getModules(taskId: String): Result<List<Module>> = suspendCoroutine { continuation ->
        val modules = mutableListOf<Module>()
        FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId).collection(PATH_MODULES)
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

    override suspend fun getHistory(taskIdList: List<String>): Result<List<History>> = suspendCoroutine { continuation ->
        val listH = mutableListOf<History>()
        FirebaseFirestore.getInstance().collection(PATH_TASKS).whereIn("taskId", taskIdList)
            .get().addOnCompleteListener { findTask ->
                if (findTask.isSuccessful) {
                    for (documentT in findTask.result!!) {
                        FirebaseFirestore.getInstance().collection(PATH_TASKS).document(documentT.id).collection(PATH_HISTORY)
                            .get().addOnCompleteListener { findHistory ->
                                if (findHistory.isSuccessful) {
                                    for (documentH in findHistory.result!!) {
                                        listH.add(documentH.toObject(History::class.java))
                                    }
                                } else {
                                    findHistory.exception?.let {
                                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                                        return@addOnCompleteListener
                                    }
                                }
                            }
                    }
                    continuation.resume(Result.Success(listH))
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

    override suspend fun createTaskModules(taskId: String, modules: Module): Result<Boolean> = suspendCoroutine { continuation ->
        val moduleCollection = FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId).collection(PATH_MODULES)

            moduleCollection.document().set(modules).addOnCompleteListener { addModule ->
                if (addModule.isSuccessful) {
                    Toast.makeText(
                        LiTsapApplication.appContext,
                        instance.getString(R.string.create_module_success),
                        Toast.LENGTH_SHORT
                    ).show()
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


//        fun createTask() {
//        val tasksDocument = LiTsapApplication.db.collection(studio.honidot.litsap.task.create.PATH_USERS).document(
//            PATH_USERS_DOCUMENT
//        ).collection(studio.honidot.litsap.task.create.PATH_TASKS).document()
//        tasksDocument.set(
//            Task(
//                userId = "",
//                taskId = tasksDocument.id,
//                taskName = title.value ?: "無任務名稱",
//                taskCategoryId = selectedTaskCategoryPosition.value ?: 5,
//                accumCount = 0,
//                goalCount = amount.value ?: 1,
//                dueDate = dueDate.value ?: 1,
//                groupId = "",
//                todayDone = false,
//                taskDone = false
//            )
//        )
//        moduleNameList.value!!.forEach {
//            tasksDocument.collection(studio.honidot.litsap.task.create.PATH_MODULES).document().set(Module(it, 0)).addOnSuccessListener {
//                Log.i("HAHA", "Success")
//            }.addOnFailureListener {
//                Log.i("HAHA", "Oh no")
//            }
//        }
//        Toast.makeText(
//            LiTsapApplication.appContext,
//            instance.getString(R.string.create_task_success),
//            Toast.LENGTH_SHORT
//        ).show()
//    }
//    override suspend fun addUserOngoingList(userId : String, taskId: String): Result<Boolean> = suspendCoroutine { continuation ->
//        val userCollection = FirebaseFirestore.getInstance().collection(PATH_USERS)
//        userCollection.whereEqualTo("userId",userId).get().addOnCompleteListener {findUser->
//            if (findUser.isSuccessful) {
//                for (doc in findUser.result!! ) {
//                    userCollection.document(doc.id).update("userCollection", FieldValue.arrayUnion(taskId))
//                }
//                continuation.resume(Result.Success(true))
//            }else {
//                findUser.exception?.let {
//
//                    Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
//                    continuation.resume(Result.Error(it))
//                }
//                continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
//            }
//        }
//    }
//

}