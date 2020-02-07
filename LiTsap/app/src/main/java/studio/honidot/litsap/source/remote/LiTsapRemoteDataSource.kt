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
import java.util.ArrayList


object LiTsapRemoteDataSource : LiTsapDataSource {

    private const val PATH_USERS = "users"

    private const val PATH_TASKS = "tasks"
//    override fun getProductsCollected(): LiveData<List<ProductCollected>> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
    override suspend fun getTasks(): Result<List<TaskItem>> = suspendCoroutine { continuation ->
    val taskCollection = FirebaseFirestore.getInstance().collection(PATH_USERS).document("Rachel").collection(PATH_TASKS)

    taskCollection.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<TaskItem>()
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

                        val taskFound =  TaskItem.Assignment( FireTask(
                            taskId = document.data["id"].toString(),
                            title = document.data["title"].toString(),
                            categoryId = document.data["categoryId"].toString().toInt(),
                            modules = listM,
                            accumulatedCount = document.data["accumulatedCount"].toString().toInt(),
                            totalCount = document.data["totalCount"].toString().toInt(),
                            dueDate = document.data["dueDate"].toString(),
                            chatStatus = document.data["chatStatus"].toString().toBoolean(),
                            taskStatus = document.data["taskStatus"].toString().toBoolean()))//document.toObject(TaskItem::class.java)
                        list.add(taskFound)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Logger.w("[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
                    continuation.resume(Result.Fail(instance.getString(R.string.you_know_nothing)))
                }
            }
    }



//    val taskCollection = LiTsapApplication.db.collection("users").document("Rachel").collection("tasks")
//    val taskList = ArrayList<TaskItem>()
//    coroutineScope.launch {
//        taskCollection.get()
//            .addOnCompleteListener { thisTask ->
//                if (thisTask.isSuccessful) {
//                    for (document in thisTask.result!!) {
                        //val authorData = document.data["author"] as HashMap<*, *>
//                        val modules = ArrayList<Module>()
//                        taskCollection.document(document.id).collection("modules").get().addOnCompleteListener {
//                            if (thisTask.isSuccessful) {
//                                for (document in thisTask.result!!) {
//                                    modules.add(
//                                        Module(
//                                            name = document.data["name"].toString(),
//                                            progressCount = document.data["progressCount"].toString().toInt()
//                                        )
//                                    )
//                                }
//                            }
//                        }.await()
//                        taskList.add(
//                            TaskItem.Assignment( FireTask(
//                                taskId = document.data["id"].toString(),
//                                title = document.data["title"].toString(),
//                                categoryId = document.data["categoryId"].toString().toInt(),
//                                modules = modules.toList(),
//                                accumulatedCount = document.data["accumulatedCount"].toString().toInt(),
//                                totalCount = document.data["totalCount"].toString().toInt(),
//                                dueDate = document.data["dueDate"].toString(),
//                                chatStatus = document.data["chatStatus"].toString().toBoolean(),
//                                taskStatus = document.data["taskStatus"].toString().toBoolean()
//                            )
//                            ))
//
//                        Log.d("HAHA", document.id + " => " + document.data)
//                    }
//
//                } else {
//                    Log.w(
//                        "HAHA",
//                        "Error getting documents.",
//                        thisTask.exception
//                    )
//                }
//            }
//            .await()
//        _taskItems.value = taskList.toList()
//    }

}