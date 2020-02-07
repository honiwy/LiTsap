package studio.honidot.litsap.source

import androidx.lifecycle.LiveData
import studio.honidot.litsap.data.FireTask
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.TaskItem

interface LiTsapDataSource {
    //    fun getProductsCollected(): LiveData<List<ProductCollected>>
//
    suspend fun getTasks(): Result<List<TaskItem>>
}