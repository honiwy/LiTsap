package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    var taskId: String = "",
    var groupId: String = "",
    var userId: String = "",
    var taskName: String = "",
    var goalCount: Int = 0,
    var accumCount: Int = 0,
    var taskCategoryId: Int = 0,
    var dueDate: Long = 0,
    var taskDone: Boolean = false, //whether a history task or not
    var todayDone: Boolean = false //whether finish the task today
) : Parcelable



