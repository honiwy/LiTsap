package studio.honidot.litsap.share.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Share
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.data.User
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.share.ShareTypeFilter
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Util

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

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    fun findUser(firebaseUser: FirebaseUser) {
        coroutineScope.launch {
            val result = repository.findUser(firebaseUser.uid)
            _user.value = when (result) {
                is Result.Success -> {
                    if(result.data?.shareTasks!!.isNotEmpty()){
                        retrieveUserSharingTasks(result.data.shareTasks)
                    }
                   result.data
                }
                is Result.Fail -> {
                    null
                }
                is Result.Error -> {
                    null
                }
                else -> {
                    null
                }
            }
        }
    }

    private fun retrieveUserSharingTasks(shareIdList: List<String>) {
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING
                val result = repository.getShares(shareIdList)
                _shareList.value = when (result) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                        result.data
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                        null
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                        null
                    }
                    else -> {
                        _error.value = Util.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                        null
                    }
                }
        }
    }


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