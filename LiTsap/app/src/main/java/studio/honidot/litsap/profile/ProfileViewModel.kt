package studio.honidot.litsap.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger

private const val PATH_USERS_DOCUMENT = "8ZoicZsGSucyU2niQ4nr"
class ProfileViewModel(private val repository: LiTsapRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private fun retrieveUserInfo(userId:String) {
        coroutineScope.launch {
            val result = repository.getUser(userId)
            _user.value = when (result) {
                is Result.Success -> {
                    retrieveOngoingTasks(result.data.ongoingTasks)
                    retrieveHistoryPoints(result.data.ongoingTasks)
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

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        retrieveUserInfo(PATH_USERS_DOCUMENT)
    }

    private val _tasks = MutableLiveData<List<String>>()

    val tasks: LiveData<List<String>>
        get() = _tasks

    private fun retrieveOngoingTasks(taskIdList: List<String>) {
        coroutineScope.launch {
            val result = repository.getTasks(taskIdList)
            _tasks.value = when (result){
                is Result.Success->{
                    val tmpList = mutableListOf<String>()
                    result.data.forEach { task->
                        tmpList.add(task.taskName)
                    }
                     tmpList
                }
                is Result.Fail ->{
                    null
                }
                is Result.Error ->{
                    null
                }
                else ->{
                    null
                }
            }
        }
    }

    fun onPieDrew() {
        _tasks.value = null
    }

    private val _historyPoints = MutableLiveData<List<History>>()

    val historyPoints: LiveData<List<History>>
        get() = _historyPoints

    private fun retrieveHistoryPoints(taskIdList: List<String>) {
        coroutineScope.launch {
            val result = repository.getHistory(taskIdList)
            _historyPoints.value = when (result) {
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
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val calendarOpen = MutableLiveData<Boolean>().apply {
        value = true
    }
    val completeOpen = MutableLiveData<Boolean>().apply {
        value = true
    }

    fun clickCompleteArrow() {
        completeOpen.value?.let {
            completeOpen.value = !it
        }
    }

    fun clickCalendarArrow() {
        calendarOpen.value?.let {
            calendarOpen.value = !it
        }
    }












}