package studio.honidot.litsap.task.detail

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import studio.honidot.litsap.data.FireTask
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.source.LiTsapRepository


private const val section = 20*60L //20 minutes

class DetailViewModel(
    private val liTsapRepository: LiTsapRepository,
    private val arguments: FireTask
) : ViewModel() {

    // Detail has product data from arguments
    private val _taskInfo = MutableLiveData<FireTask>().apply {
        value = arguments
    }

    val taskInfo: LiveData<FireTask>
        get() = _taskInfo

    private val _workout = MutableLiveData<Workout>()

    val workout: LiveData<Workout>
        get() = _workout

    val moduleDetailOpen = MutableLiveData<Boolean>()
    val moduleTime = MutableLiveData<Int>()

    val moduleStatusOpen = MutableLiveData<Boolean>()

    // Handle leave detail
    private val _leaveDetail = MutableLiveData<Boolean>()

    val leaveDetail: LiveData<Boolean>
        get() = _leaveDetail

    private val _navigateToWorkout = MutableLiveData<Workout>()

    val navigateToWorkout: LiveData<Workout>
        get() = _navigateToWorkout

    init {
        _taskInfo.value?.apply {
            _workout.value =
                Workout(
                    this,Module(modules[0].name, modules[0].progressCount),
                    0, false, listOf(""))
        }
        moduleTime.value = 0
        moduleStatusOpen.value = true
        moduleDetailOpen.value = true
    }


    fun changeModule(selectedPosition: Int){
        _workout.value?.apply {
            this.selectedModule = _taskInfo.value!!.modules[selectedPosition]
        }
    }

    fun onSetWorkoutTime(time: Int) {
        _workout.value?.apply{
            workoutTime = time * section

        }
        moduleTime.value = time
    }

    fun clickModuleDetailArrow() {
        moduleDetailOpen.value?.let {
            moduleDetailOpen.value = !it
        }
    }

    fun clickModuleStatusArrow() {
        moduleStatusOpen.value?.let {
            moduleStatusOpen.value = !it
        }
    }

    fun addDataSet(chart: PieChart) {
        val colorTable = listOf( "#41a8d1", "#f8cd72", "#bdd176", "#81ce8f", "#45c6af", "#15b9c8")
        _taskInfo.value?.let {
            val yEntry = ArrayList<PieEntry>()
            val colors = ArrayList<Int>()
            it.modules.forEach { moduleItem ->
                if (moduleItem.progressCount > 0) {
                    colors.add(Color.parseColor(colorTable[yEntry.size]))
                    yEntry.add(
                        PieEntry(
                            (moduleItem.progressCount.toFloat() / it.accumulatedCount),
                            moduleItem.name
                        )
                    )
                }
            }
            val pieDataSet = PieDataSet(yEntry, "")
            pieDataSet.colors = colors
            pieDataSet.setDrawValues(false)


            chart.apply {
                data = PieData(pieDataSet)
                holeRadius = 20f
                chart.description.isEnabled = false
                setTransparentCircleAlpha(0)
                setEntryLabelColor(Color.BLACK)
                chart.legend.isEnabled = false
                invalidate()
            }

        }

    }

    fun navigateToWorkout(workout: Workout) {
        _navigateToWorkout.value = workout
    }

    fun onWorkoutNavigated() {
        _navigateToWorkout.value = null
    }

    fun leaveDetail() {
        _leaveDetail.value = true
    }

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

}