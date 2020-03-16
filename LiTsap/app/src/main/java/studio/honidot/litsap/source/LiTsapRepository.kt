package studio.honidot.litsap.source

import android.net.Uri
import androidx.lifecycle.LiveData
import studio.honidot.litsap.data.*

interface LiTsapRepository {

    suspend fun eraseTaskDone(taskId: String): Result<Boolean>

    suspend fun eraseTodayDoneCount(userId: String): Result<Boolean>

    suspend fun addMemberToGroup(member: Member): Result<Boolean>

    suspend fun createGroup(group: Group): Result<String>

    suspend fun findGroup(taskCategoryId: Int): Result<List<String>>

    suspend fun checkGroupFull(groupId: String): Result<Boolean>

    suspend fun updateUserIcon(userId: String, iconId: Int): Result<Boolean>

    suspend fun findUser(firebaseUserId: String): Result<User?>

    suspend fun createUser(user: User): Result<Boolean>

    fun getUser(userId: String): LiveData<User>

    suspend fun getTasks(taskIdList: List<String>): Result<List<Task>>

    suspend fun getShares(shareIdList: List<String>): Result<List<Share>>

    suspend fun getAllShares(): Result<List<Share>>

    suspend fun deleteUserOngoingTask(userId: String, taskId: String): Result<Boolean>

    suspend fun deleteTask(taskId: String): Result<Boolean>

    suspend fun getHistory(taskIdList: List<String>, passNday: Int): Result<List<History>>

    suspend fun getMemberMurmurs(groupId: String): Result<List<Member>>

    suspend fun getHistoryOnThatDay(
        taskIdList: List<String>,
        dateString: String
    ): Result<List<History>>

    suspend fun getModules(taskId: String): Result<List<Module>>

    suspend fun createSharePost(share: Share): Result<Boolean>

    suspend fun createTask(task: Task): Result<String>

    suspend fun createTaskModules(taskId: String, modules: Module): Result<Boolean>

    suspend fun addUserOngoingList(userId: String, taskId: String): Result<Boolean>

    suspend fun addUserHistoryList(userId: String, taskId: String): Result<Boolean>

    suspend fun updateTaskStatus(taskId: String, accumulationPoints: Long): Result<Boolean>

    suspend fun updateMurmur(member: Member): Result<Boolean>

    suspend fun updateSharePost(share: Share): Result<Boolean>

    suspend fun updateUserStatus(workout: Workout): Result<Boolean>

    suspend fun updateTaskModule(workout: Workout): Result<Boolean>

    suspend fun createTaskHistory(history: History): Result<Boolean>

    suspend fun uploadImage(imageUri: Uri): Result<Uri>
}
