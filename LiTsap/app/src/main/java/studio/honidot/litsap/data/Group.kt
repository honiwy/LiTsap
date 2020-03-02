package studio.honidot.litsap.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(
    var groupId: String = "",
    var groupCategoryId: Int = -1,
    var groupFull: Boolean = false,
    var onlineCount: Int = 0//Indicate how many members are currently doing the task
) : Parcelable