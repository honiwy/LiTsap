package studio.honidot.litsap.task.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.R
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Util

class DetailViewModel(
    private val repository: LiTsapRepository,
    private val arguments: Task
) : ViewModel() {

    // Detail has product data from arguments
    private val _task = MutableLiveData<Task>().apply {
        value = arguments
    }

    val task: LiveData<Task>
        get() = _task

    private val _modules = MutableLiveData<List<Module>>()

    val modules: LiveData<List<Module>>
        get() = _modules

    private val _awaitDrawModules = MutableLiveData<List<Module>>()

    val awaitDrawModules: LiveData<List<Module>>
        get() = _awaitDrawModules

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private fun retrieveModules(taskId: String) {
        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            val result = repository.getModules(taskId)
            _modules.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    _workout.value = Workout(
                        todayDone = arguments.todayDone,
                        taskName = arguments.taskName,
                        moduleName = result.data[0].moduleName,
                        moduleId = result.data[0].moduleId,
                        taskCategoryId = arguments.taskCategoryId,
                        userId = arguments.userId,
                        taskId = arguments.taskId,
                        groupId = arguments.groupId
                    )
                    _awaitDrawModules.value = result.data
                    result.data
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

    private val _workout = MutableLiveData<Workout>()

    val workout: LiveData<Workout>
        get() = _workout

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        retrieveModules(arguments.taskId)
    }

    fun changeModule(selectedPosition: Int) {
        _modules.value?.let{
            _workout.value?.apply {
                moduleName = it[selectedPosition].moduleName
                moduleId = it[selectedPosition].moduleId
            }
            _workout.value = _workout.value
        }
    }

    fun onSetWorkoutTime(time: Int) {
        _workout.value?.apply {
            planSectionCount = time
            lastTime = (time+arguments.accumCount) == arguments.goalCount
        }
        _workout.value = _workout.value
    }


    // Handle leave detail
    private val _leaveDetail = MutableLiveData<Boolean>()

    val leaveDetail: LiveData<Boolean>
        get() = _leaveDetail

    private val _navigateToWorkout = MutableLiveData<Workout>()

    val navigateToWorkout: LiveData<Workout>
        get() = _navigateToWorkout

    fun navigateToWorkout(workout: Workout) {
        _navigateToWorkout.value = workout
    }

    fun onWorkoutNavigated() {
        _navigateToWorkout.value = null
    }

    fun leaveDetail() {
        _leaveDetail.value = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}