package studio.honidot.litsap.data

import android.content.Context
import studio.honidot.litsap.LiTsapApplication.Companion.instance

object UserManager {

    private const val USER_DATA = "user_data"
    private const val USER_ID = "user_id"

    var userId: String? = null
        get() = instance
            .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
            .getString(USER_ID, null)
        set(value) {
            field = when (value) {
                null -> {
                    instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .remove(USER_ID)
                        .apply()
                    null
                }
                else -> {
                    instance
                        .getSharedPreferences(USER_DATA, Context.MODE_PRIVATE).edit()
                        .putString(USER_ID, value)
                        .apply()
                    value
                }
            }
        }





    /**
     * It can be use to check login status directly
     */
    val isLoggedIn: Boolean
        get() = userId != null

    /**
     * Clear the [userToken] and the [user]/[_user] data
     */
    fun clear() {
        userId = null
    }

    private var lastChallengeTime: Long = 0
    private var challengeCount: Int = 0
    private const val CHALLENGE_LIMIT = 23

    /**
     * Winter is coming
     */
//    fun challenge() {
//        if (System.currentTimeMillis() - lastChallengeTime > 5000) {
//            lastChallengeTime = System.currentTimeMillis()
//            challengeCount = 0
//        } else {
//            if (challengeCount == CHALLENGE_LIMIT) {
//                userToken = null
//                Toast.makeText(instance,
//                    getString(R.string.profile_mystic_information),
//                    Toast.LENGTH_SHORT).show()
//            } else {
//                challengeCount++
//            }
//        }
//    }
}