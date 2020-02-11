package studio.honidot.litsap.extension

import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.data.FireTask
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.factory.TaskViewModelFactory
import studio.honidot.litsap.factory.ViewModelFactory
import studio.honidot.litsap.factory.WorkoutViewModelFactory

fun getVmFactory(): ViewModelFactory {
    val repository = (LiTsapApplication.appContext as LiTsapApplication).liTsapRepository
    return ViewModelFactory(repository)
}

fun getVmFactory(task: FireTask): TaskViewModelFactory {
    val repository = (LiTsapApplication.appContext as LiTsapApplication).liTsapRepository
    return TaskViewModelFactory(repository,task)
}

fun getVmFactory(workout: Workout): WorkoutViewModelFactory {
    val repository = (LiTsapApplication.appContext as LiTsapApplication).liTsapRepository
    return WorkoutViewModelFactory(repository,workout)
}