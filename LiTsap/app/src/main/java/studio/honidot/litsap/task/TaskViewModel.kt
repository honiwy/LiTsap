package studio.honidot.litsap.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.data.User
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import studio.honidot.litsap.util.Util
import studio.honidot.litsap.util.Util.getString


class TaskViewModel(private val repository: LiTsapRepository, private val arguments: String) :
    ViewModel() {

    private val _user = repository.getUser(arguments)

    val user: LiveData<User>
        get() = _user

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _taskItems = MutableLiveData<List<TaskItem>>()

    val taskItems: LiveData<List<TaskItem>>
        get() = _taskItems

    private val _taskCount = MutableLiveData<Int>()

    val taskCount: LiveData<Int>
        get() = _taskCount

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error


    fun retrieveOngoingTasks(taskIdList: List<String>) {
        if (taskIdList.isEmpty()) {
            _taskItems.value =
                mutableListOf(TaskItem.Title(LiTsapApplication.instance.getString(R.string.task_create_one)))
            _taskCount.value = 0
        } else {
            coroutineScope.launch {
                _status.value = LoadApiStatus.LOADING
                val result = repository.getTasks(taskIdList)
                _taskItems.value = when (result) {
                    is Result.Success -> {
                        val resultArranged = Util.countTaskNumberAndAddHeader(result.data)
                        _taskCount.value = resultArranged.first
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                        resultArranged.second
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
                        _error.value = getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                        null
                    }
                }
            }
        }
    }


    fun deleteUserOngoingTask(userId: String, taskId: String) {
        coroutineScope.launch {
            when (repository.deleteUserOngoingTask(userId, taskId)) {
                is Result.Success -> {
                }
                is Result.Fail -> {
                }
                is Result.Error -> {
                }
                else -> {
                }
            }
        }
    }

    private val _longPressTaskItem = MutableLiveData<Task>()

    val longPressTaskItem: LiveData<Task>
        get() = _longPressTaskItem

    fun longPressTaskItem(task: Task) {
        _longPressTaskItem.value = task
    }

    fun onlongPressTaskItemFinish() {
        _longPressTaskItem.value = null
    }

    // Handle navigation to detail
    private val _navigateToDetail = MutableLiveData<Task>()

    val navigateToDetail: LiveData<Task>
        get() = _navigateToDetail

    fun navigateToDetail(task: Task) {
        _navigateToDetail.value = task
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}