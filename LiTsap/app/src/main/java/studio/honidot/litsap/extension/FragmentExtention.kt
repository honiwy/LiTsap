package studio.honidot.litsap.extension

import studio.honidot.litsap.data.TaskInfo
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.factory.TaskViewModelFactory
import studio.honidot.litsap.factory.ViewModelFactory
import studio.honidot.litsap.factory.WorkoutViewModelFactory

fun getVmFactory(): ViewModelFactory {
    return ViewModelFactory()
}

fun getVmFactory(task: TaskInfo): TaskViewModelFactory {
    return TaskViewModelFactory(task)
}

fun getVmFactory(workout: Workout): WorkoutViewModelFactory {
    return WorkoutViewModelFactory(workout)
}