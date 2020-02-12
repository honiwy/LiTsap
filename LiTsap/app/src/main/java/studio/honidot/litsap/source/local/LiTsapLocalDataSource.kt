package studio.honidot.litsap.source.local

import android.content.Context
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapDataSource

class LiTsapLocalDataSource(val context: Context) : LiTsapDataSource {
//    override  fun getProductsCollected(): LiveData<List<ProductCollected>> {
//        return StylishDatabase.getInstance(context).stylishDatabaseDao.getAllProductsCollected()
//    }
//

    override suspend fun getOngoingTaskList(user: User): Result<List<Task>>  {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUser(): Result<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getHistoryPoints(user: User): Result<List<History>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}