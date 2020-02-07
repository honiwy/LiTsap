package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FireTask(
    val taskId: String,
    val title: String,
    val categoryId: Int,
    val accumulatedCount: Int,
    val totalCount: Int,
    val dueDate: String,
    val chatStatus: Boolean, //whether somebody is in the chat room
    val taskStatus: Boolean //whether finish the task today
): Parcelable