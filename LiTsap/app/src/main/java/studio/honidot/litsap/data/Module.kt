package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Module(
    var moduleName: String = "",
    var moduleId: String = "",
    var achieveSection: Int = -1
) : Parcelable