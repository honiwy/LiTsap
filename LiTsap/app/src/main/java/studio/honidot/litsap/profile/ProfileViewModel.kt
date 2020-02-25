package studio.honidot.litsap.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val BAR_CHART_DRAW_DAYS = 7

class ProfileViewModel(private val repository: LiTsapRepository, private val arguments: String) :
    ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private val _murmurs = MutableLiveData<List<Member>>()

    val murmurs: LiveData<List<Member>>
        get() = _murmurs

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _historyPoints = MutableLiveData<List<History>>()

    val historyPoints: LiveData<List<History>>
        get() = _historyPoints

    private val _taskTabs = MutableLiveData<List<TaskTab>>()

    val taskTabs: LiveData<List<TaskTab>>
        get() = _taskTabs

    init {
        findUser(arguments)
        getMurmur("6W3CzuYGO2vr5Qcj6YCf")
    }

//Rrhr7r7YidDWiwU0sSo5
    private fun getMurmur(groupId: String) {
        coroutineScope.launch {
            val result = repository.getMemberMurmurs(groupId)
            _murmurs.value = when (result) {
                is Result.Success -> {
                    Logger.d("murmur: ${result.data}")
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


    private fun findUser(firebaseUserId: String) {
        coroutineScope.launch {
            val result = repository.findUser(firebaseUserId)
            _user.value = when (result) {
                is Result.Success -> {
                    if (result.data!!.ongoingTasks.isNotEmpty()) {
                        retrieveHistoryPoints(result.data.ongoingTasks, BAR_CHART_DRAW_DAYS - 1)
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


    private fun queryTask(historyList:List<History>) {
        val sortedList = historyList.sortedBy { history -> history.taskName  }
        var tmpName = ""
        val tmpList = mutableListOf<TaskTab>()
        sortedList.forEach { history->
            if(history.taskName!= tmpName)
            {
                tmpList.add(TaskTab(history.taskId,history.taskName,false))
            }
            tmpName = history.taskName
        }
        tmpList[0].selected = true
        _taskTabs.value = tmpList
    }

    private fun retrieveHistoryPoints(taskIdList: List<String>, passNday: Int) {

        coroutineScope.launch {
            val result = repository.getHistory(taskIdList, passNday)
            _historyPoints.value = when (result) {
                is Result.Success -> {
                    Logger.d("history: ${result.data}")
                    queryTask(result.data)
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}