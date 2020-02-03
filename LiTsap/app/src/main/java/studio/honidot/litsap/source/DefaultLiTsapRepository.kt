package studio.honidot.litsap.source

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultLiTsapRepository(private val liTsapRemoteDataSource: LiTsapDataSource,
                               private val liTsapLocalDataSource: LiTsapDataSource,
                               private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LiTsapRepository {

//    override  fun getProductsCollected(): LiveData<List<ProductCollected>> {
//        return stylishLocalDataSource.getProductsCollected()
//    }
//
//    override suspend fun isProductInCart(id: Long, colorCode: String, size: String): Boolean {
//        return stylishLocalDataSource.isProductCart(id, colorCode, size)
//    }
}