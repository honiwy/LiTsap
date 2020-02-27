package studio.honidot.litsap.task.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import studio.honidot.litsap.databinding.FragmentDetailBinding
import studio.honidot.litsap.extension.getVmFactory
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.data.Module
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
                findNavController().navigate(NavigationDirections.navigateToWorkoutFragment(it))
                viewModel.onWorkoutNavigated()
            }
        })
        binding.recyclerModuleDetail.adapter = DetailModuleAdapter(viewModel)

        viewModel.awaitDrawModules.observe(this, Observer {
            it?.let {
                drawPieChart(binding.piechart, it)
            }
        })

        return binding.root
    }

    private fun drawPieChart(chart: PieChart, modules: List<Module>) {
        val colorTable = listOf("#41a8d1", "#f8cd72", "#bdd176", "#81ce8f", "#45c6af", "#15b9c8")
        val yEntry = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        modules.forEach { module ->
            if (module.achieveSection > 0) {
                colors.add(Color.parseColor(colorTable[colors.size]))
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
            setExtraOffsets(10f,20f,10f,20f)
            chart.description.isEnabled = false
            setTransparentCircleAlpha(0)
            setEntryLabelColor(Color.BLACK)
            setUsePercentValues(true)
            chart.legend.isEnabled = false
            invalidate()
        }
    }

    class PieChartValueFormatter : ValueFormatter() {
        private val format = DecimalFormat("##0.0")
        override fun getFormattedValue(value: Float): String {
            return format.format(value) + " %"
        }
    }
}