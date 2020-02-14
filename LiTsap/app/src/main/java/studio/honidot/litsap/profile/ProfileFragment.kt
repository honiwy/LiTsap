package studio.honidot.litsap.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import studio.honidot.litsap.data.History
import studio.honidot.litsap.databinding.FragmentProfileBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.util.Logger
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {
    private val viewModel by viewModels<ProfileViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.historyPoints.observe(this, Observer {
            it?.let {
                viewModel.tasks.value?.let { tasks ->
                    drawBarChart(binding.barChart, tasks,it)
                }
            }
        })

        viewModel.user.observe(this, Observer {
            it?.let {
                viewModel.retrieveOngoingTasks(it.ongoingTasks)
                viewModel.retrieveHistoryPoints(it.ongoingTasks,5)
            }
        })

        return binding.root
    }

    private fun drawBarChart(chart: BarChart, taskNames: List<String>, history: List<History>) {
        val colorTable = listOf( "#f8cd72","#bdd176","#81ce8f","#45c6af","#15b9c8","#41a8d1")
        val formatter = DateTimeFormatter.ofPattern("MMM dd")

        val xDate = ArrayList<String>()
        val yEntry = ArrayList<BarEntry>()
        val colors = ArrayList<Int>()
        val legends = ArrayList<LegendEntry>()
        val pointArray  = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f)

        for(i in 5 downTo 0)
        {
            val timeMin =  LocalDateTime.now().minusDays(i.toLong()).toEpochSecond(ZoneOffset.MIN)*1000
            val timeMax =  LocalDateTime.now().minusDays(i-1.toLong()).toEpochSecond(ZoneOffset.MIN)*1000
            history.forEach {
                if (it.recordDate in (timeMin + 1) until timeMax){
                    Logger.v("You have a history in $i day ago, which got ${it.achieveCount}")
                    pointArray[5-i] = it.achieveCount.toFloat()
                }
            }
        }

        colorTable.forEach {color->
            colors.add(Color.parseColor(color))
            xDate.add(LocalDateTime.now().minusDays(colorTable.size.toLong()-colors.size).format(formatter))
        }
        taskNames.forEach {name->
            val legendEntry = LegendEntry()
            legendEntry.label = name
            legendEntry.formColor = colors[legends.size]
            legends.add(legendEntry)
        }
        yEntry.add(BarEntry(0f, floatArrayOf(pointArray[0], 3f, 2f, 3f, 2f, 1f)))
        yEntry.add(BarEntry(1f, floatArrayOf(pointArray[1], 3f, 1f, 4f, 4f, 0f)))
        yEntry.add(BarEntry(2f, floatArrayOf(pointArray[2], 3f, 1f, 4f, 4f, 0f)))
        yEntry.add(BarEntry(3f, floatArrayOf(pointArray[3], 3f, 1f, 4f, 4f, 0f)))
        yEntry.add(BarEntry(4f, floatArrayOf(pointArray[4], 3f, 1f, 0f, 4f, 1f)))
        yEntry.add(BarEntry(5f, floatArrayOf(pointArray[5], 3f, 1f, 4f, 4f, 0f)))

        val barDataSet = BarDataSet(yEntry, "")
        barDataSet.colors = colors
        barDataSet.setDrawValues(false)

        chart.apply {
            data = BarData(barDataSet)
            xAxis.setDrawGridLines(false)
            chart.description.isEnabled = false
            xAxis.valueFormatter = IndexAxisValueFormatter(xDate)
            legend.setCustom( legends )
            legend.form = Legend.LegendForm.CIRCLE
            legend.isWordWrapEnabled = true
            // xAxis.position = XAxis.XAxisPosition.BOTTOM
            invalidate()
        }
    }
}