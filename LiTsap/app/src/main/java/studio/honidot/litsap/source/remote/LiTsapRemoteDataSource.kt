package studio.honidot.litsap.source.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapDataSource
import studio.honidot.litsap.util.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object LiTsapRemoteDataSource : LiTsapDataSource {

    private const val PATH_USERS = "users"
    private const val PATH_USERS_DOCUMENT = "8ZoicZsGSucyU2niQ4nr"
    private const val PATH_TASKS = "tasks"
    private const val PATH_MODULES = "modules"
    private const val PATH_HISTORY = "history"

    override suspend fun getOngoingTaskList(user: User): Result<List<Task>> =
        suspendCoroutine { continuation ->
            val list = mutableListOf<Task>()

            FirebaseFirestore.getInstance().collection(PATH_TASKS)
                .whereIn("taskId", user.ongoingTasks)
                .get().addOnCompleteListener { findTaskResult ->
                    if (findTaskResult.isSuccessful) {
                        for (documentT in findTaskResult.result!!) {
                            val listM = mutableListOf<Module>()
                            FirebaseFirestore.getInstance().collection(PATH_TASKS)
                                .document(documentT.id).collection(PATH_MODULES).get()
                                .addOnCompleteListener { findModuleResult ->
                                    for (documentM in findModuleResult.result!!) {
                                        listM.add(documentM.toObject(Module::class.java))
                                    }
                                }
                            val taskFound = Task(
                                accumCount = documentT.data["accumCount"].toString().toInt(),
                                dueDate = documentT.data["dueDate"].toString().toLong(),
                                goalCount = documentT.data["goalCount"].toString().toInt(),
                                groupId = documentT.data["groupId"].toString(),
                                taskCategoryId = documentT.data["taskCategoryId"].toString().toInt(),
                                taskDone = documentT.data["taskDone"].toString().toBoolean(),
                                taskId = documentT.data["taskId"].toString(),
                                taskName = documentT.data["taskName"].toString(),
                                todayDone = documentT.data["todayDone"].toString().toBoolean(),
                                modules = listM,
                                userId = documentT.data["userId"].toString()
                            )//document.toObject(TaskItem::class.java)
                            list.add(taskFound)
                        }
                        continuation.resume(Result.Success(list))
                    } else {
                        findTaskResult.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }

//            var count = 0
//            user.ongoingTasks.forEach { taskId ->
//                Log.i("HAHA", "task id: $taskId")
//                val taskDocument =
//                    FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
//                taskDocument.get().addOnCompleteListener { findTaskResult ->
//                    if (findTaskResult.isSuccessful) {
//                        val listM = mutableListOf<Module>()
//                        taskDocument.collection(PATH_MODULES).get()
//                            .addOnCompleteListener { findModuleResult ->
//
//                                if (findModuleResult.isSuccessful) {
//                                    for (documentM in findModuleResult.result!!) {
//                                        Log.i(
//                                            "HAHA",
//                                            "a module document id is found: ${documentM.id}"
//                                        )
//                                        listM.add(documentM.toObject(Module::class.java))
//                                    }
//                                    val taskFound = Task(
//                                        accumCount = findTaskResult.result!!.data!!["accumCount"].toString().toInt(),
//                                        dueDate = findTaskResult.result!!.data!!["dueDate"].toString().toLong(),
//                                        goalCount = findTaskResult.result!!.data!!["goalCount"].toString().toInt(),
//                                        groupId = findTaskResult.result!!.data!!["groupId"].toString(),
//                                        taskCategoryId = findTaskResult.result!!.data!!["taskCategoryId"].toString().toInt(),
//                                        taskDone = findTaskResult.result!!.data!!["taskDone"].toString().toBoolean(),
//                                        taskId = findTaskResult.result!!.data!!["taskId"].toString(),
//                                        taskName = findTaskResult.result!!.data!!["taskName"].toString(),
//                                        todayDone = findTaskResult.result!!.data!!["todayDone"].toString().toBoolean(),
//                                        modules = listM,
//                                        userId = findTaskResult.result!!.data!!["userId"].toString()
//                                    )//document.toObject(TaskItem::class.java)
//                                    list.add(taskFound)
//                                    count += 1
//
//                                    Logger.d("taskId=$taskId, count=$count)}")
//                                    if (count == user.ongoingTasks.size) {
//
//                                        Logger.d("last gotcha")
//                                        continuation.resume(Result.Success(list))
//                                    }
//                                } else {
//                                    findModuleResult.exception?.let {
//                                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
//                                        continuation.resume(Result.Error(it))
//                                        return@addOnCompleteListener
//                                    }
//                                    continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
//                                }
//
//
//                            }
//
//
//
//                    } else {
//                        findTaskResult.exception?.let {
//                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
//                            continuation.resume(Result.Error(it))
//                            return@addOnCompleteListener
//                        }
//
//                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
//                    }
//
//                }
//            }
                }
        }

    override suspend fun getUser(): Result<User> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance().collection(PATH_USERS).document(PATH_USERS_DOCUMENT)
            .get()
            .addOnCompleteListener { findUserResult ->
                if (findUserResult.isSuccessful) {
                    val user = findUserResult.result!!.toObject(User::class.java)!!
                    continuation.resume(Result.Success(user))
                } else {
                    findUserResult.exception?.let {
                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                }


            }
    }

    override suspend fun getHistoryPoints(user: User): Result<List<History>> =
        suspendCoroutine { continuation ->
            val listH = mutableListOf<History>()
            FirebaseFirestore.getInstance().collection(PATH_TASKS)
                .whereIn("taskId", user.ongoingTasks)
                .get().addOnCompleteListener { findTaskResult ->
                    if (findTaskResult.isSuccessful) {
                        for (documentT in findTaskResult.result!!) {
                            FirebaseFirestore.getInstance().collection(PATH_TASKS)
                                .document(documentT.id).collection(PATH_HISTORY).get()
                                .addOnCompleteListener { findHistoryResult ->
                                    if (findHistoryResult.isSuccessful) {
                                        for (documentH in findHistoryResult.result!!) {
                                            listH.add(documentH.toObject(History::class.java))
                                        }
                                    }else {
                                        findHistoryResult.exception?.let {
                                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                                            return@addOnCompleteListener
                                        }
                                    }
                                }
                        }
                        continuation.resume(Result.Success(listH))
                    }else {
                        findTaskResult.exception?.let {
                            Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                    }
                }
        }
}