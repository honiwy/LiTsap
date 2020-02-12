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

    override suspend fun getTasks(): Result<List<TaskItem>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance().collection(PATH_USERS).document(PATH_USERS_DOCUMENT).get()
            .addOnCompleteListener { findUserResult ->
                if (findUserResult.isSuccessful) {
                    val list = mutableListOf<Task>()
                    findUserResult.result!!.toObject(User::class.java)!!.ongoingTasks.forEach { taskId ->

                        Log.i("HAHA", "task id: $taskId")
                        val taskDocument =
                            FirebaseFirestore.getInstance().collection(PATH_TASKS).document(taskId)
                        taskDocument.get().addOnCompleteListener { findTaskResult ->

                            if (findTaskResult.isSuccessful) {

                                val listM = mutableListOf<Module>()
                                taskDocument.collection(PATH_MODULES).get()
                                    .addOnCompleteListener { findModuleResult ->

                                        if (findModuleResult.isSuccessful) {
                                            for (documentM in findModuleResult.result!!) {
                                                Log.i(
                                                    "HAHA",
                                                    "a document id is found: ${documentM.id}"
                                                )
                                                listM.add(documentM.toObject(Module::class.java))
                                            }
                                        } else {
                                            findUserResult.exception?.let {
                                                Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                                                continuation.resume(Result.Error(it))
                                                return@addOnCompleteListener
                                            }
                                            continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                                        }
                                    }
                                val taskFound = Task(
                                    accumCount = findTaskResult.result!!.data!!["accumCount"].toString().toInt(),
                                    dueDate = findTaskResult.result!!.data!!["dueDate"].toString().toLong(),
                                    goalCount = findTaskResult.result!!.data!!["goalCount"].toString().toInt(),
                                    groupId = findTaskResult.result!!.data!!["groupId"].toString(),
                                    taskCategoryId = findTaskResult.result!!.data!!["taskCategoryId"].toString().toInt(),
                                    taskDone = findTaskResult.result!!.data!!["taskDone"].toString().toBoolean(),
                                    taskId = findTaskResult.result!!.data!!["taskId"].toString(),
                                    taskName = findTaskResult.result!!.data!!["taskName"].toString(),
                                    todayDone = findTaskResult.result!!.data!!["todayDone"].toString().toBoolean(),
                                    modules = listM,
                                    userId = findTaskResult.result!!.data!!["userId"].toString()
                                )//document.toObject(TaskItem::class.java)
                                list.add(taskFound)
                                fun transTasksToTaskItems(fireTasks: MutableList<Task>): List<TaskItem> {
                                    val taskItems = mutableListOf<TaskItem>()
                                    taskItems.add(TaskItem.Title("待完成"))

                                    var lastStatus = false
                                    fireTasks.forEach {
                                        if (it.todayDone != lastStatus) {
                                            taskItems.add(TaskItem.Title("已完成"))
                                        }
                                        taskItems.add(TaskItem.Assignment(it))
                                        lastStatus = it.todayDone
                                    }
                                    return taskItems
                                }
                                continuation.resume(Result.Success(transTasksToTaskItems(list)))
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

    override suspend fun getUser(): Result<User> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance().collection(PATH_USERS).document(PATH_USERS_DOCUMENT).get()
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
}