package studio.honidot.litsap.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.source.LiTsapDataSource

class LiTsapLocalDataSource(val context: Context) : LiTsapDataSource {
//    override  fun getProductsCollected(): LiveData<List<ProductCollected>> {
//        return StylishDatabase.getInstance(context).stylishDatabaseDao.getAllProductsCollected()
//    }
//
//    override suspend fun isProductCart(id: Long, colorCode: String, size: String): Boolean {
//        return withContext(Dispatchers.IO) {
//            StylishDatabase.getInstance(context).stylishDatabaseDao.getCart(id, colorCode, size) != null
//        }
//    }
override suspend fun getTasks(): Result<List<TaskItem>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
}