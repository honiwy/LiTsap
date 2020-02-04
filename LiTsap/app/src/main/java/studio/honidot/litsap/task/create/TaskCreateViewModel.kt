package studio.honidot.litsap.task.create

import android.util.Log
import androidx.databinding.InverseMethod
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

    val amount = MutableLiveData<Long>()

    fun increaseAmount() {
        amount.value = amount.value?.plus(1)
    }

    fun decreaseAmount() {
        amount.value = amount.value?.minus(1)
    }

    fun createTask() {
        title.value?.let{taskTitle->
            selectedTaskCategoryPosition.value?.let{chosenCategory->
              Log.i("TaskCreate","Task title: $taskTitle, Task category: $chosenCategory")
            }

        }
    }

    init{
        amount.value = 1
    }
    @InverseMethod("convertLongToString")
    fun convertStringToLong(value: String): Long {
        return try {
            value.toLong().let {
                when (it) {
                    0L -> 1
                    else -> it
                }
            }
        } catch (e: NumberFormatException) {
            1
        }
    }

    fun convertLongToString(value: Long): String {
        return value.toString()
    }
}