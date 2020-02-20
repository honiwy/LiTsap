package studio.honidot.litsap.source

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import studio.honidot.litsap.data.*

interface LiTsapRepository {

    suspend fun findUser(firebaseUserId: String): Result<User?>

    suspend fun createUser(user : User): Result<Boolean>

    fun getUser(userId : String): LiveData<User>

    suspend fun getTasks(taskIdList: List<String>): Result<List<Task>>

    suspend fun deleteUserOngoingTask(userId : String, taskId: String): Result<Boolean>

    suspend fun deleteTask(taskId: String): Result<Boolean>

    suspend fun getHistory(taskIdList: List<String>,passNday:Int): Result<List<History>>

    suspend fun getModules(taskId: String): Result<List<Module>>

    suspend fun createTask(task: Task): Result<String>

    suspend fun createTaskModules(taskId: String, modules: Module): Result<Boolean>

    suspend fun createFirstTaskHistory(taskId: String, history: History): Result<Boolean>

    suspend fun addUserOngoingList(userId: String, taskId: String): Result<Boolean>

    suspend fun updateTaskStatus(workout: Workout): Result<Boolean>

    suspend fun updateUserStatus(workout: Workout): Result<Boolean>

    suspend fun updateUserExperience(workout: Workout): Result<Boolean>
}
