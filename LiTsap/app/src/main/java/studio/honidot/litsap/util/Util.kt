package studio.honidot.litsap.util

import studio.honidot.litsap.LiTsapApplication

object Util {

    fun getString(resourceId: Int): String {
        return LiTsapApplication.instance.getString(resourceId)
    }

}