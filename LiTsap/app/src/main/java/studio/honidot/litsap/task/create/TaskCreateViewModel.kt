package studio.honidot.litsap.task.create

import android.icu.util.Calendar
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.*
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import studio.honidot.litsap.util.Util


class TaskCreateViewModel(private val repository: LiTsapRepository) : ViewModel() {

    var moduleNameList = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }

    var newModule = MutableLiveData<String>().apply {
        value = ""
    }

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    fun findUser(firebaseUserId: String) {
        coroutineScope.launch {
            val result = repository.findUser(firebaseUserId)
            _user.value = when (result) {
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

    fun addModule() {
        moduleNameList.value?.let { list ->
            if (list.size < 6) {
                newModule.value?.let {
                    list.add(it.trim())
                    moduleNameList.value = moduleNameList.value//Let observer detect the change
                    newModule.value = ""
                }
            }
        }
    }



    fun removeModule(module: String) {
        moduleNameList.value?.let { list ->
            list.remove(list.findLast { it == module })
        }
        moduleNameList.value = moduleNameList.value
    }

    val amount = MutableLiveData<Int>().apply {
        value = 1
    }

    var dueDate = MutableLiveData<Long>().apply {
        value = Calendar.getInstance().timeInMillis
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

    var title = MutableLiveData<String>().apply {
        value = ""
    }

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _taskId = MutableLiveData<String>()

    val taskId: LiveData<String>
        get() = _taskId

    private val _count = MutableLiveData<Int>()

    val count: LiveData<Int>
        get() = _count

    private fun updateTaskIdList(userId: String, taskId: String) {
        coroutineScope.launch {
            when (val result = repository.addUserOngoingList(userId, taskId)) {
                is Result.Success -> {
                    Logger.i("Task ongoing list update!")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = Util.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _count.value?.let {
                _count.value = it.plus(1)
            }
        }
    }

    private fun createTaskModules(taskId: String, modules: Module) {
        coroutineScope.launch {
            when (val result = repository.createTaskModules(taskId, modules)) {
                is Result.Success -> {
                    Logger.i("Modules update!")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = Util.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    private fun createFirstTaskHistory(taskId: String) {
        coroutineScope.launch {
            val history = History(
                note = instance.getString(
                    R.string.create_first_history,
                    moduleNameList.value!!.size
                ),
                imageUri = "",
                achieveCount = 0,
                recordDate = Calendar.getInstance().timeInMillis,
                taskId = taskId,
                taskName = title.value ?: instance.getString(R.string.create_no_name)
            )
            when (val result = repository.createTaskHistory(history)) {
                is Result.Success -> {
                    Logger.i("History create!")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = Util.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _count.value?.let {
                _count.value = it.plus(1)
            }
        }
    }

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    fun create() {
        _status.value = LoadApiStatus.LOADING
        _count.value = 0
        findGroup(selectedTaskCategoryPosition.value ?: 6)
    }

    private fun createTask(groupId: String) {
        coroutineScope.launch {
            _user.value?.let { currentUser ->
                val task = Task(
                    userId = currentUser.userId,
                    taskId = "",
                    taskName = title.value ?: instance.getString(R.string.create_no_name),
                    taskCategoryId = selectedTaskCategoryPosition.value ?: 6,
                    accumCount = 0,
                    goalCount = amount.value ?: 1,
                    dueDate = dueDate.value ?: 1,
                    groupId = groupId,
                    todayDone = false,
                    taskDone = false
                )

                when (val result = repository.createTask(task)) {
                    is Result.Success -> {
                        moduleNameList.value?.let { list ->
                            list.forEach { moduleName ->
                                createTaskModules(result.data, Module(moduleName, "", 0))
                            }
                        }
                        createFirstTaskHistory(result.data)
                        updateTaskIdList(currentUser.userId, result.data)
                        addMemberToGroup(
                            Member(
                                groupId, currentUser.userId, currentUser.userName, result.data,
                                instance.getString(R.string.create_murmur)
                            )
                        )
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                        null
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                        null
                    }
                    else -> {
                        _error.value = Util.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                        null
                    }
                }
                _count.value?.let {
                    _count.value = it.plus(1)
                }
            }
        }
    }

    private fun findGroup(taskCategoryId: Int) {
        coroutineScope.launch {
            when (val result = repository.findGroup(taskCategoryId)) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        createGroup(Group("", taskCategoryId, false, 0))
                    } else {
                        createTask(result.data[0])
                    }
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = Util.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _count.value?.let {
                _count.value = it.plus(1)
            }
        }
    }

    private fun createGroup(group: Group) {
        coroutineScope.launch {
            when (val result = repository.createGroup(group)) {
                is Result.Success -> {
                    createTask(result.data)
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = Util.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    private fun checkGroupFull(groupId: String) {
        coroutineScope.launch {
            when (val result = repository.checkGroupFull(groupId)) {
                is Result.Success -> {
                    Logger.i("History create!")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = Util.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _count.value?.let {
                _count.value = it.plus(1)
            }
        }
    }

    private fun addMemberToGroup(member: Member) {
        coroutineScope.launch {
            when (val result = repository.addMemberToGroup(member)) {
                is Result.Success -> {
                    checkGroupFull(member.groupId)
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = Util.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _count.value?.let {
                _count.value = it.plus(1)
            }
        }
    }

    fun onTaskCreated() {
        _error.value = null
        _status.value = LoadApiStatus.DONE
        _count.value = null
    }

    val selectedTaskCategoryPosition = MutableLiveData<Int>()


}