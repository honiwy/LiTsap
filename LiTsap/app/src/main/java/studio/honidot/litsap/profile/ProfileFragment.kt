package studio.honidot.litsap.profile

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R
import studio.honidot.litsap.data.History
import studio.honidot.litsap.databinding.FragmentProfileBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.profile.face.FaceChooseDialog
import studio.honidot.litsap.util.ChartColor
import studio.honidot.litsap.util.Logger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {
    private val viewModel by viewModels<ProfileViewModel> {
        getVmFactory(
            ProfileFragmentArgs.fromBundle(
                arguments!!
            ).userIdKey
        )
    }
    lateinit var mHandler:Handler
    lateinit var runnable: Runnable
    companion object {
        private const val BAR_CHART_DRAW_DAYS = 7
        private const val ONE_DAY_MILLI_SECOND = 86400 * 1000
        private const val CHART_ANIMATION_TIME = 1000
        private const val DIALOG_FACE = "face"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.imageProfileAvatar.setOnClickListener {
            FaceChooseDialog().show(childFragmentManager, DIALOG_FACE)
        }


        binding.textProfileInfo.isSelected = true
        binding.textProfileInfo.ellipsize = TextUtils.TruncateAt.MARQUEE
        binding.recyclerTab.adapter =
            CompetitionAdapter(viewModel, CompetitionAdapter.OnClickListener {
                viewModel.getMurmur(it.groupId)

            })

        viewModel.murmurs.observe(this, Observer {
            binding.recyclerMurmur.adapter?.notifyDataSetChanged()
        })

        mHandler = Handler(Looper.getMainLooper())
        var count = 0

        runnable = object : Runnable {
            override fun run() {
                binding.recyclerMurmur.smoothScrollToPosition(
                    if (count >= binding.recyclerMurmur.adapter?.itemCount ?: 0) {
                        count = 0
                        0
                    } else {
                        count++
                    }
                )
                mHandler.postDelayed(this, 2000)
            }
        }

        val adapter = MurmurAdapter(viewModel)
        binding.recyclerMurmur.adapter = adapter

    mHandler.postDelayed(runnable, 2000)


        viewModel.historyPoints.observe(this, Observer {
            it?.let {
                drawBarChart(
                    binding.barChart,
                    it,
                    BAR_CHART_DRAW_DAYS,
                    viewModel.user.value!!.ongoingTasks.size
                )
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mHandler.removeCallbacks(runnable)
    }

    private fun drawBarChart(
        chart: BarChart,
        history: List<History>,
        dayCount: Int,
        taskCount: Int
    ) {
        val formatter = DateTimeFormatter.ofPattern(
            instance.getString(R.string.barchart_month_date)
        )

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
            formColor = ChartColor.getColor(taskIndex)
        }
        colors.add(ChartColor.getColor(taskIndex))
        legends.add(legendEntry)
        sortedHistory.forEach {
            if (it.taskName != name) { // found a different task
                taskIndex += 1
                val legendEntry = LegendEntry().apply {
                    label = it.taskName
                    formColor = ChartColor.getColor(taskIndex)
                }
                colors.add(ChartColor.getColor(taskIndex))
                legends.add(legendEntry)
                name = it.taskName
            }
            for (i in dayCount downTo 1) {
                val timeFrom = (Calendar.getInstance().timeInMillis - ONE_DAY_MILLI_SECOND * i)
                val timeTo = (Calendar.getInstance().timeInMillis - ONE_DAY_MILLI_SECOND * (i - 1))
                if (it.recordDate in timeFrom until timeTo) {
                    pointArrayList[dayCount - i][taskIndex] += it.achieveCount.toFloat()
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
            description.isEnabled = false
            xAxis.valueFormatter = IndexAxisValueFormatter(xDate)
            legend.setCustom(legends)
            legend.form = Legend.LegendForm.CIRCLE
            legend.isWordWrapEnabled = true
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            animateY(CHART_ANIMATION_TIME)
            setScaleEnabled(false)
            invalidate()
            notifyDataSetChanged()
        }
    }
}