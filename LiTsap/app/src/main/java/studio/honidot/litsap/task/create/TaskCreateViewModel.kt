package studio.honidot.litsap.task.create

import android.util.Log
import android.widget.Toast
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication.Companion.appContext
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.LiTsapApplication.Companion.db
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.User
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger


private const val PATH_USERS_DOCUMENT = "8ZoicZsGSucyU2niQ4nr"
class TaskCreateViewModel(private val repository: LiTsapRepository) : ViewModel() {

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

    var dueDate = MutableLiveData<Long>().apply {
        value = 1
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

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _taskId = MutableLiveData<String>()

    val taskId: LiveData<String>
        get() = _taskId

    private fun createTaskModules(taskId: String, modules: Module){
        coroutineScope.launch {
            val result = repository.createTaskModules(taskId,modules)
            when (result) {
                is Result.Success -> {
                    Logger.i("Modules update!")
                }
                else -> null
            }
        }
    }

    fun createTask() {
        coroutineScope.launch {
            val task = Task(
                userId = PATH_USERS_DOCUMENT,
                taskId = "",
                taskName = title.value ?: "無任務名稱",
                taskCategoryId = selectedTaskCategoryPosition.value ?: 5,
                accumCount = 0,
                goalCount = amount.value ?: 1,
                dueDate = dueDate.value ?: 1,
                groupId = "",
                todayDone = false,
                taskDone = false
            )
            val result = repository.createTask(task )
            _taskId.value = when (result) {
                is Result.Success -> {
                    moduleNameList.value!!.forEach {moduleName->
                        createTaskModules(result.data,Module(moduleName,0))
                    }
                    result.data
                }
                else -> {
                    null
                }
            }
        }
    }

    val selectedTaskCategoryPosition = MutableLiveData<Int>()


}