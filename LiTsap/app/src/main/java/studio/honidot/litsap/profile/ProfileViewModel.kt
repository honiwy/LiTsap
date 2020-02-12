package studio.honidot.litsap.profile

import android.graphics.Color
import androidx.constraintlayout.solver.widgets.Analyzer.setPosition
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.honidot.litsap.data.Result
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.data.User
import studio.honidot.litsap.source.LiTsapRepository


class ProfileViewModel(private val repository: LiTsapRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val calendarOpen = MutableLiveData<Boolean>().apply {
        value = true
    }
    val completeOpen = MutableLiveData<Boolean>().apply {
        value = true
    }

    fun clickCompleteArrow() {
        completeOpen.value?.let {
            completeOpen.value = !it
        }
    }

    fun clickCalendarArrow() {
        calendarOpen.value?.let {
            calendarOpen.value = !it
        }
    }

    init {
        retrieveUserInfo()
    }

    private fun retrieveUserInfo() {
        coroutineScope.launch {
            val result = repository.getUser()
            _user.value = when (result) {
                is Result.Success -> {
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


    fun addDataSet(chart: BarChart) {
        val colorTable = listOf(
            Color.parseColor("#f8cd72"),
            Color.parseColor("#bdd176"),
            Color.parseColor("#81ce8f"),
            Color.parseColor("#45c6af"),
            Color.parseColor("#15b9c8"),
            Color.parseColor("#41a8d1")
        )
        val dataVals = ArrayList<BarEntry>()
        dataVals.add(BarEntry(0f, floatArrayOf(2f, 3f, 2f, 3f, 2f, 1f)))
        dataVals.add(BarEntry(1f, floatArrayOf(0f, 3f, 1f, 4f, 4f, 0f)))
        dataVals.add(BarEntry(2f, floatArrayOf(2f, 2f, 3f, 0f, 3f, 0f)))
        dataVals.add(BarEntry(3f, floatArrayOf(0f, 0f, 0f, 2f, 4f, 1f)))
        dataVals.add(BarEntry(4f, floatArrayOf(2f, 3f, 1f, 0f, 4f, 1f)))
        dataVals.add(BarEntry(5f, floatArrayOf(3f, 1f, 0f, 2f, 1f, 2f)))

        val barDataSet = BarDataSet(dataVals, "任務細項")
        barDataSet.colors = colorTable
        barDataSet.setDrawValues(false)


        val xLabels = listOf("Feb 5", "Feb 6", "Feb 7", "Feb 8", "昨日", "今日")
        val legendEntryA = LegendEntry()
        legendEntryA.label = "吃東西"
        legendEntryA.formColor = colorTable[0]
        val legendEntryB = LegendEntry()
        legendEntryB.label = "喝東西"
        legendEntryB.formColor = colorTable[1]
        val legendEntryC = LegendEntry()
        legendEntryC.label = "打東西打東西打東西"
        legendEntryC.formColor = colorTable[2]
        val legendEntryD = LegendEntry()
        legendEntryD.label = "敲東西"
        legendEntryD.formColor = colorTable[3]
        val legendEntryE = LegendEntry()
        legendEntryE.label = "想東西想東西"
        legendEntryE.formColor = colorTable[4]
        val legendEntryF = LegendEntry()
        legendEntryF.label = "丟東西"
        legendEntryF.formColor = colorTable[5]

        chart.apply {
            data = BarData(barDataSet)
            xAxis.setDrawGridLines(false)
            chart.description.isEnabled = false
            xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
            legend.setCustom(
                listOf(
                    legendEntryA,
                    legendEntryB,
                    legendEntryC,
                    legendEntryD,
                    legendEntryE,
                    legendEntryF
                )
            )
            legend.form = Legend.LegendForm.CIRCLE
            legend.isWordWrapEnabled = true
            // xAxis.position = XAxis.XAxisPosition.BOTTOM
        }

    }


}