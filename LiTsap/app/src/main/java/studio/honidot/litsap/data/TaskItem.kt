package studio.honidot.litsap.data

sealed class TaskItem {

    abstract val id: Long

    data class Title(val title: String): TaskItem() {
        override val id: Long = -1
    }
    data class Assignment(val task: Task): TaskItem() {
        override val id: Long
            get() = task.id
    }
}