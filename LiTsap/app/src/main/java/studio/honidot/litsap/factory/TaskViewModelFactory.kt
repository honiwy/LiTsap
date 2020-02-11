package studio.honidot.litsap.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import studio.honidot.litsap.data.FireTask
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.task.detail.DetailViewModel

@Suppress("UNCHECKED_CAST")
class TaskViewModelFactory(
    private val liTsapRepository: LiTsapRepository,
    private val taskInfo: FireTask
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DetailViewModel::class.java) ->
                    DetailViewModel(liTsapRepository,taskInfo)


                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
