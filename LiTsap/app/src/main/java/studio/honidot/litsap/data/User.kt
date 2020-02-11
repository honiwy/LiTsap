package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var userId: String = "",
    var userName: String = "",
    var iconId: Int = 0,
    var experience: Long = 0,
    var ongoingTasks: List<String> = listOf(), //User ongoing task id list
    var historyTasks: List<String> = listOf() //User history task id list
) : Parcelable