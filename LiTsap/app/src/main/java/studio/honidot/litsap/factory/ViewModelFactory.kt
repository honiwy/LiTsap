package studio.honidot.litsap.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import studio.honidot.litsap.MainViewModel
import studio.honidot.litsap.login.LoginViewModel
import studio.honidot.litsap.profile.face.FaceChooseViewModel
import studio.honidot.litsap.share.ShareViewModel
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.task.create.TaskCreateViewModel

class ViewModelFactory(
    private val liTsapRepository: LiTsapRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ShareViewModel::class.java) ->
                    ShareViewModel(liTsapRepository)

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(liTsapRepository)

                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(liTsapRepository)

                isAssignableFrom(TaskCreateViewModel::class.java) ->
                    TaskCreateViewModel(liTsapRepository)

                isAssignableFrom(FaceChooseViewModel::class.java) ->
                    FaceChooseViewModel(liTsapRepository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}