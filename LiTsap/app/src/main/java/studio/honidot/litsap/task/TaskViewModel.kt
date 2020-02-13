package studio.honidot.litsap.task

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapRepository

private const val PATH_USERS_DOCUMENT = "8ZoicZsGSucyU2niQ4nr"
class TaskViewModel(private val repository: LiTsapRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private fun retrieveUserInfo(userId:String) {
        coroutineScope.launch {
            val result = repository.getUser(userId)
            _user.value = when (result) {
                is Result.Success -> {
                    retrieveOngoingTasks(result.data.ongoingTasks)
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

    private val _taskItems = MutableLiveData<List<TaskItem>>()

    val taskItems: LiveData<List<TaskItem>>
        get() = _taskItems

    private fun sortTasksAndAddHeader(unsortedTasks: List<Task>): List<TaskItem> {
        val taskItems = mutableListOf<TaskItem>()
        if(unsortedTasks.isNotEmpty()){
            val sortTasks = unsortedTasks.sortedBy { it.todayDone }
            taskItems.add(TaskItem.Title("待完成"))
            var lastStatus = false
            sortTasks.forEach {
                if (it.todayDone != lastStatus) {
                    taskItems.add(TaskItem.Title("已完成"))
                }
                taskItems.add(TaskItem.Assignment(it))
                lastStatus = it.todayDone
            }
        }else{
            taskItems.add(TaskItem.Title("快去新增任務吧!"))
        }
        return taskItems
    }

    private fun retrieveOngoingTasks(taskIdList: List<String>) {
        coroutineScope.launch {
            val result = repository.getTasks(taskIdList)
            _taskItems.value = when (result) {
                is Result.Success -> {
                    sortTasksAndAddHeader(result.data)
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