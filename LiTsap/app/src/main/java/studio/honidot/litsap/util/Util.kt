package studio.honidot.litsap.util

import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
import studio.honidot.litsap.data.History
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.TaskItem

object Util {

    fun getString(resourceId: Int): String {
        return LiTsapApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return LiTsapApplication.instance.getColor(resourceId)
    }

    //ProfileViewModel
    fun sortAndCountTaskNumber(list: List<History>): Pair<List<String>, List<History>> {
        val sortedHistoryList = list.sortedBy { it.taskName }
        var name = ""
        val nameList = mutableListOf<String>()
        sortedHistoryList.forEach { history ->
            if (history.taskName != name) {
                nameList.add(history.taskName)
                name = history.taskName
            }
        }
        return Pair(nameList, sortedHistoryList)
    }

    //TaskViewModel
    fun countTaskNumberAndAddHeader(sortedTasks: List<Task>): Pair<Int, List<TaskItem>> {
        val taskItems = mutableListOf<TaskItem>()
        var lastStatus = false
        if (!sortedTasks[0].todayDone) {
            taskItems.add(TaskItem.Title(LiTsapApplication.instance.getString(R.string.await_todo)))
        }
        sortedTasks.forEach {
            if (it.todayDone != lastStatus) {
                taskItems.add(TaskItem.Title(LiTsapApplication.instance.getString(R.string.finished)))
            }
            taskItems.add(TaskItem.Assignment(it))
            lastStatus = it.todayDone
        }
        return Pair(sortedTasks.size, taskItems)
    }

}