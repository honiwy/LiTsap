package studio.honidot.litsap.data

import studio.honidot.litsap.TaskCategory

data class Task(
    val id: Long,
    val title: String,
    val category: TaskCategory,
    val interval: Double,
    val accumulatedTime: Double,
    val status: Boolean
)