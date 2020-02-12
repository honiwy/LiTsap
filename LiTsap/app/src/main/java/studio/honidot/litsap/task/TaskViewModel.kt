package studio.honidot.litsap.task

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.LiTsapApplication.Companion.db
import studio.honidot.litsap.TaskCategory
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapRepository
import java.util.ArrayList
import java.util.HashMap

class TaskViewModel(private val repository: LiTsapRepository) : ViewModel() {

    private val _taskItems = MutableLiveData<List<TaskItem>>()

    val taskItems: LiveData<List<TaskItem>>
        get() = _taskItems

    // Handle navigation to detail
    private val _navigateToDetail = MutableLiveData<Task>()

    val navigateToDetail: LiveData<Task>
        get() = _navigateToDetail

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        retrieveUserInfo()
    }

    fun navigateToDetail(task: Task) {
        _navigateToDetail.value = task
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    private fun transTasksToTaskItems(fireTasks: List<Task>): List<TaskItem> {
        val taskItems = mutableListOf<TaskItem>()
        taskItems.add(TaskItem.Title("待完成"))

        var lastStatus = false
        fireTasks.forEach {
            if (it.todayDone != lastStatus) {
                taskItems.add(TaskItem.Title("已完成"))
            }
            taskItems.add(TaskItem.Assignment(it))
            lastStatus = it.todayDone
        }
        return taskItems
    }

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    private fun retrieveUserInfo() {
        coroutineScope.launch {
            val result = repository.getUser()
            _user.value = when (result) {
                is Result.Success -> {
                    Log.i("HAHA","user data: ${result.data}")
                    retrieveTasks(result.data)
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

    private fun retrieveTasks(user: User) {
        coroutineScope.launch {
            val result = repository.getOngoingTaskList(user)
            _taskItems.value = when (result) {
                is Result.Success -> {
                    Log.i("HAHA","task data: ${result.data}")
                    val sortList = result.data.sortedBy {
                        it.todayDone
                    }
                    Log.i("HAHA","sorted task data: ${result.data}")
                    transTasksToTaskItems(sortList)
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