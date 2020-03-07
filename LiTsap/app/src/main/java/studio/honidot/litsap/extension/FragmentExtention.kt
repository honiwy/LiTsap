package studio.honidot.litsap.extension

import androidx.fragment.app.Fragment
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.factory.*
import studio.honidot.litsap.share.ShareTypeFilter

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

fun Fragment.getVmFactory(shareType: ShareTypeFilter): ShareItemViewModelFactory {
    val repository = (requireContext().applicationContext as LiTsapApplication).liTsapRepository
    return ShareItemViewModelFactory(repository, shareType)
}