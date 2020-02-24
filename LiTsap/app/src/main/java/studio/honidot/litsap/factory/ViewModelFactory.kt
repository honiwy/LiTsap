package studio.honidot.litsap.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import studio.honidot.litsap.MainViewModel
import studio.honidot.litsap.login.LoginViewModel
import studio.honidot.litsap.post.PostViewModel
import studio.honidot.litsap.profile.ProfileViewModel
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.task.finish.FinishViewModel
import studio.honidot.litsap.task.TaskViewModel
import studio.honidot.litsap.task.create.TaskCreateViewModel

class ViewModelFactory(
    private val liTsapRepository: LiTsapRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(liTsapRepository)

                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(liTsapRepository)

                isAssignableFrom(TaskCreateViewModel::class.java) ->
                    TaskCreateViewModel(liTsapRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}