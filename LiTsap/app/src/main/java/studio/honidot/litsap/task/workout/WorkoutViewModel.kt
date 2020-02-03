package studio.honidot.litsap.task.workout

import android.R
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
import java.util.concurrent.TimeUnit


class WorkoutViewModel(
    private val liTsapRepository: LiTsapRepository,
    private val arguments: Workout
) : ViewModel() {

    //*60*1000

    lateinit var countDownTimer: CountDownTimer
    private val _minuteRemained = MutableLiveData<Long>()
    val minuteRemained: LiveData<Long>
        get() = _minuteRemained
    private val _secondRemained = MutableLiveData<Long>()
    val secondRemained: LiveData<Long>
        get() = _secondRemained
    private val _totalRemained = MutableLiveData<Int>()
    val totalRemained: LiveData<Int>
        get() = _totalRemained

    // Detail has product data from arguments
    private val _workout = MutableLiveData<Workout>().apply {
        value = arguments
    }

    val workout: LiveData<Workout>
        get() = _workout

    init {
        _totalRemained.value = arguments.displayProcess
        Log.i("HAHA","Workout:${arguments.displayProcess}")
        startCountDownTimer(arguments.workoutTime*1000)
    }

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
        countDownTimer.cancel()
        _leaveWorkout.value = true
    }

    private fun startCountDownTimer(timeCountInMilliSeconds: Long) {
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _totalRemained.value = (millisUntilFinished/1000).toInt()
                _minuteRemained.value = millisUntilFinished/1000/ 60
                _secondRemained.value = _totalRemained.value!! - _minuteRemained.value!! * 60
                Log.i("HAHA","Total: ${(workout.value as Workout).displayProcess}, Remain: ${totalRemained.value}")
            }
            override fun onFinish() {

            }
        }
        countDownTimer.start()
    }


}