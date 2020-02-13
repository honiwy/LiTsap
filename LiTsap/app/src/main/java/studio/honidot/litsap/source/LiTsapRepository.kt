package studio.honidot.litsap.source

import androidx.lifecycle.LiveData
import studio.honidot.litsap.data.*

interface LiTsapRepository {
    //    fun getProductsCollected(): LiveData<List<ProductCollected>>
//
//    suspend fun isProductInCart(id: Long, colorCode: String, size: String): Boolean
    suspend fun getUser(userId : String): Result<User>

    suspend fun getTasks(taskIdList: List<String>): Result<List<Task>>

    suspend fun getHistory(taskIdList: List<String>): Result<List<History>>

    suspend fun getModules(taskId: String): Result<List<Module>>

    suspend fun createTask(task: Task): Result<String>
}
