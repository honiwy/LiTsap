package studio.honidot.litsap.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapDataSource

class LiTsapLocalDataSource(val context: Context) : LiTsapDataSource {

    override fun getUser(userId: String): LiveData<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getTasks(taskIdList: List<String>): Result<List<Task>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override suspend fun getHistory(taskIdList: List<String>,passNday:Int): Result<List<History>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getModules(taskId: String): Result<List<Module>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun createTask(task: Task): Result<String>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun createTaskModules(taskId: String, modules: Module): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun createFirstTaskHistory(taskId: String, history: History): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun addUserOngoingList(userId: String, taskId: String): Result<Boolean>{
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}