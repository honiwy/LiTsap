package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Workout(
    var todayDone: Boolean = false,
    var lastTime: Boolean = false,
    var taskName: String = "",
    var taskCategoryId: Int = -1,
    var moduleName: String = "",
    var moduleId: String = "",
    var userId: String = "",
    var taskId: String = "",
    var groupId: String = "",
    var planSectionCount: Int = 0,
    var achieveSectionCount: Int = 0,
    var chatWithGroup: Boolean = false,
    var recordInfo: List<String> = listOf(),
    var note: String = "",
    var imageUri: String = ""
) : Parcelable {
    companion object {
        private const val TIME_UNIT = 1 // min:60, sec:1
        const val BREAK_TIME = 5 * TIME_UNIT
        const val WORKOUT_TIME = 4 * TIME_UNIT
    }

    val displayProcess
        get() = planSectionCount * WORKOUT_TIME
    val workoutTime
        get() = displayProcess.toLong()
}


