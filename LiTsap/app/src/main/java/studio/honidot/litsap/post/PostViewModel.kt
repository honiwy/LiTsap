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
import studio.honidot.litsap.data.History
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.User
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import java.util.*

class PostViewModel(private val repository: LiTsapRepository, private val arguments: String) : ViewModel() {

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
            val date = DateFormat.format("d/M/yyyy", Date(Calendar.getInstance().timeInMillis)).toString()
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
                    val result = repository.getHistoryOnThatDay(twoList, dateSelected)
                    _records.value = when (result) {
                        is Result.Success -> {
                            Logger.d("history: ${result.data}")
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
        }
    }

}