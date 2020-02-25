package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaskTab(
    var taskId: String = "",
    var taskName: String = ""
) : Parcelable
