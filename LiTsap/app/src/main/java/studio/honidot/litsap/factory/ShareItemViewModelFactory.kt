package studio.honidot.litsap.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import studio.honidot.litsap.share.ShareTypeFilter
import studio.honidot.litsap.share.item.ShareItemViewModel
import studio.honidot.litsap.source.LiTsapRepository

@Suppress("UNCHECKED_CAST")
class ShareItemViewModelFactory(
    private val repository: LiTsapRepository,
    private val shareType: ShareTypeFilter
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(ShareItemViewModel::class.java) ->
                    ShareItemViewModel(repository, shareType)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}