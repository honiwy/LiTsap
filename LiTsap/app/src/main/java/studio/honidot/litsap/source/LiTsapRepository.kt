package studio.honidot.litsap.source

import androidx.lifecycle.LiveData
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.data.User

interface LiTsapRepository {
    //    fun getProductsCollected(): LiveData<List<ProductCollected>>
//
//    suspend fun isProductInCart(id: Long, colorCode: String, size: String): Boolean
    suspend fun getTasks(): Result<List<TaskItem>>

    suspend fun getUser(): Result<User>
}
