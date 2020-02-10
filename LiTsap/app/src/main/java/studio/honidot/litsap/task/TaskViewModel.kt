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
    private val _navigateToDetail = MutableLiveData<FireTask>()

    val navigateToDetail: LiveData<FireTask>
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
        retrieveTasks()
    }

    fun navigateToDetail(task: FireTask) {
        _navigateToDetail.value = task
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }



    private fun retrieveTasks(){
        coroutineScope.launch {
            val result = repository.getTasks()
            _taskItems.value =  when (result) {
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

}