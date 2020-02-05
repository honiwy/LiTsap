package studio.honidot.litsap.task.create

import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import studio.honidot.litsap.LiTsapApplication.Companion.appContext
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.TaskCategory
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.TaskInfo

class TaskCreateViewModel : ViewModel() {

    var moduleNameList = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }

    var newModule = MutableLiveData<String>()


    fun addModule() {
        if (moduleNameList.value!!.size < 6) {
            newModule.value?.let {
                moduleNameList.value!!.add(it)
                moduleNameList.value = moduleNameList.value//Let observer detect the change
                newModule.value = ""
            }
        } else {
            Toast.makeText(
                appContext,
                instance.getString(R.string.plenty_module_info), Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun removeModule(module: String) {
        moduleNameList.value!!.remove(moduleNameList.value!!.findLast { it == module })
        moduleNameList.value = moduleNameList.value
    }

    val amount = MutableLiveData<Int>().apply {
        value = 1
    }

    var dueDate = MutableLiveData<String>().apply {
        value = "沒有截止日期"
    }

    fun increaseAmount() {
        amount.value = amount.value?.plus(1)
    }

    fun decreaseAmount() {
        amount.value = amount.value?.minus(1)
    }

    @InverseMethod("convertIntToString")
    fun convertStringToInt(value: String): Int {
        return try {
            value.toInt().let {
                when (it) {
                    0 -> 1
                    else -> it
                }
            }
        } catch (e: NumberFormatException) {
            1
        }
    }

    fun convertIntToString(value: Long): String {
        return value.toString()
    }

    var title = MutableLiveData<String>()

    fun createTask() {
        val moduleList = mutableListOf<Module>()
        moduleNameList.value!!.forEach {
            moduleList.add(Module(it, 0))
        }
        _newTaskInfo.value = TaskInfo(
            Task(
                123,
                title.value ?: "沒有名稱",
                taskCategory.value ?: TaskCategory.OTHER
            ),
            moduleList,
            0,
            amount.value ?: 100,
            dueDate.value?:"沒有截止日期",
            false,
            false
        )
        Log.i("HAHA", "TaskCreate, Task title: ${_newTaskInfo.value}")
    }


    private var _newTaskInfo = MutableLiveData<TaskInfo>()

    val selectedTaskCategoryPosition = MutableLiveData<Int>()

    val taskCategory: LiveData<TaskCategory> =
        Transformations.map(selectedTaskCategoryPosition) {
            TaskCategory.values()[it]
        }


}