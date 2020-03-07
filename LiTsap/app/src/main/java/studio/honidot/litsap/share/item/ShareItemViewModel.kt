package studio.honidot.litsap.share.item

import androidx.lifecycle.ViewModel
import studio.honidot.litsap.share.ShareTypeFilter
import studio.honidot.litsap.source.LiTsapRepository

class ShareItemViewModel(
    private val repository: LiTsapRepository,
    shareType: ShareTypeFilter // Handle the type for each catalog item
) : ViewModel() {
}