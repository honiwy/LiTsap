package studio.honidot.litsap.task

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.TaskCategory
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.TaskItem

class TaskViewModel : ViewModel() {

    private val _taskItems = MutableLiveData<List<TaskItem>>()

    val taskItems: LiveData<List<TaskItem>>
        get() = _taskItems

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        getTasks()
    }

    private fun getTasks() {
        val res = mutableListOf<TaskItem>()
        res.add(TaskItem.Title("待執行"))
        res.add(TaskItem.Assignment(Task(1L,"我要成為海賊王",TaskCategory.NETWORKING,30.0,0.0,false)))
        res.add(TaskItem.Title("已完成"))
        res.add(TaskItem.Assignment(Task(2L,"旅遊基金儲存計畫",TaskCategory.WEALTH,80.0,10.0,false)))

        _taskItems.value = res
    }

}