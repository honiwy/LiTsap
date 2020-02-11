package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Workout(
    var taskName: String = "",
    var taskCategoryId: Int = -1,
    var moduleName: String ="",
    var userId: String="",
    var taskId: String="",
    var groupId: String="",
    var planSectionCount: Int=0,
    var achieveSectionCount: Int=0,
    var chatWithGroup: Boolean=false,
    val recordInfo: List<String> =listOf()
): Parcelable {
    val workoutTime
        get() = planSectionCount*20*60L//20 min
    val displayProcess
        get() = workoutTime.toInt()
    val breakTime
        get() = 5*60L //5 min
}


