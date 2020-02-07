package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FireTask(
    val taskId: String = "",
    val title: String = "",
    val categoryId: Int = -1,
    var modules: List<Module> = listOf(),
    val accumulatedCount: Int = -1,
    val totalCount: Int = -1,
    val dueDate: String = "",
    val chatStatus: Boolean = false, //whether somebody is in the chat room
    val taskStatus: Boolean = false //whether finish the task today
) : Parcelable