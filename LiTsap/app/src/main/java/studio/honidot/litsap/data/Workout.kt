package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Workout(
    var taskName: String = "",
    var taskCategoryId: Int = -1,
    var moduleName: String = "",
    var userId: String = "",
    var taskId: String = "",
    var groupId: String = "",
    var planSectionCount: Int = 0,
    var achieveSectionCount: Int = 0,
    var chatWithGroup: Boolean = false,
    var recordInfo: List<String> = listOf()
) : Parcelable {
    val breakTimeConstant
        get() = 5//*60 //5 min
    val sectionConstant
        get() = 20 //*60//20 min
    val workoutTime
        get() = planSectionCount * sectionConstant.toLong()
    val displayProcess
        get() = planSectionCount * sectionConstant

}


