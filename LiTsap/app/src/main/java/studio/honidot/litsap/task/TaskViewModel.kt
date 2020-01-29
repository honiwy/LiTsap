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
        res.add(TaskItem.Assignment(Task(0L, "我要成為海賊王", TaskCategory.NETWORKING, 3, 200, "",true,false)))
        res.add(TaskItem.Assignment(Task(1L, "韓文小高手", TaskCategory.STUDY, 2, 15, "當第一個完成的吧",false,false)))

        res.add(TaskItem.Title("已完成"))
        res.add(TaskItem.Assignment(Task(2L, "旅遊基金儲存計畫", TaskCategory.WEALTH, 5, 10, "",true,true)))
        res.add(TaskItem.Assignment(Task(3L, "舞蹈成發練習", TaskCategory.EXERCISE, 5, 30, "",false,true)))
        res.add(TaskItem.Assignment(Task(4L, "多益990", TaskCategory.STUDY, 1, 30, "HAHA",false,true)))
        res.add(TaskItem.Assignment(Task(5L, "Potluck料理準備", TaskCategory.FOOD, 4, 30, "",false,true)))

        _taskItems.value = res
    }

}