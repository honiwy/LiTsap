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
import com.github.mikephil.charting.components.XAxis
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
import kotlin.collections.ArrayList

private const val BAR_CHART_DRAW_DAYS = 7

class ProfileFragment : Fragment() {
    private val viewModel by viewModels<ProfileViewModel> { getVmFactory(
        ProfileFragmentArgs.fromBundle(
            arguments!!
        ).userIdKey
    ) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        binding.recyclerTab.adapter = CompetitionAdapter(viewModel, CompetitionAdapter.OnClickListener {
//            viewModel.navigateToDetail(it)
        })

        val adapter = MurmurAdapter(viewModel)
        binding.recyclerMurmur.adapter = adapter

        viewModel.historyPoints.observe(this, Observer {
            it?.let {
                drawBarChart(binding.barChart, it, BAR_CHART_DRAW_DAYS,viewModel.user.value!!.ongoingTasks.size)
            }
        })

        return binding.root
    }

    private fun drawBarChart(chart: BarChart, history: List<History>, dayCount: Int, taskCount: Int) {
        val colorTable = listOf("#f8cd72", "#bdd176", "#81ce8f", "#45c6af", "#15b9c8", "#41a8d1")
        val formatter = DateTimeFormatter.ofPattern("MMM dd")

        val xDate = ArrayList<String>()
        val yEntry = ArrayList<BarEntry>()
        val colors = ArrayList<Int>()
        val legends = ArrayList<LegendEntry>()

        val pointArrayList = mutableListOf<FloatArray>()
        val time = LocalDateTime.now()

        for (i in dayCount downTo 1) {
            xDate.add(time.minusDays((i - 1).toLong()).format(formatter)) //set each date format in x axis
            pointArrayList.add(FloatArray(taskCount)) //prepare ${dayCount} FloatArray(taskCount)
        }


        val sortedHistory = history.sortedBy { it.taskName }
        var taskIndex = 0  // indicate n-th task
        var historyIndex = 0
        var name = sortedHistory[historyIndex].taskName
        val legendEntry = LegendEntry().apply {
            label = sortedHistory[historyIndex].taskName
            formColor = Color.parseColor(colorTable[taskIndex])
        }
        colors.add(Color.parseColor(colorTable[taskIndex]))
        legends.add(legendEntry)
        sortedHistory.forEach {
            if (it.taskName != name) { // found a different task
                taskIndex += 1
                val legendEntry = LegendEntry().apply {
                    label = it.taskName
                    formColor = Color.parseColor(colorTable[taskIndex])
                }
                colors.add(Color.parseColor(colorTable[taskIndex]))
                legends.add(legendEntry)
                name = it.taskName
            }
            for (i in dayCount downTo 1) {
                val timeFrom = time.minusDays(i-1.toLong()).toEpochSecond(ZoneOffset.MAX) * 1000
                val timeTo = time.minusDays(i-1.toLong()).toEpochSecond(ZoneOffset.MIN) * 1000
                if (it.recordDate in timeFrom until timeTo) {
                    pointArrayList[dayCount - i][taskIndex] += it.achieveCount.toFloat()
                    Logger.e("The history ${it.taskName} is done in ${dayCount - i}th day, got ${it.achieveCount} points")
                    break //if found the day belong, no need to run the remaining for loop
                }
            }
            historyIndex += 1
        }

        for (i in 0 until dayCount) {
            yEntry.add(BarEntry(i.toFloat(), pointArrayList[i]))
        }


        val barDataSet = BarDataSet(yEntry, "")
        barDataSet.colors = colors
        barDataSet.setDrawValues(false)

        chart.apply {
            data = BarData(barDataSet)
            xAxis.setDrawGridLines(false)
            chart.description.isEnabled = false
            xAxis.valueFormatter = IndexAxisValueFormatter(xDate)
            legend.setCustom(legends)
            legend.form = Legend.LegendForm.CIRCLE
            legend.isWordWrapEnabled = true
             xAxis.position = XAxis.XAxisPosition.BOTTOM
            invalidate()
        }
    }
}