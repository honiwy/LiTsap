package studio.honidot.litsap.task.finish

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.data.History
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger

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


    private val _workout = MutableLiveData<Workout>().apply {
        value = arguments
    }
    val workout: LiveData<Workout>
        get() = _workout


    private val _count = MutableLiveData<Int>().apply {
        value = 0
    }

    val count: LiveData<Int>
        get() = _count


    fun update(workout: Workout){
        updateTaskModule(workout)
        createTaskHistory(History(workout.note,workout.imageUri,workout.achieveSectionCount,Calendar.getInstance().timeInMillis,workout.taskId,workout.taskName))
        updateUserStatus(workout)
        updateTaskStatus(workout)
    }

    private fun updateTaskStatus(workout: Workout) {
        coroutineScope.launch {
            val result = repository.updateTaskStatus(workout)
            when (result) {
                is Result.Success -> {
                    _count.value = _count.value!!.plus(1)
                }
            }
        }
    }

    private fun updateUserStatus(workout: Workout) {
        coroutineScope.launch {
            val result = repository.updateUserStatus(workout)
            when (result) {
                is Result.Success -> {
                    _count.value = _count.value!!.plus(1)
                }
            }
        }
    }

    private fun updateTaskModule(workout: Workout) {
        coroutineScope.launch {
            val result = repository.updateTaskModule(workout)
            when (result) {
                is Result.Success -> {
                    _count.value = _count.value!!.plus(1)
                }
            }
        }
    }

    private fun createTaskHistory(history: History) {
        coroutineScope.launch {
            val result = repository.createTaskHistory(history)
            when (result) {
                is Result.Success -> {
                    _count.value = _count.value!!.plus(1)
                }
            }
        }
    }

}