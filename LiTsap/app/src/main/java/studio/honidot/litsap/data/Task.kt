package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import studio.honidot.litsap.TaskCategory

@Parcelize
data class Task(
    val taskId: Long,
    val title: String,
    val category: TaskCategory
): Parcelable