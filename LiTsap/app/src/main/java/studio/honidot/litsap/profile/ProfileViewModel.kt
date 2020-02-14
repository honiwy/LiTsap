package studio.honidot.litsap.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val PATH_USERS_DOCUMENT = "8ZoicZsGSucyU2niQ4nr"
class ProfileViewModel(private val repository: LiTsapRepository) : ViewModel() {

    private val _user =  repository.getUser(PATH_USERS_DOCUMENT)

    val user: LiveData<User>
        get() = _user

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _tasks = MutableLiveData<List<String>>()

    val tasks: LiveData<List<String>>
        get() = _tasks

    fun retrieveOngoingTasks(taskIdList: List<String>) {
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
                else ->{
                    null
                }
            }
        }
    }

    private val _historyPoints = MutableLiveData<List<History>>()

    val historyPoints: LiveData<List<History>>
        get() = _historyPoints

    fun retrieveHistoryPoints(taskId: String,passNday:Long) {
        coroutineScope.launch {
            val result = repository.getHistory(taskId,passNday)
            _historyPoints.value = when (result) {
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