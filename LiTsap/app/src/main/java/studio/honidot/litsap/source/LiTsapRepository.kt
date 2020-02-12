package studio.honidot.litsap.source

import androidx.lifecycle.LiveData
import studio.honidot.litsap.data.*

interface LiTsapRepository {
    //    fun getProductsCollected(): LiveData<List<ProductCollected>>
//
//    suspend fun isProductInCart(id: Long, colorCode: String, size: String): Boolean
    suspend fun getOngoingTaskList(user: User): Result<List<Task>>

    suspend fun getUser(): Result<User>

    suspend fun getHistoryPoints(user: User): Result<List<History>>
}
