package studio.honidot.litsap.profile.face

import android.icu.util.Calendar
import android.widget.Toast
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
import studio.honidot.litsap.UserProfile
import studio.honidot.litsap.data.History
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger

class FaceChooseViewModel(private val repository: LiTsapRepository) : ViewModel() {
 val faceList = MutableLiveData<List<Int>>().apply {
     value = getFaceList()
 }

    private fun getFaceList(): List<Int> {
        val tmp = mutableListOf<Int>()
        var count = 0

        for(i in UserProfile.values().indices){
            tmp.add(count)
            count += 1
            Logger.d("count: $count")
        }

         return tmp
    }

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _taskId = MutableLiveData<String>()

    val taskId: LiveData<String>
        get() = _taskId


    private fun createTaskModules(taskId: String, modules: Module) {
        coroutineScope.launch {
            val result = repository.createTaskModules(taskId, modules)
            when (result) {
                is Result.Success -> {
                    Logger.i("Modules update!")
                }
            }
        }
    }

    val selectedFacePosition = MutableLiveData<Int>()


}