package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var userId: String = "",
    var loginVia: String = "",
    var userName: String = "",
    var iconId: Int = 0,
    var experience: Long = 0,
    var ongoingTasks: List<String> = listOf(), //User ongoing task id list
    var historyTasks: List<String> = listOf(), //User history task id list
    var todayDoneCount: Int = 0
) : Parcelable{
    val intervalConstant
        get() = 10
    val level
        get() = (experience/intervalConstant)
    val levelProcess
        get() = (experience%intervalConstant).toInt()
}