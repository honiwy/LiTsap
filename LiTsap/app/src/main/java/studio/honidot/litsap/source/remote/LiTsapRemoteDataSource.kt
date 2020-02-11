package studio.honidot.litsap.source.remote

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
    val taskCollection = FirebaseFirestore.getInstance().collection(PATH_USERS).document(PATH_USERS_DOCUMENT).collection(PATH_TASKS)
    taskCollection.whereEqualTo("taskDone",false).orderBy("todayDone").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Task>()

                    for (document in task.result!!) {
                        Logger.d(document.id + " => " + document.data)

                        val listM = mutableListOf<Module>()
                        taskCollection.document(document.id).collection(PATH_MODULES).get().addOnCompleteListener {moduleTask->
                            if (moduleTask.isSuccessful) {
                                for (documentM in moduleTask.result!!) {

                                    val module = documentM.toObject(Module::class.java)
                                    listM.add(module)
                                }
                            }
                        }

                        val taskFound =  Task(
                            accumCount = document.data["accumCount"].toString().toInt(),
                            dueDate = document.data["dueDate"].toString().toLong(),
                            goalCount = document.data["goalCount"].toString().toInt(),
                            groupId = document.data["groupId"].toString(),
                            taskCategoryId = document.data["taskCategoryId"].toString().toInt(),
                            taskDone = document.data["taskDone"].toString().toBoolean(),
                            taskId = document.data["taskId"].toString(),
                            taskName = document.data["taskName"].toString(),
                            todayDone = document.data["todayDone"].toString().toBoolean(),
                            modules = listM,
                            userId = document.data["userId"].toString())//document.toObject(TaskItem::class.java)
                        list.add(taskFound)
                    }

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
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                }
            }
    }





}