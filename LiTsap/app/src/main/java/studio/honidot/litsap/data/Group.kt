package studio.honidot.litsap.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(
    var groupId: String = "",
    var groupCategoryId: Int = -1,
    var isFull: Boolean,
    var onlineCount: Int //Indicate how many members are currently doing the task
) : Parcelable