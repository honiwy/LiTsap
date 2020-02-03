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
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.data.TaskInfo
import studio.honidot.litsap.data.Workout
import studio.honidot.litsap.source.LiTsapRepository
import java.text.DecimalFormat


private const val section = 20*60L //20 minutes

class DetailViewModel(
    private val liTsapRepository: LiTsapRepository,
    private val arguments: TaskInfo
) : ViewModel() {

    // Detail has product data from arguments
    private val _taskInfo = MutableLiveData<TaskInfo>().apply {
        value = arguments
    }

    val taskInfo: LiveData<TaskInfo>
        get() = _taskInfo

    private val _workout = MutableLiveData<Workout>()

    val workout: LiveData<Workout>
        get() = _workout


    val selectedModuleRadio = MutableLiveData<Int>() //await to fix with radio group/recyclerView


    val moduleDetailOpen = MutableLiveData<Boolean>()
    val moduleTime = MutableLiveData<Int>()

    val moduleStatusOpen = MutableLiveData<Boolean>()

    // Handle leave detail
    private val _leaveDetail = MutableLiveData<Boolean>()

    val leaveDetail: LiveData<Boolean>
        get() = _leaveDetail

    // Handle navigation to detail
    private val _navigateToWorkout = MutableLiveData<Workout>()

    val navigateToWorkout: LiveData<Workout>
        get() = _navigateToWorkout

    init {
        moduleDetailOpen.value = true
        _taskInfo.value?.apply {
            _workout.value =
                Workout(
                    task,Module(modules[0].name, modules[0].progressCount),
                    0, false, listOf(""))
        }
        moduleTime.value = 0
        moduleStatusOpen.value = true
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
        val colorTable = listOf("#f5e0b0", "#f9c267", "#015c92", "#88CDF6", "#2D82B6", "#f1ab10")
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