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
        uploadImage()
        updateTaskModule(workout)
        createTaskHistory(
            History(
                workout.note,
                workout.imageUri,
                workout.achieveSectionCount,
                Calendar.getInstance().timeInMillis,
                workout.taskId,
                workout.taskName
            )
        )
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


    private var filePath: Uri? = null
    private var storageReference: StorageReference? = null

    private fun uploadImage() {
        Logger.w("uploadImage! filePath:$filePath, filePathString:${filePath.toString()}   ")
        if (filePath != null) {

            storageReference = FirebaseStorage.getInstance().reference

            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                Logger.w("Upload success!")
                _count.value = _count.value!!.plus(1)
            }?.addOnFailureListener {
                Logger.w("Upload fail!")
            }
        } else {
            Logger.w("Please Upload an Image")
        }
    }

    private fun addUploadRecordToDb(uri: String) {

        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        FirebaseFirestore.getInstance().collection("posts")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Logger.w("Saved to DB")
            }
            .addOnFailureListener { e ->
                Logger.w("Error saving to DB")
            }
    }

    fun bindImagePath(imageUri: Uri) {
        filePath = imageUri
        _workout.value!!.imageUri = imageUri.toString()
        _workout.value = _workout.value
    }

    fun onProfileNavigated() {
        _count.value = null
    }


}