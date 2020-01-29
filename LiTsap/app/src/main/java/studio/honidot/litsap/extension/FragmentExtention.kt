package studio.honidot.litsap.extension

import studio.honidot.litsap.data.Task
import studio.honidot.litsap.factory.TaskViewModelFactory
import studio.honidot.litsap.factory.ViewModelFactory

fun getVmFactory(): ViewModelFactory {
    return ViewModelFactory()
}

fun getVmFactory(task: Task): TaskViewModelFactory {
    return TaskViewModelFactory(task)
}