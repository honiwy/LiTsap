package studio.honidot.litsap.task.workout

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.source.LiTsapRepository


class WorkoutViewModel(
    private val liTsapRepository: LiTsapRepository,
    private val arguments: Workout
) : ViewModel() {

    lateinit var taskCountDownTimer: CountDownTimer

    private val _totalTaskRemained = MutableLiveData<Int>()
    val totalTaskRemained: LiveData<Int>
        get() = _totalTaskRemained

    private val _isCountingTask = MutableLiveData<Boolean>()
    val isCountingTask: LiveData<Boolean>
        get() = _isCountingTask

    init {
        _totalTaskRemained.value = arguments.displayProcess
        startTaskCountDownTimer(arguments.workoutTime * 1000)
//        _totalTaskRemained.value = 60 //60 sec
//        startTaskCountDownTimer(60L*1000)

    }

    val sectionSec = 1200 //1200: 20 min, 10: 10 sec
    val restSec = 300 //300: 5 min, 5: 5 sec

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
        _navigateToFinish.value = _workout.value
    }

    fun onFinishNavigated() {
        _navigateToFinish.value = null
    }

    private fun startTaskCountDownTimer(timeCountInMilliSeconds: Long) {
        taskCountDownTimer = object : CountDownTimer(timeCountInMilliSeconds - 1, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _totalTaskRemained.value = (millisUntilFinished / 1000).toInt()
                if (_totalTaskRemained.value!!.rem(sectionSec) == 0 && _totalTaskRemained.value != 0) {
                    _isCountingRest.value = true
                    pausePlayTimer()
                    startRestCountDownTimer(restSec * 1000L)
                }
            }

            override fun onFinish() {
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