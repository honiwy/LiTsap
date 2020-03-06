package studio.honidot.litsap.extension

import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.factory.TaskViewModelFactory
import studio.honidot.litsap.factory.UserViewModelFactory
import studio.honidot.litsap.factory.ViewModelFactory
import studio.honidot.litsap.factory.WorkoutViewModelFactory

fun getVmFactory(): ViewModelFactory {
    val repository = (LiTsapApplication.appContext as LiTsapApplication).liTsapRepository
    return ViewModelFactory(repository)
}

fun getVmFactory(task: Task): TaskViewModelFactory {
    val repository = (LiTsapApplication.appContext as LiTsapApplication).liTsapRepository
    return TaskViewModelFactory(repository, task)
}

fun getVmFactory(workout: Workout): WorkoutViewModelFactory {
    val repository = (LiTsapApplication.appContext as LiTsapApplication).liTsapRepository
    return WorkoutViewModelFactory(repository, workout)
}

fun getVmFactory(userId: String): UserViewModelFactory {
    val repository = (LiTsapApplication.appContext as LiTsapApplication).liTsapRepository
    return UserViewModelFactory(repository, userId)
}