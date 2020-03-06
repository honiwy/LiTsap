package studio.honidot.litsap.post

import android.icu.util.Calendar
import android.text.format.DateFormat
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
import studio.honidot.litsap.data.History
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.User
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import studio.honidot.litsap.util.Util
import java.util.*

class PostViewModel(private val repository: LiTsapRepository, private val arguments: String) :
    ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val layoutManager = CenterZoomLayoutManager(LiTsapApplication.appContext).apply {
        orientation = LinearLayoutManager.HORIZONTAL
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        findUser(arguments)
    }

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private fun findUser(firebaseUserId: String) {
        coroutineScope.launch {
            val result = repository.findUser(firebaseUserId)
            _user.value = when (result) {
                is Result.Success -> {
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
            val date =
                DateFormat.format(LiTsapApplication.instance.getString(R.string.post_select_date), Date(Calendar.getInstance().timeInMillis)).toString()
            getHistoryOnThatDay(date)
        }
    }

    private val _records = MutableLiveData<List<History>>()

    val records: LiveData<List<History>>
        get() = _records

    fun getHistoryOnThatDay(dateSelected: String) {
        _user.value?.let {
            val twoList = mutableListOf<String>()
            twoList.addAll(it.ongoingTasks)
            twoList.addAll(it.historyTasks)
            if (twoList.isNotEmpty()) {
                coroutineScope.launch {
                    _status.value = LoadApiStatus.LOADING
                    val result = repository.getHistoryOnThatDay(twoList, dateSelected)
                    _records.value = when (result) {
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
        }
    }
}

