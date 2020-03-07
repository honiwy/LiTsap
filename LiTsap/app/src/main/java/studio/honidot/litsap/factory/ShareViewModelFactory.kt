package studio.honidot.litsap.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import studio.honidot.litsap.data.Share
import studio.honidot.litsap.share.post.PostViewModel
import studio.honidot.litsap.source.LiTsapRepository

@Suppress("UNCHECKED_CAST")
class ShareViewModelFactory(
    private val liTsapRepository: LiTsapRepository,
    private val share: Share
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(PostViewModel::class.java) ->
                    PostViewModel(liTsapRepository, share)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}