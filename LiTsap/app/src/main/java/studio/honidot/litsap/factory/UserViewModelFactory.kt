package studio.honidot.litsap.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import studio.honidot.litsap.diary.DiaryViewModel
import studio.honidot.litsap.profile.ProfileViewModel
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.task.TaskViewModel

class UserViewModelFactory(
    private val liTsapRepository: LiTsapRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(TaskViewModel::class.java) ->
                    TaskViewModel(liTsapRepository, userId)

                isAssignableFrom(DiaryViewModel::class.java) ->
                    DiaryViewModel(liTsapRepository, userId)

                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(liTsapRepository, userId)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}