package studio.honidot.litsap.source

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.data.User

class DefaultLiTsapRepository(
    private val remoteDataSource: LiTsapDataSource,
    private val localDataSource: LiTsapDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LiTsapRepository {

    //    override  fun getProductsCollected(): LiveData<List<ProductCollected>> {
//        return stylishLocalDataSource.getProductsCollected()
//    }

    override suspend fun getTasks(): Result<List<TaskItem>> {
        return remoteDataSource.getTasks()
    }

    override suspend fun getUser(): Result<User> {
        return remoteDataSource.getUser()
    }
}