package studio.honidot.litsap.task.finish

import android.icu.util.Calendar
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
import studio.honidot.litsap.data.History
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Share
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import studio.honidot.litsap.util.Util

class FinishViewModel(
    private val repository: LiTsapRepository,
    private val arguments: Workout
) : ViewModel() {

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun copyFootprint() {
        _workout.value?.apply {
            note = ""
            recordInfo.forEach {
                note += (it + "\n")
            }
        }
        _workout.value = _workout.value
    }

    private val _workout = MutableLiveData<Workout>().apply {
        value = arguments
        if (arguments.recordInfo.isEmpty()) {
            arguments.recordInfo =
                listOf(LiTsapApplication.instance.getString(R.string.finish_no_footprint))
        }
    }
    val workout: LiveData<Workout>
        get() = _workout


    private val _count = MutableLiveData<Int>()

    val count: LiveData<Int>
        get() = _count

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    fun update(workout: Workout) {
        _status.value = LoadApiStatus.LOADING
        _count.value = 0
        uploadImage(workout)
        updateTaskModule(workout)
        updateUserStatus(workout)
        updateTaskStatus(workout.taskId, workout.achieveSectionCount.toLong())
        if(arguments.lastTime && arguments.achieveSectionCount == arguments.planSectionCount){
            deleteUserOngoingTask(workout.userId, workout.taskId)
            addHistoryTaskId(workout.userId, workout.taskId)
            Toast.makeText(LiTsapApplication.appContext,LiTsapApplication.appContext.getString(R.string.finish_congrats),Toast.LENGTH_SHORT).show()
        }
    }

    private fun createSharePost(workout: Workout){
        val newShare = Share(userId=workout.userId)
//        var shareId: String = "",
//        var userId: String = "",
//        var userName: String = "",
//        var taskId: String = "",
//        var taskCategoryId: Int = 0,
//        var taskName: String = "",
//        var note: String = "",
//        var recordDate: Long = 0,
//        var imageUris: List<String> = listOf()
    }

    private fun addHistoryTaskId(userId: String, taskId: String) {
        coroutineScope.launch {
            when (val result = repository.addUserHistoryList(userId, taskId)) {
                is Result.Success -> {
                    null
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

    private fun deleteUserOngoingTask(userId: String, taskId: String) {
        coroutineScope.launch {
            when (val result = repository.deleteUserOngoingTask(userId, taskId)) {
                is Result.Success -> {
                   null
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

    private fun updateTaskStatus(taskId: String, accumulationPoints: Long) {
        coroutineScope.launch {
            when (val result = repository.updateTaskStatus(taskId, accumulationPoints)) {
                is Result.Success -> {

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

    private fun updateUserStatus(workout: Workout) {
        coroutineScope.launch {
            when (val result = repository.updateUserStatus(workout)) {
                is Result.Success -> {

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

    private fun createTaskHistory(history: History) {
        coroutineScope.launch {
            when (val result = repository.createTaskHistory(history)) {
                is Result.Success -> {

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

    val filePath = MutableLiveData<Uri>()

    private fun updateTaskModule(workout: Workout) {
        coroutineScope.launch {
            when (val result = repository.updateTaskModule(workout)) {
                is Result.Success -> {

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

    private fun uploadImage(workout: Workout) {
        coroutineScope.launch {
            filePath.value?.let {
                when (val result = repository.uploadImage(it)) {
                    is Result.Success -> {
                        _workout.value!!.imageUri = result.data.toString()
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
            createTaskHistory(
                History(
                    workout.note,
                    if (filePath.value == null) "" else _workout.value!!.imageUri,
                    workout.achieveSectionCount,
                    Calendar.getInstance().timeInMillis,
                    workout.taskId,
                    workout.taskName
                )
            )
            _count.value?.let {
                _count.value = it.plus(1)
            }
        }
    }

    fun onTaskNavigated() {
        _error.value = null
        _status.value = LoadApiStatus.DONE
        _count.value = null
    }

}