package studio.honidot.litsap.task.create

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskCreateViewModel : ViewModel() {

    var title = MutableLiveData<String>()
    var category = MutableLiveData<String>()

    fun createTask() {
        title.value?.let{taskTitle->
            category.value?.let{chosenCategory->
              Log.i("TaskCreate","Task title: ${title.value}, Task category: ${category.value}")
            }

        }
    }

}