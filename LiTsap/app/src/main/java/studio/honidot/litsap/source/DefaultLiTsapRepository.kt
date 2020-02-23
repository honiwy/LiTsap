package studio.honidot.litsap.source

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import studio.honidot.litsap.data.*

class DefaultLiTsapRepository(
    private val remoteDataSource: LiTsapDataSource,
    private val localDataSource: LiTsapDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LiTsapRepository {

    override suspend fun findUser(firebaseUserId: String): Result<User?> {
        return remoteDataSource.findUser(firebaseUserId)
    }

    override suspend fun createUser(user : User): Result<Boolean> {
        return remoteDataSource.createUser(user)
    }

    override fun getUser(userId: String): LiveData<User> {
        return remoteDataSource.getUser(userId)
    }

    override suspend fun getTasks(taskIdList: List<String>): Result<List<Task>> {
        return remoteDataSource.getTasks(taskIdList)
    }

    override suspend fun deleteUserOngoingTask(userId: String, taskId: String): Result<Boolean> {
        return remoteDataSource.deleteUserOngoingTask(userId, taskId)
    }

    override suspend fun deleteTask(taskId: String): Result<Boolean> {
        return remoteDataSource.deleteTask(taskId)
    }

    override suspend fun getHistory(
        taskIdList: List<String>,
        passNday: Int
    ): Result<List<History>> {
        return remoteDataSource.getHistory(taskIdList, passNday)
    }

    override suspend fun getModules(taskId: String): Result<List<Module>> {
        return remoteDataSource.getModules(taskId)
    }

    override suspend fun createTask(task: Task): Result<String> {
        return remoteDataSource.createTask(task)
    }

    override suspend fun createTaskModules(taskId: String, modules: Module): Result<Boolean> {
        return remoteDataSource.createTaskModules(taskId, modules)
    }

    override suspend fun createFirstTaskHistory(history: History): Result<Boolean> {
        return remoteDataSource.createFirstTaskHistory(history)
    }

    override suspend fun addUserOngoingList(userId: String, taskId: String): Result<Boolean> {
        return remoteDataSource.addUserOngoingList(userId, taskId)
    }

    override suspend fun updateTaskStatus(taskId: String, accumulationPoints: Long): Result<Boolean> {
        return remoteDataSource.updateTaskStatus(taskId,accumulationPoints)
    }

    override suspend fun updateUserStatus(workout: Workout): Result<Boolean> {
        return remoteDataSource.updateUserStatus(workout)
    }

    override suspend fun updateTaskModule(workout: Workout): Result<Boolean>{
        return remoteDataSource.updateTaskModule(workout)
    }

    override suspend fun createTaskHistory(history: History): Result<Boolean>{
        return remoteDataSource.createTaskHistory(history)
    }

    override suspend fun uploadImage(imageUri: Uri): Result<Uri>{
        return remoteDataSource.uploadImage(imageUri)
    }
}