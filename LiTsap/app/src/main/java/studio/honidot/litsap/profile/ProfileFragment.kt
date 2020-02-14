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
            Logger.w("Hello")
            it?.let {
                Logger.w("Hey")
                viewModel.tasks.value?.let { tasks ->
                    drawBarChart(binding.barChart, tasks, it)
                }
            }
        })

        viewModel.tasks.observe(this, Observer {
            it?.let {
                viewModel.retrieveHistoryPoints(viewModel.user.value!!.ongoingTasks, 5)
            }
        })

        viewModel.user.observe(this, Observer {
            it?.let {
                viewModel.retrieveOngoingTasks(it.ongoingTasks)
            }
        })

        return binding.root
    }

    private fun drawBarChart(chart: BarChart, taskNames: List<String>, history: List<History>) {
        val colorTable = listOf("#f8cd72", "#bdd176", "#81ce8f", "#45c6af", "#15b9c8", "#41a8d1")
        val formatter = DateTimeFormatter.ofPattern("MMM dd")

        val xDate = ArrayList<String>()
        val yEntry = ArrayList<BarEntry>()
        val colors = ArrayList<Int>()
        val legends = ArrayList<LegendEntry>()

        val pointArrayList = mutableListOf<FloatArray>()

        colorTable.forEach { color ->
            colors.add(Color.parseColor(color))
            xDate.add(
                LocalDateTime.now().minusDays(colorTable.size.toLong() - colors.size).format(
                    formatter
                )
            )
            pointArrayList.add(floatArrayOf(0f, 0f, 0f))
        }

        taskNames.forEach { name ->
            val legendEntry = LegendEntry()
            legendEntry.label = name
            legendEntry.formColor = colors[legends.size]
            legends.add(legendEntry)
        }

        val sortedHistory = history.sortedBy { it.taskName }

        var historyIndex = 0
        var a = taskNames[0]
        sortedHistory.forEach {
            if (it.taskName != a) {
                historyIndex += 1
                a = it.taskName
            }
            for (i in 5 downTo 0) {
                val timeMin =
                    LocalDateTime.now().minusDays(i.toLong()).toEpochSecond(ZoneOffset.MIN) * 1000
                val timeMax =
                    LocalDateTime.now().minusDays(i - 1.toLong()).toEpochSecond(ZoneOffset.MIN) * 1000
                if (it.recordDate in (timeMin + 1) until timeMax) {
                    pointArrayList[5 - i][historyIndex] = it.achieveCount.toFloat()
                }
            }

        }
        Logger.i("0 ${pointArrayList[0][0]},0 ${pointArrayList[0][1]},0 ${pointArrayList[0][2]}")
        Logger.i("1 ${pointArrayList[1][0]},0 ${pointArrayList[1][1]},0 ${pointArrayList[1][2]}")
        Logger.i("2 ${pointArrayList[2][0]},0 ${pointArrayList[2][1]},0 ${pointArrayList[2][2]}")
        Logger.i("3 ${pointArrayList[3][0]},0 ${pointArrayList[3][1]},0 ${pointArrayList[3][2]}")
        Logger.i("4 ${pointArrayList[4][0]},0 ${pointArrayList[4][1]},0 ${pointArrayList[4][2]}")
        Logger.i("5 ${pointArrayList[5][0]},0 ${pointArrayList[5][1]},0 ${pointArrayList[5][2]}")

        yEntry.add(BarEntry(0f, pointArrayList[0]))
        yEntry.add(BarEntry(1f, pointArrayList[1]))//2
        yEntry.add(BarEntry(2f, pointArrayList[2]))
        yEntry.add(BarEntry(3f, pointArrayList[3]))
        yEntry.add(BarEntry(4f, pointArrayList[4]))
        yEntry.add(BarEntry(5f, pointArrayList[5]))
//        yEntry.add(BarEntry(3f, floatArrayOf(pointArray[3], 3f, 1f, 4f, 4f, 0f)))
//        yEntry.add(BarEntry(4f, floatArrayOf(pointArray[4], 3f, 1f, 0f, 4f, 1f)))
//        yEntry.add(BarEntry(5f, floatArrayOf(pointArray[5], 3f, 1f, 4f, 4f, 0f)))

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
            // xAxis.position = XAxis.XAxisPosition.BOTTOM
            invalidate()
        }
    }
}