package studio.honidot.litsap.task.workout

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.source.LiTsapRepository


class WorkoutViewModel(
    private val repository: LiTsapRepository,
    private val arguments: Workout
) : ViewModel() {

    var messageList = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }



    var newMessage = MutableLiveData<String>()

    fun addMessage() {
        newMessage.value?.let {
            if (it.isNotEmpty()) {
                messageList.value!!.add(it)
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
                    if(result.data==null)
                    {
                        0
                    }else{
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

    init {
        _totalTaskRemained.value = arguments.displayProcess
        _musicPlay.value = true
        startTaskCountDownTimer(arguments.workoutTime * 1000)

    }

    private val _isCountingRest = MutableLiveData<Boolean>()
    val isCountingRest: LiveData<Boolean>
        get() = _isCountingRest

    fun pausePlayTimer() {
        if (_isCountingTask.value == true) {
            taskCountDownTimer.cancel()
            _isCountingTask.value = false
        } else {
            startTaskCountDownTimer(_totalTaskRemained.value!! * 1000L)
        }
    }

    fun navigateToFinish() {
        _workout.value?.let{
            it.recordInfo = messageList.value as List<String>}
        _navigateToFinish.value = _workout.value
    }

    fun onFinishNavigated() {
        _navigateToFinish.value = null
    }

    fun muteMusic(){
        _musicPlay.value = (_musicPlay.value== false)
    }

    private fun startTaskCountDownTimer(timeCountInMilliSeconds: Long) {
        taskCountDownTimer = object : CountDownTimer(timeCountInMilliSeconds - 1, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _workout.value?.let {wo->
                    _totalTaskRemained.value = (millisUntilFinished / 1000).toInt()
                    if (_totalTaskRemained.value!!.rem(wo.sectionConstant) == 0 && _totalTaskRemained.value != 0) {
                        _isCountingRest.value = true
                        _musicPlay.value = null
                        wo.achieveSectionCount += 1
                        pausePlayTimer()
                        startRestCountDownTimer(wo.breakTimeConstant * 1000L)
                    }
                }
            }

            override fun onFinish() {
                _workout.value!!.achieveSectionCount += 1
                _musicPlay.value = null
                navigateToFinish()
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
        restCountDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _totalRestRemained.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                _isCountingRest.value = false
                _musicPlay.value = true
                pausePlayTimer()
            }
        }
        restCountDownTimer.start()
    }


    // Detail has product data from arguments
    private val _workout = MutableLiveData<Workout>().apply {
        value = arguments
    }
    val workout: LiveData<Workout>
        get() = _workout


    private val _navigateToFinish = MutableLiveData<Workout>()

    val navigateToFinish: LiveData<Workout>
        get() = _navigateToFinish

    // Handle leave detail
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
        taskCountDownTimer.cancel()
        _leaveWorkout.value = true
    }


}