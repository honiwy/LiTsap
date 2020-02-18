package studio.honidot.litsap.task.finish

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    // Detail has product data from arguments
    private val _workout = MutableLiveData<Workout>().apply {
        value = arguments
    }
    val workout: LiveData<Workout>
        get() = _workout

    fun updateTaskStatus(workout: Workout) {
        coroutineScope.launch {
            Logger.d("updateTaskStatus viewModel! userId: ${workout.userId}, achieve: ${workout.achieveSectionCount}")
            val result = repository.updateTaskStatus(workout)
            when (result) {
                is Result.Success -> {
                    updateUserStatus(workout)
                    Logger.d("You updateTaskStatus!!")
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

    fun updateUserStatus(workout: Workout) {
        coroutineScope.launch {
            Logger.d("updateUserStatus viewModel! userId: ${workout.userId}, achieve: ${workout.achieveSectionCount}")
            val result = repository.updateUserStatus(workout)
            when (result) {
                is Result.Success -> {
                    updateUserExperience(workout)
                    Logger.d("You updateUserStatus!!")
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

    fun updateUserExperience(workout: Workout) {
        coroutineScope.launch {
            Logger.d("updateUserExperience viewModel! userId: ${workout.userId}, achieve: ${workout.achieveSectionCount}")
            val result = repository.updateUserExperience(workout)
            when (result) {
                is Result.Success -> {
                    Logger.d("You updateUserStatus!!")
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


    fun navigateToProfile( workout: Workout) {
        updateTaskStatus(workout)
        _navigateToProfile.value = _workout.value
    }

    fun onProfileNavigated() {
        _navigateToProfile.value = null
    }

    private val _navigateToProfile = MutableLiveData<Workout>()

    val navigateToProfile: LiveData<Workout>
        get() = _navigateToProfile

}