package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class History(
    val note: String = "",
    val achieveCount: Int = 0,
    val recordDate: Long = 0,
    val taskId: String = ""
) : Parcelable