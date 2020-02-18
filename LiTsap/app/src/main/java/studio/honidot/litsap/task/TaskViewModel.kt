package studio.honidot.litsap.task

import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.data.User
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger


private const val PATH_USERS_DOCUMENT = "8ZoicZsGSucyU2niQ4nr"
class TaskViewModel(private val repository: LiTsapRepository) : ViewModel() {

    private val _user = repository.getUser(PATH_USERS_DOCUMENT)

    val user: LiveData<User>
        get() = _user

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _taskItems = MutableLiveData<List<TaskItem>>()

    val taskItems: LiveData<List<TaskItem>>
        get() = _taskItems

    private fun addHeader(sortedTasks: List<Task>): List<TaskItem> {
        val taskItems = mutableListOf<TaskItem>()
        if(sortedTasks.isNotEmpty()){
            taskItems.add(TaskItem.Title("待完成"))
            var lastStatus = false
            sortedTasks.forEach {
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

    fun retrieveOngoingTasks(taskIdList: List<String>) {
        coroutineScope.launch {
            val result = repository.getTasks(taskIdList)
            _taskItems.value = when (result) {
                is Result.Success -> {
                    addHeader(result.data)
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



    fun deleteUserOngoingTask(userId: String, taskId: String) {
        coroutineScope.launch {

            val result = repository.deleteUserOngoingTask(userId, taskId)
            when (result) {
                is Result.Success -> {
                    deleteTaskFromCollection(taskId)
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

    fun deleteTaskFromCollection(taskId: String) {
        coroutineScope.launch {
            val result = repository.deleteTask(taskId)
            when (result) {
                is Result.Success -> {
                    Logger.d("Delete success")
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

    private val _longPressTaskItem = MutableLiveData<Task>()

    val longPressTaskItem: LiveData<Task>
        get() = _longPressTaskItem

    fun longPressTaskItem (task: Task) {
        _longPressTaskItem.value = task
    }

    fun onlongPressTaskItemFinish () {
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