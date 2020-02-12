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

    override suspend fun getOngoingTaskList(user: User): Result<List<Task>> {
        return remoteDataSource.getOngoingTaskList(user)
    }

    override suspend fun getUser(): Result<User> {
        return remoteDataSource.getUser()
    }

    override suspend fun getHistoryPoints(user: User): Result<List<History>>{
        return remoteDataSource.getHistoryPoints(user)
    }
}