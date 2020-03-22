package studio.honidot.litsap.profile.face

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.UserProfile
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger

class FaceChooseViewModel(private val repository: LiTsapRepository) : ViewModel() {
    val faceList = MutableLiveData<List<Int>>().apply {
        value = getFaceList()
    }

    private fun getFaceList(): List<Int> {
        val faceList = mutableListOf<Int>()
        for (i in UserProfile.values().indices) {
            faceList.add(i)
        }
        return faceList
    }

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun updateUserIcon(userId: String, iconId: Int) {
        coroutineScope.launch {
            when (repository.updateUserIcon(userId, iconId)) {
                is Result.Success -> {
                    Logger.i("Icon update!")
                }
            }
        }
    }

    val selectedFacePosition = MutableLiveData<Int>()

}