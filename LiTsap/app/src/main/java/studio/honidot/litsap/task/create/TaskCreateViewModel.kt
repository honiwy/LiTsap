package studio.honidot.litsap.task.create

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import studio.honidot.litsap.TaskCategory

class TaskCreateViewModel : ViewModel() {

    var title = MutableLiveData<String>()
    var category = MutableLiveData<String>()

    val selectedTaskCategoryPosition = MutableLiveData<Int>()

    private val taskCategory: LiveData<TaskCategory> = Transformations.map(selectedTaskCategoryPosition) {
        TaskCategory.values()[it]
    }

    fun createTask() {
        title.value?.let{taskTitle->
            selectedTaskCategoryPosition.value?.let{chosenCategory->
              Log.i("TaskCreate","Task title: $taskTitle, Task category: $chosenCategory")
            }

        }
    }

}