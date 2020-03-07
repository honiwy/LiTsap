package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Share(
    var shareId: String = "",
    var userId: String = "",
    var userName: String = "",
    var taskId: String = "",
    var taskName: String = "",
    var note: String = "",
    var imageUriList: List<String> = listOf()
) : Parcelable