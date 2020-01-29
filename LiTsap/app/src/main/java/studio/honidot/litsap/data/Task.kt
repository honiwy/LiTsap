package studio.honidot.litsap.data

import studio.honidot.litsap.TaskCategory

data class Task(
    val id: Long,
    val title: String,
    val category: TaskCategory,
    val accumulatedCount: Int,
    val totalCount: Int,
    val phrase: String,
    val chatStatus: Boolean, //whether somebody is in the chat room
    val taskStatus: Boolean //whether finish the task today
)