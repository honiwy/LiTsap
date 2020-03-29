package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Progress(
    var userId: String = "",
    var iconId: Int = 0,
    var percent: Float = 0.0f
) : Parcelable
