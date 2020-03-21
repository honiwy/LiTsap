package studio.honidot.litsap.share.post

import android.graphics.Rect
import android.icu.util.Calendar
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Share
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import studio.honidot.litsap.util.Util

class PostViewModel(
    private val repository: LiTsapRepository,
    private val arguments: Share, val isSameUser: Boolean
) : ViewModel() {

    // Detail has product data from arguments
    private val _share = MutableLiveData<Share>().apply {
        value = arguments
    }

    val share: LiveData<Share>
        get() = _share

    val filePath = MutableLiveData<Uri>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _editing = MutableLiveData<Boolean>().apply { value = false }

    val editing: LiveData<Boolean>
        get() = _editing

    fun changeEditing() {
        _editing.value = _editing.value == false
    }

    // Handle leave detail
    private val _leavePost = MutableLiveData<Boolean>()

    val leavePost: LiveData<Boolean>
        get() = _leavePost


    fun leavePost() {
        _leavePost.value = true
    }

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    fun preUpdateSharePost() {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            filePath.value?.let {
                when (val result = repository.uploadImage(it)) {
                    is Result.Success -> {
                        _share.value?.imageUris = listOf(result.data.toString())
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value = Util.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            updateSharePost()
        }
    }


    private fun updateSharePost() {
        _share.value?.let {
            coroutineScope.launch {
                _editing.value = false
                it.recordDate = Calendar.getInstance().timeInMillis
                when (val result = repository.updateSharePost(it, filePath.value!=null)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value = Util.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }

        }
    }


    // it for gallery circles design
    private val _snapPosition = MutableLiveData<Int>()

    val snapPosition: LiveData<Int>
        get() = _snapPosition

    /**
     * When the gallery scroll, at the same time circles design will switch.
     */
    fun onGalleryScrollChange(
        layoutManager: RecyclerView.LayoutManager?,
        linearSnapHelper: LinearSnapHelper
    ) {
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let {
            layoutManager?.getPosition(snapView)?.let {
                if (it != snapPosition.value) {
                    _snapPosition.value = it
                }
            }
        }
    }

    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left = instance.resources.getDimensionPixelSize(R.dimen.space_post_circle)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
