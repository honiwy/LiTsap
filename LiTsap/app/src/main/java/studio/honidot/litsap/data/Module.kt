package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Module(
    val moduleName: String = "",
    val achieveSection: Int = -1
) : Parcelable