package studio.honidot.litsap

import android.app.Application
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.ServiceLocator
import kotlin.properties.Delegates

class LiTsapApplication : Application() {

    // Depends on the flavor,
    val liTsapRepository: LiTsapRepository
        get() = ServiceLocator.provideTasksRepository(this)


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