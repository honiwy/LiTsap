package studio.honidot.litsap.source.remote

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.FireTask
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.source.LiTsapDataSource
import studio.honidot.litsap.util.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.TaskItem


object LiTsapRemoteDataSource : LiTsapDataSource {

    private const val PATH_USERS = "users"

    private const val PATH_TASKS = "tasks"

    override suspend fun getTasks(): Result<List<TaskItem>> = suspendCoroutine { continuation ->
    val taskCollection = FirebaseFirestore.getInstance().collection(PATH_USERS).document("Rachel").collection(PATH_TASKS)

    taskCollection.orderBy("taskStatus").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<FireTask>()

                    for (document in task.result!!) {
                        Logger.d(document.id + " => " + document.data)

                        val listM = mutableListOf<Module>()
                        taskCollection.document(document.id).collection("modules").get().addOnCompleteListener {moduleTask->
                            if (moduleTask.isSuccessful) {
                                for (documentM in moduleTask.result!!) {

                                    val module = documentM.toObject(Module::class.java)
                                    listM.add(module)
                                }
                            }
                        }

                        val taskFound =  FireTask(
                            taskId = document.data["id"].toString(),
                            title = document.data["title"].toString(),
                            categoryId = document.data["categoryId"].toString().toInt(),
                            modules = listM,
                            accumulatedCount = document.data["accumulatedCount"].toString().toInt(),
                            totalCount = document.data["totalCount"].toString().toInt(),
                            dueDate = document.data["dueDate"].toString(),
                            chatStatus = document.data["chatStatus"].toString().toBoolean(),
                            taskStatus = document.data["taskStatus"].toString().toBoolean())//document.toObject(TaskItem::class.java)
                        list.add(taskFound)
                    }

                    fun transFireTasksToTaskItems(fireTasks: MutableList<FireTask>): List<TaskItem> {

                        val taskItems = mutableListOf<TaskItem>()
                        taskItems.add(TaskItem.Title("待完成"))

                        var lastStatus = false
                        fireTasks.forEach {
                            if (it.taskStatus != lastStatus) {
                                taskItems.add(TaskItem.Title("已完成"))
                            }
                            taskItems.add(TaskItem.Assignment(it))
                            lastStatus = it.taskStatus
                        }
                        return taskItems
                    }

                    continuation.resume(Result.Success(transFireTasksToTaskItems(list)))
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