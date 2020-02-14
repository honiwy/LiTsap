package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class History(
    var note: String = "",
    var achieveCount: Int = 0,
    var recordDate: Long = 0,
    var taskId: String = "",
    var taskName: String = ""
) : Parcelable