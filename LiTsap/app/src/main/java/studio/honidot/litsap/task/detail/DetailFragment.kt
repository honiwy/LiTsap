package studio.honidot.litsap.task.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.R
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.databinding.FragmentDetailBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.extension.setTouchDelegate
import studio.honidot.litsap.util.ChartColor
import java.text.DecimalFormat

class DetailFragment : Fragment() {
    private val viewModel by viewModels<DetailViewModel> {
        getVmFactory(
            DetailFragmentArgs.fromBundle(
                arguments!!
            ).taskKey
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.leaveDetail.observe(this, Observer {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        })
        viewModel.navigateToWorkout.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionDetailFragmentToWorkoutFragment(it))
                viewModel.onWorkoutNavigated()
            }
        })
        binding.recyclerModuleDetail.adapter = DetailModuleAdapter(viewModel)

        binding.imageBack.setTouchDelegate()

        viewModel.awaitDrawModules.observe(this, Observer {
            it?.let {
                drawPieChart(binding.piechart, it)
            }
        })

        return binding.root
    }

    private fun drawPieChart(chart: PieChart, modules: List<Module>) {
        val yEntry = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        modules.forEach { module ->
            if (module.achieveSection > 0) {
                colors.add(ChartColor.getColor(colors.size))
                yEntry.add(PieEntry(module.achieveSection.toFloat(), module.moduleName))
            }
        }
        val pieDataSet = PieDataSet(yEntry, "")

        pieDataSet.colors = colors
//        pieDataSet.setDrawValues(false)

        pieDataSet.apply {
            valueTextSize = 12f
            valueLinePart1OffsetPercentage = 110f
            valueLinePart1Length = 1f
            valueLinePart2Length = 0.8f
            valueFormatter = PieChartValueFormatter()
            isUsingSliceColorAsValueLineColor = true
            valueLineWidth = 1f
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            sliceSpace = 2f
        }

        chart.apply {
            data = PieData(pieDataSet)
            holeRadius = 20f
            setExtraOffsets(45f, 20f, 45f, 20f)
            chart.description.isEnabled = false
            setTransparentCircleAlpha(0)
            setEntryLabelColor(Color.BLACK)
            setUsePercentValues(true)
            chart.legend.isEnabled = false
            animateY(CHART_ANIMATION_TIME)
            invalidate()
        }
    }

    class PieChartValueFormatter : ValueFormatter() {
        private val format = DecimalFormat(LiTsapApplication.instance.getString(R.string.detail_chart_format))
        override fun getFormattedValue(value: Float): String {
            return format.format(value/100)
        }
    }

    companion object {
        private const val CHART_ANIMATION_TIME = 1000
    }
}