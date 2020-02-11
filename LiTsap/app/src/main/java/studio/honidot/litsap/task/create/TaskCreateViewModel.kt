package studio.honidot.litsap.task.create

import android.util.Log
import android.widget.Toast
import androidx.databinding.InverseMethod
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.honidot.litsap.LiTsapApplication.Companion.appContext
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.LiTsapApplication.Companion.db
import studio.honidot.litsap.data.FireTask

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
        val tasksDocument = db.collection("users").document("Rachel").collection("tasks").document()
        tasksDocument.set(
            FireTask(
                taskId = tasksDocument.id,
                title = title.value ?: "無任務名稱",
                categoryId = selectedTaskCategoryPosition.value ?: 5,
                accumulatedCount = 0,
                totalCount = amount.value ?: 100,
                dueDate = dueDate.value ?: "沒有截止日期",
                chatStatus = false,
                taskStatus = false
            )
        )
        moduleNameList.value!!.forEach {
            tasksDocument.collection("modules").document().set(Module(it, 0)).addOnSuccessListener {
                Log.i("HAHA", "Success")
            }.addOnFailureListener {
                Log.i("HAHA", "Oh no")
            }
        }
        Toast.makeText(
            appContext,
            instance.getString(R.string.create_task_success),
            Toast.LENGTH_SHORT
        ).show()
    }

    val selectedTaskCategoryPosition = MutableLiveData<Int>()


}