package studio.honidot.litsap.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(
    var groupId: String = "",
    var groupCategoryId: Int = -1,
    @Exclude var members: List<Member>, //Include user herself, max 3 members in group
    var isFull: Boolean,
    var onlineCount: Int //Indicate how many members are currently doing the task
) : Parcelable