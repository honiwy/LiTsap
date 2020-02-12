package studio.honidot.litsap.source.local

import android.content.Context
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.data.User
import studio.honidot.litsap.source.LiTsapDataSource

class LiTsapLocalDataSource(val context: Context) : LiTsapDataSource {
//    override  fun getProductsCollected(): LiveData<List<ProductCollected>> {
//        return StylishDatabase.getInstance(context).stylishDatabaseDao.getAllProductsCollected()
//    }
//

    override suspend fun getTasks(): Result<List<TaskItem>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUser(): Result<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}