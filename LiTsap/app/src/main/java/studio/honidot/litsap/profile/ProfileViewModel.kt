package studio.honidot.litsap.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import studio.honidot.litsap.TaskCategory
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.data.TaskInfo
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.source.LiTsapRepository

class ProfileViewModel(private val liTsapRepository: LiTsapRepository) : ViewModel() {



    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }



}