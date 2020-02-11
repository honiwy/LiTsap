package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Member(
    var userId: String = "",
    var taskId: String = "",
    var memberMurmur: String = ""
) : Parcelable
