package studio.honidot.litsap.task.rest

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

class RestViewModel(
    private val liTsapRepository: LiTsapRepository,
    private val arguments: Workout
) : ViewModel() {

    private val breakTime = 10*60L
    val breakTimeInt:Int
            get() = breakTime.toInt()

    lateinit var countDownTimer: CountDownTimer
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
        _totalRemained.value = breakTimeInt
        startCountDownTimer(breakTime*1000)
    }

    // Handle leave detail
    private val _leaveRest = MutableLiveData<Boolean>()

    val leaveRest: LiveData<Boolean>
        get() = _leaveRest

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

    fun leaveRest() {
        countDownTimer.cancel()
        _leaveRest.value = true
    }

    private fun startCountDownTimer(timeCountInMilliSeconds: Long) {
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _totalRemained.value = (millisUntilFinished/1000).toInt()
            }
            override fun onFinish() {

            }
        }
        countDownTimer.start()
    }


}