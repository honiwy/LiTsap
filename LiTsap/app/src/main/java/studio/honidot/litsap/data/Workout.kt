package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Workout(
    val task:Task,
    val selectedModule: Module,
    var workoutTime: Long,
    val chatWithGroup: Boolean,
    val message: List<String>
): Parcelable {
    val displayProcess
        get() = workoutTime.toInt()
}