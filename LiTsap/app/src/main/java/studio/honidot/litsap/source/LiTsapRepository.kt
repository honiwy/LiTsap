package studio.honidot.litsap.source

import androidx.lifecycle.LiveData
import studio.honidot.litsap.data.*

interface LiTsapRepository {

    fun getUser(userId : String): LiveData<User>

    suspend fun getTasks(taskIdList: List<String>): Result<List<Task>>

    suspend fun getHistory(taskIdList: List<String>,passNday:Long): Result<List<History>>

    suspend fun getModules(taskId: String): Result<List<Module>>

    suspend fun createTask(task: Task): Result<String>

    suspend fun createTaskModules(taskId: String, modules: Module): Result<Boolean>

    suspend fun createFirstTaskHistory(taskId: String, history: History): Result<Boolean>

    suspend fun addUserOngoingList(userId: String, taskId: String): Result<Boolean>
}
