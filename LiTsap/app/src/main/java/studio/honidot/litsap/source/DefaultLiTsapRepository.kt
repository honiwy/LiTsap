package studio.honidot.litsap.source

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import studio.honidot.litsap.data.*

class DefaultLiTsapRepository(
    private val remoteDataSource: LiTsapDataSource,
    private val localDataSource: LiTsapDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LiTsapRepository {

    //    override  fun getProductsCollected(): LiveData<List<ProductCollected>> {
//        return stylishLocalDataSource.getProductsCollected()
//    }
    override suspend fun getUser(userId : String): Result<User> {
        return remoteDataSource.getUser(userId)
    }

    override suspend fun getTasks(taskIdList: List<String>): Result<List<Task>> {
        return remoteDataSource.getTasks(taskIdList)
    }

    override suspend fun getHistory(taskIdList: List<String>): Result<List<History>>{
        return remoteDataSource.getHistory(taskIdList)
    }

    override suspend fun getModules(taskId: String): Result<List<Module>>{
        return remoteDataSource.getModules(taskId)
    }

    override suspend fun createTask(task: Task): Result<String>{
        return remoteDataSource.createTask(task)
    }
}