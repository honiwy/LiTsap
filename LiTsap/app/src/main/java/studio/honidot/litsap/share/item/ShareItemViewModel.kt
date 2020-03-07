package studio.honidot.litsap.share.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import studio.honidot.litsap.data.Share
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.share.ShareTypeFilter
import studio.honidot.litsap.source.LiTsapRepository

class ShareItemViewModel(
    private val repository: LiTsapRepository,
    shareType: ShareTypeFilter // Handle the type for each catalog item
) : ViewModel() {

    private val _shareList = MutableLiveData<List<Share>>()

    val shareList: LiveData<List<Share>>
        get() = _shareList


    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // Handle navigation to detail
    private val _navigateToPost = MutableLiveData<Share>()

    val navigateToPost: LiveData<Share>
        get() = _navigateToPost

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun navigateToPost(share:Share) {
        _navigateToPost.value = share
    }

    fun onPostNavigated() {
        _navigateToPost.value = null
    }
}