package studio.honidot.litsap.extension

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as LiTsapApplication).liTsapRepository
    return ViewModelFactory(repository)
}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}