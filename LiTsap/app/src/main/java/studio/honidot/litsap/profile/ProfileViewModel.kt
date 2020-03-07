package studio.honidot.litsap.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.R
import studio.honidot.litsap.data.*
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Util

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

    private val _onGoingTasks = MutableLiveData<List<Task>>()

    val onGoingTasks: LiveData<List<Task>>
        get() = _onGoingTasks

    val editMurmur = MutableLiveData<String>().apply {
        value = ""
    }

    fun attemptEditMurmur() {
        _editing.value = true
    }

    private val _editing = MutableLiveData<Boolean>()

    val editing: LiveData<Boolean>
        get() = _editing

    fun sentEditMurmur() {
        _user.value?.let { thisUser ->
            _murmurs.value?.forEach {
                if (it.userId == thisUser.userId) {
                    it.murmur = editMurmur.value ?: ""
                    coroutineScope.launch {
                        when (repository.updateMurmur(it)) {
                            is Result.Success -> {
                                getMurmur(it.groupId)
                            }
                        }
                    }
                }
            }
        }
    }

    var selectedTaskPosition = MutableLiveData<Int>().apply {
        value = 0
    }

    init {
        findUser(arguments)
    }


    fun getMurmur(groupId: String) {
        coroutineScope.launch {
            val result = repository.getMemberMurmurs(groupId)
            _murmurs.value = when (result) {
                is Result.Success -> {
                    _editing.value = null
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

    private fun getGroupIdList(taskIdList: List<String>) {
        //find groupMurmur depend on group id
        coroutineScope.launch {
            val result = repository.getTasks(taskIdList)
            _onGoingTasks.value = when (result) {
                is Result.Success -> {
                    if (result.data.isNotEmpty()) {
                        getMurmur(result.data[0].groupId)
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

    private fun findUser(firebaseUserId: String) {
        coroutineScope.launch {
            val result = repository.findUser(firebaseUserId)
            _user.value = when (result) {
                is Result.Success -> {
                    result.data?.let {
                        val twoList = mutableListOf<String>()
                        if (it.ongoingTasks.isNotEmpty()) {
                            getGroupIdList(it.ongoingTasks)
                            twoList.addAll(it.ongoingTasks)
                        }
//                        if (it.historyTasks.isNotEmpty()) {
//                            twoList.addAll(it.historyTasks)
//                        }
                        if (twoList.isNotEmpty()) {
                            retrieveHistoryPoints(twoList, BAR_CHART_DRAW_DAYS - 1)
                        }
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

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private fun retrieveHistoryPoints(taskIdList: List<String>, passNday: Int) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            val result = repository.getHistory(taskIdList, passNday)
            _historyPoints.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    if (result.data.isNotEmpty()) {
                        result.data
                    } else {
                        createMockHistory(taskIdList)
                        _historyPoints.value
                    }
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

    private fun createMockHistory(taskIdList: List<String>) {
        coroutineScope.launch {
            val result = repository.getTasks(taskIdList)
            _historyPoints.value = when (result) {
                is Result.Success -> {
                    val tmpList = mutableListOf<History>()
                    result.data.forEach {
                        tmpList.add(History(taskId = it.taskId, taskName = it.taskName))
                    }
                    tmpList
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