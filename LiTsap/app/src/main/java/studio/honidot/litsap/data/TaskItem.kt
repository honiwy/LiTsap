package studio.honidot.litsap.data

sealed class TaskItem {

    abstract val id: String

    data class Title(val title: String): TaskItem() {
        override val id: String = ""
    }
    data class Assignment(val task: Task): TaskItem() {
        override val id: String
            get() = task.taskId
    }
}