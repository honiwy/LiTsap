package studio.honidot.litsap.login

import android.content.Context
import studio.honidot.litsap.LiTsapApplication

object UserManager {

    private const val USER_DATA = "user_data"
    private const val LAST_TIME_LOGIN_FB = "last_time_fb"
    private const val LAST_TIME_LOGIN_GOOGLE = "last_time_google"

    var lastTimeFB: String? = null
        get() = LiTsapApplication.instance
            .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            .getString(LAST_TIME_LOGIN_FB, "")
        set(value) {
            field = when (value) {
                null -> {
                    LiTsapApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .remove(LAST_TIME_LOGIN_FB)
                        .apply()
                    null
                }
                else -> {
                    LiTsapApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .putString(LAST_TIME_LOGIN_FB, value)
                        .apply()
                    value
                }
            }
        }

    var lastTimeGoogle: String? = null
        get() = LiTsapApplication.instance
            .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            .getString(LAST_TIME_LOGIN_GOOGLE, "")
        set(value) {
            field = when (value) {
                null -> {
                    LiTsapApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .remove(LAST_TIME_LOGIN_GOOGLE)
                        .apply()
                    null
                }
                else -> {
                    LiTsapApplication.instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .putString(LAST_TIME_LOGIN_GOOGLE, value)
                        .apply()
                    value
                }
            }
        }
}