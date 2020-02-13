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
}