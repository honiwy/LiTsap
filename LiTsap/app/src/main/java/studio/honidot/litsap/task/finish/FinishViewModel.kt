package studio.honidot.litsap.task.finish

import android.icu.util.Calendar
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.data.History
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import java.util.*

class FinishViewModel(
    private val repository: LiTsapRepository,
    private val arguments: Workout
) : ViewModel() {

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    private val _workout = MutableLiveData<Workout>().apply {
        value = arguments
    }
    val workout: LiveData<Workout>
        get() = _workout


    private val _count = MutableLiveData<Int>().apply {
        value = 0
    }

    val count: LiveData<Int>
        get() = _count


    fun update(workout: Workout) {
        uploadImage(workout)
        updateTaskModule(workout)

        updateUserStatus(workout)
        updateTaskStatus(workout.taskId, workout.achieveSectionCount.toLong())
    }

    private fun updateTaskStatus(taskId: String, accumulationPoints: Long) {
        coroutineScope.launch {
            val result = repository.updateTaskStatus(taskId, accumulationPoints)
            when (result) {
                is Result.Success -> {

                }
                else -> {
                    Logger.d("Oops! [updateTaskStatus] is failed")
                }
            }
            _count.value = _count.value!!.plus(1)
        }
    }

    private fun updateUserStatus(workout: Workout) {
        coroutineScope.launch {
            val result = repository.updateUserStatus(workout)
            when (result) {
                is Result.Success -> {

                }
                else -> {
                    Logger.d("Oops! [updateUserStatus] is failed")
                }
            }
            _count.value = _count.value!!.plus(1)
        }
    }



    private fun createTaskHistory(history: History) {
        coroutineScope.launch {
            val result = repository.createTaskHistory(history)
            when (result) {
                is Result.Success -> {

                }
                else -> {
                    Logger.d("Oops! [createTaskHistory] is failed")
                }
            }
            _count.value = _count.value!!.plus(1)
        }

    }

    val filePath = MutableLiveData<Uri>()

    private fun updateTaskModule(workout: Workout) {
        coroutineScope.launch {
            val result = repository.updateTaskModule(workout)
            when (result) {
                is Result.Success -> {

                }
                else -> {
                    Logger.d("Oops! [updateTaskModule] is failed")
                }
            }
            _count.value = _count.value!!.plus(1)
        }
    }

    private fun uploadImage(workout: Workout) {

        coroutineScope.launch {
            filePath.value?.let {
                val result = repository.uploadImage(it)
                when (result) {
                    is Result.Success -> {
                        _workout.value!!.imageUri = result.data.toString()
                        createTaskHistory(
                            History(
                                workout.note,
                                result.data.toString(),
                                workout.achieveSectionCount,
                                Calendar.getInstance().timeInMillis,
                                workout.taskId,
                                workout.taskName
                            )
                        )
                    }
                    else -> {
                        Logger.d("Oops! [createTaskHistory] is failed")
                    }
                }
            }
            _count.value = _count.value!!.plus(1)
        }
    }


    fun onTaskNavigated() {
        _count.value = null
    }


}