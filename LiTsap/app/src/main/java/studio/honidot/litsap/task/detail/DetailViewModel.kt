package studio.honidot.litsap.task.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.source.LiTsapRepository

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

    private fun retrieveModules(taskId: String) {
        coroutineScope.launch {
            val result = repository.getModules(taskId)
            _modules.value = when (result) {
                is Result.Success -> {
                    _workout.value = Workout(
                        arguments.taskName,
                        arguments.taskCategoryId,
                        result.data[0].moduleName,
                        arguments.userId,
                        arguments.taskId,
                        arguments.groupId,
                        0,
                        0,
                        false,
                        listOf("")
                    )
                    _awaitDrawModules.value = result.data
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

    private val _workout = MutableLiveData<Workout>()

    val workout: LiveData<Workout>
        get() = _workout

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        retrieveModules(_task.value!!.taskId)
    }

    fun changeModule(selectedPosition: Int) {
        _workout.value?.apply {
            moduleName = _modules.value!![selectedPosition].moduleName
        }
        _workout.value = _workout.value
    }

    fun onSetWorkoutTime(time: Int) {
        _workout.value?.apply {
            planSectionCount = time
        }
        _workout.value = _workout.value
    }

    val moduleDetailOpen = MutableLiveData<Boolean>().apply {
        value = true
    }

    val moduleStatusOpen = MutableLiveData<Boolean>().apply {
        value = true
    }

    fun clickModuleDetailArrow() {
        moduleDetailOpen.value?.let {
            moduleDetailOpen.value = !it
        }
    }

    fun clickModuleStatusArrow() {
        moduleStatusOpen.value?.let {
            moduleStatusOpen.value = !it
        }
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

    fun onPieDrew() {
        _awaitDrawModules.value = null
    }

    fun leaveDetail() {
        _leaveDetail.value = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}