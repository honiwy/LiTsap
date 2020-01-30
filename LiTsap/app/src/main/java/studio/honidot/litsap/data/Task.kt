package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import studio.honidot.litsap.TaskCategory

@Parcelize
data class Task(
    val id: Long,
    val title: String,
    val category: TaskCategory,
    val modules: List<Module>,
    val accumulatedCount: Int,
    val totalCount: Int,
    val phrase: String,
    val chatStatus: Boolean, //whether somebody is in the chat room
    val taskStatus: Boolean //whether finish the task today
):Parcelable

@Parcelize
data class Module(
    val name:String,
    val progressCount:Int
):Parcelable