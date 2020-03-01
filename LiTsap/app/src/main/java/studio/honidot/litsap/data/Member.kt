package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Member(
    var groupId: String = "",
    var userId: String = "",
    var userName: String="",
    var taskId: String = "",
    var murmur: String = ""
) : Parcelable
