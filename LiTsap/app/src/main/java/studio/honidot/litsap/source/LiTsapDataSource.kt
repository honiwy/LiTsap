package studio.honidot.litsap.source

import androidx.lifecycle.LiveData
import studio.honidot.litsap.data.*

interface LiTsapDataSource {
    //    fun getProductsCollected(): LiveData<List<ProductCollected>>
//
    suspend fun getOngoingTaskList(user: User): Result<List<Task>>

    suspend fun getUser(): Result<User>

    suspend fun getHistoryPoints(user: User): Result<List<History>>
}