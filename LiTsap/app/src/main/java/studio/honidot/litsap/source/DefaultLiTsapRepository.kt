package studio.honidot.litsap.source

import android.net.Uri
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import studio.honidot.litsap.data.*

class DefaultLiTsapRepository(
    private val remoteDataSource: LiTsapDataSource,
    private val localDataSource: LiTsapDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LiTsapRepository {

    override suspend fun addMemberToGroup(member: Member): Result<Boolean> {
        return remoteDataSource.addMemberToGroup(member)
    }

    override suspend fun createGroup(group: Group): Result<String> {
        return remoteDataSource.createGroup(group)
    }

    override suspend fun checkGroupFull(groupId: String): Result<Boolean> {
        return remoteDataSource.checkGroupFull(groupId)
    }

    override suspend fun findGroup(taskCategoryId: Int): Result<List<String>> {
        return remoteDataSource.findGroup(taskCategoryId)
    }

    override suspend fun updateUserIcon(userId: String, iconId: Int): Result<Boolean> {
        return remoteDataSource.updateUserIcon(userId, iconId)
    }

    override suspend fun findUser(firebaseUserId: String): Result<User?> {
        return remoteDataSource.findUser(firebaseUserId)
    }

    override suspend fun createUser(user: User): Result<Boolean> {
        return remoteDataSource.createUser(user)
    }

    override fun getUser(userId: String): LiveData<User> {
        return remoteDataSource.getUser(userId)
    }

    override suspend fun getTasks(taskIdList: List<String>): Result<List<Task>> {
        return remoteDataSource.getTasks(taskIdList)
    }

    override suspend fun getShares(shareIdList: List<String>): Result<List<Share>>{
        return remoteDataSource.getShares(shareIdList)
    }

    override suspend fun getAllShares(): Result<List<Share>>{
        return remoteDataSource.getAllShares()
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

    override suspend fun getMemberMurmurs(groupId: String): Result<List<Member>> {
        return remoteDataSource.getMemberMurmurs(groupId)
    }

    override suspend fun getHistoryOnThatDay(
        taskIdList: List<String>,
        dateString: String
    ): Result<List<History>> {
        return remoteDataSource.getHistoryOnThatDay(taskIdList, dateString)
    }

    override suspend fun getModules(taskId: String): Result<List<Module>> {
        return remoteDataSource.getModules(taskId)
    }

    override suspend fun createSharePost(share: Share): Result<Boolean>{
        return remoteDataSource.createSharePost(share)
    }

    override suspend fun createTask(task: Task): Result<String> {
        return remoteDataSource.createTask(task)
    }

    override suspend fun createTaskModules(taskId: String, modules: Module): Result<Boolean> {
        return remoteDataSource.createTaskModules(taskId, modules)
    }

    override suspend fun addUserOngoingList(userId: String, taskId: String): Result<Boolean> {
        return remoteDataSource.addUserOngoingList(userId, taskId)
    }

    override suspend fun addUserHistoryList(userId: String, taskId: String): Result<Boolean> {
        return remoteDataSource.addUserHistoryList(userId, taskId)
    }

    override suspend fun updateMurmur(member: Member): Result<Boolean> {
        return remoteDataSource.updateMurmur(member)
    }

    override suspend fun updateSharePost(share: Share): Result<Boolean>{
        return remoteDataSource.updateSharePost(share)
    }

    override suspend fun updateTaskStatus(
        taskId: String,
        accumulationPoints: Long
    ): Result<Boolean> {
        return remoteDataSource.updateTaskStatus(taskId, accumulationPoints)
    }

    override suspend fun updateUserStatus(workout: Workout): Result<Boolean> {
        return remoteDataSource.updateUserStatus(workout)
    }

    override suspend fun updateTaskModule(workout: Workout): Result<Boolean> {
        return remoteDataSource.updateTaskModule(workout)
    }

    override suspend fun createTaskHistory(history: History): Result<Boolean> {
        return remoteDataSource.createTaskHistory(history)
    }

    override suspend fun uploadImage(imageUri: Uri): Result<Uri> {
        return remoteDataSource.uploadImage(imageUri)
    }
}