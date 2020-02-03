package studio.honidot.litsap.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.task.TaskViewModel

class ViewModelFactory(
    private val liTsapRepository: LiTsapRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(liTsapRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}