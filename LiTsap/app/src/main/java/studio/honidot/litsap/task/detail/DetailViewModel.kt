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
import studio.honidot.litsap.data.Task


class DetailViewModel(
    private val arguments: Task
) : ViewModel() {

    // Detail has product data from arguments
    private val _task = MutableLiveData<Task>().apply {
        value = arguments
    }

    val task: LiveData<Task>
        get() = _task


    val moduleDetailOpen= MutableLiveData<Boolean>()

    init{
        moduleDetailOpen.value = false
    }

    fun clickModuleDetailArrow(){
        moduleDetailOpen.value?.let{
            moduleDetailOpen.value = !it
        }
    }

    fun addDataSet(chart: PieChart) {
        val colorTable = listOf("#f5e0b0","#f9c267","#015c92","#2D82B6","#f1ab10","#88CDF6")
        _task.value?.let{
            val yEntry = ArrayList<PieEntry>()
            val colors = ArrayList<Int>()
            it.modules.forEach { moduleItem->
                if(moduleItem.progressCount>0) {
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
            pieDataSet.valueTextSize = 12f
            pieDataSet.colors = colors

            chart.apply {
                data = PieData(pieDataSet)
                holeRadius= 20f
                chart.description.isEnabled = false
                setTransparentCircleAlpha(0)
                setEntryLabelColor(Color.BLACK)
                chart.legend.isEnabled = false
                setCenterTextSize(10f)
                invalidate()
            }

        }

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