package studio.honidot.litsap

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

class LiTsapApplication : Application() {

    companion object {
        var instance: LiTsapApplication by Delegates.notNull()
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContext = applicationContext
    }
}