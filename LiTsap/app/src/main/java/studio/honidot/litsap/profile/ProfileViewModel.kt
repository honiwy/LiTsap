package studio.honidot.litsap.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import studio.honidot.litsap.data.*
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

private const val BAR_CHART_DRAW_DAYS = 7

class ProfileViewModel(private val repository: LiTsapRepository, private val arguments: String) :
    ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _historyPoints = MutableLiveData<List<History>>()

    val historyPoints: LiveData<List<History>>
        get() = _historyPoints

    init {
        findUser(arguments)
    }

    private fun findUser(firebaseUserId: String) {
        coroutineScope.launch {
            val result = repository.findUser(firebaseUserId)
            _user.value = when (result) {
                is Result.Success -> {
                    if (result.data!!.ongoingTasks.isNotEmpty()) {
                        retrieveHistoryPoints(result.data.ongoingTasks, BAR_CHART_DRAW_DAYS - 1)
                    }
                    result.data
                }
                is Result.Fail -> {
                    null
                }
                is Result.Error -> {
                    null
                }
                else -> {
                    null
                }
            }
        }
    }

    private fun retrieveHistoryPoints(taskIdList: List<String>, passNday: Int) {

        coroutineScope.launch {
            val result = repository.getHistory(taskIdList, passNday)
            _historyPoints.value = when (result) {
                is Result.Success -> {
                    Logger.d("history: ${result.data}")
                    result.data
                }
                is Result.Fail -> {
                    null
                }
                is Result.Error -> {
                    null
                }
                else -> {
                    null
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}