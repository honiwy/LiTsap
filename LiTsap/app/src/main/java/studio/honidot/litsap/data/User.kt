package studio.honidot.litsap.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class User(
    var userId: String = "",
    var loginVia: String = "",
    var userName: String = "",
    var iconId: Int = -1,
    var experience: Long = 0,
    var ongoingTasks: List<String> = listOf(), //User ongoing task id list
    var historyTasks: List<String> = listOf(), //User history task id list
    var todayDoneCount: Int = 0
) : Parcelable{
    companion object {
        const val INTERVAL_CONSTANT = 50
    }
    val level
        get() = (experience / INTERVAL_CONSTANT)
    val levelProcess
        get() = (experience % INTERVAL_CONSTANT).toInt()

//    fun isLoginFrom(from: Int): Boolean {
//        return loginVia == when (from) {
//            GOOGLE -> LiTsapApplication.instance.getString(R.string.google)
//            FACEBOOK -> LiTsapApplication.instance.getString(R.string.facebook)
//            else -> ""
//        }
//    }
}

//const val GOOGLE = 0x01
//const val FACEBOOK = 0x02
//const val UNKNOWN = -1