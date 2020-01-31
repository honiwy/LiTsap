package studio.honidot.litsap.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.task.workout.WorkoutViewModel

@Suppress("UNCHECKED_CAST")
class WorkoutViewModelFactory(
    private val workout: Workout
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(WorkoutViewModel::class.java) ->
                    WorkoutViewModel(workout)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
