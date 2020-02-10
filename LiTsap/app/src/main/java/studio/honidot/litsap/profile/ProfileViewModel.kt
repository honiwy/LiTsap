package studio.honidot.litsap.profile

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import studio.honidot.litsap.source.LiTsapRepository


class ProfileViewModel(private val liTsapRepository: LiTsapRepository) : ViewModel() {



    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun addDataSet(chart: BarChart) {
        val colorTable = listOf( Color.parseColor("#41a8d1"), Color.parseColor("#f8cd72"),Color.parseColor( "#bdd176"),
            Color.parseColor("#81ce8f"),Color.parseColor( "#45c6af"), Color.parseColor("#15b9c8"))
            val dataVals = ArrayList<BarEntry>()
        dataVals.add(BarEntry(0f,floatArrayOf(2f,3f,2f,3f,2f,1f)))
        dataVals.add(BarEntry(1f,floatArrayOf(0f,3f,1f,4f,4f,0f)))
        dataVals.add(BarEntry(2f,floatArrayOf(2f,2f,3f,0f,3f,4f)))
        dataVals.add(BarEntry(3f,floatArrayOf(0f,0f, 0f, 2f,4f,1f)))
        dataVals.add(BarEntry(4f,floatArrayOf(2f,3f,1f,3f,4f,1f)))
        dataVals.add(BarEntry(5f,floatArrayOf(3f,1f,0f,2f,1f,2f)))

            val barDataSet = BarDataSet(dataVals, "HAHA")
        barDataSet.colors = colorTable
        barDataSet.setDrawValues(false)


            chart.apply {
                data = BarData(barDataSet)
                xAxis.setDrawGridLines(false)
            }

    }


}