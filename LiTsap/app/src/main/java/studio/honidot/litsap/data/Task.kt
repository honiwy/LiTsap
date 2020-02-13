package studio.honidot.litsap.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize



@Parcelize
data class Task(
    var taskId: String = "",
    var groupId: String = "",
    var userId: String = "",
    var taskName: String = "",
    var goalCount: Int = -1,
    var accumCount: Int = -1,
    var taskCategoryId: Int = -1,
    var dueDate: Long = 0,
    var taskDone: Boolean = false, //whether a history task or not
    var todayDone: Boolean = false //whether finish the task today
) : Parcelable



