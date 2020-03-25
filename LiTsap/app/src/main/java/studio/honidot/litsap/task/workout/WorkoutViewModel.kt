package studio.honidot.litsap.task.workout

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.Logger

class WorkoutViewModel(
    private val repository: LiTsapRepository,
    private val arguments: Workout
) : ViewModel() {


    private val _workout = MutableLiveData<Workout>().apply {
        value = arguments
    }
    val workout: LiveData<Workout>
        get() = _workout

    var messageList = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }

    var newMessage = MutableLiveData<String>()


    fun addMessage() {
        newMessage.value?.let {
            if (it.isNotEmpty()) {
                messageList.value!!.add(it.trim())
                messageList.value = messageList.value//Let observer detect the change
                newMessage.value = ""
            }
        }
    }

    private val _musicPlay = MutableLiveData<Boolean>()

    val musicPlay: LiveData<Boolean>
        get() = _musicPlay

    private val _userIconId = MutableLiveData<Int>()

    val userIconId: LiveData<Int>
        get() = _userIconId

    fun findUser(firebaseUserId: String) {
        coroutineScope.launch {
            val result = repository.findUser(firebaseUserId)
            _userIconId.value = when (result) {
                is Result.Success -> {
                    if (result.data == null) {
                        -1
                    } else {
                        result.data.iconId
                    }
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

    lateinit var taskCountDownTimer: CountDownTimer

    private val _totalTaskRemained = MutableLiveData<Int>()
    val totalTaskRemained: LiveData<Int>
        get() = _totalTaskRemained

    private val _isCountingTask = MutableLiveData<Boolean>()
    val isCountingTask: LiveData<Boolean>
        get() = _isCountingTask

    private val _remainCount = MutableLiveData<Int>().apply {
        value = arguments.planSectionCount
    }
    val remainCount: LiveData<Int>
        get() = _remainCount

    private val _sectionTimeRemained = MutableLiveData<Int>()
    val sectionTimeRemained: LiveData<Int>
        get() = _sectionTimeRemained

    init {
        _totalTaskRemained.value = arguments.displayProcess
        _sectionTimeRemained.value = Workout.WORKOUT_TIME
        startTaskCountDownTimer(_sectionTimeRemained.value?.times(SECOND_TO_MILLISECOND.toLong())?:0)
    }

    companion object {
        const val SECOND_TO_MILLISECOND = 1000
    }

    private val _isCountingRest = MutableLiveData<Boolean>()
    val isCountingRest: LiveData<Boolean>
        get() = _isCountingRest


    fun navigateToFinish() {
        _workout.value?.let {
            it.recordInfo = messageList.value as List<String>
        }
        _navigateToFinish.value = _workout.value
    }

    fun onFinishNavigated() {
        _musicPlay.value = null
        _totalTaskRemained.value?.let {
            taskCountDownTimer.cancel()
        }
        _totalRestRemained.value?.let {
            restCountDownTimer.cancel()
        }
        _navigateToFinish.value = null
    }

    fun muteMusic() {
        _musicPlay.value = (_musicPlay.value == false)
    }

    fun pausePlayTimer() {
        if (_isCountingTask.value == true) {
            taskCountDownTimer.cancel()
            _isCountingTask.value = false
        } else {
            startTaskCountDownTimer(_sectionTimeRemained.value?.times(SECOND_TO_MILLISECOND.toLong())?:0)
        }
    }

    private fun startTaskCountDownTimer(timeCountInMilliSeconds: Long) {
        taskCountDownTimer =
            object : CountDownTimer(timeCountInMilliSeconds, SECOND_TO_MILLISECOND.toLong()) {
                override fun onTick(millisUntilFinished: Long) {
                    _totalTaskRemained.value =  _totalTaskRemained.value?.minus(1)
                    _sectionTimeRemained.value = (millisUntilFinished / SECOND_TO_MILLISECOND).toInt()
                }

                override fun onFinish() {
                    _remainCount.value = _remainCount.value?.minus(1)
                    _workout.value?.let {
                        it.achieveSectionCount += 1
                    }
                    _isCountingTask.value = false
                    if (_remainCount.value == 0) {
                        navigateToFinish()
                    } else {
                        startRestCountDownTimer(Workout.BREAK_TIME * SECOND_TO_MILLISECOND.toLong())
                        _musicPlay.value = true

                    }
                }
            }
        _isCountingTask.value = true
        taskCountDownTimer.start()
    }

    lateinit var restCountDownTimer: CountDownTimer

    private val _totalRestRemained = MutableLiveData<Int>()
    val totalRestRemained: LiveData<Int>
        get() = _totalRestRemained

    private fun startRestCountDownTimer(timeCountInMilliSeconds: Long) {
        restCountDownTimer =
            object : CountDownTimer(timeCountInMilliSeconds, SECOND_TO_MILLISECOND.toLong()) {
                override fun onTick(millisUntilFinished: Long) {
                    _totalRestRemained.value = (millisUntilFinished / SECOND_TO_MILLISECOND).toInt()
                }

                override fun onFinish() {
                    _isCountingRest.value = false
                    _musicPlay.value = null
                    _sectionTimeRemained.value = Workout.WORKOUT_TIME
                    startTaskCountDownTimer(_sectionTimeRemained.value?.times(SECOND_TO_MILLISECOND.toLong())?:0)
                }
            }
        _isCountingRest.value = true
        restCountDownTimer.start()
    }


    private val _navigateToFinish = MutableLiveData<Workout>()

    val navigateToFinish: LiveData<Workout>
        get() = _navigateToFinish

    private val _leaveWorkout = MutableLiveData<Boolean>()

    val leaveWorkout: LiveData<Boolean>
        get() = _leaveWorkout

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

    fun leaveWorkout() {
        _musicPlay.value = null
        _totalTaskRemained.value?.let {
            taskCountDownTimer.cancel()
        }
        _totalRestRemained.value?.let {
            restCountDownTimer.cancel()
        }
        _leaveWorkout.value = true
    }

}