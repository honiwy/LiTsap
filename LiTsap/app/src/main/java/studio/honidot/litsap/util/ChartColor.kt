package studio.honidot.litsap.util

import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.R

enum class ChartColor(val value: Int) {
    C1(instance.getColor(R.color.chart_1)),
    C2(instance.getColor(R.color.chart_2)),
    C3(instance.getColor(R.color.chart_3)),
    C4(instance.getColor(R.color.chart_4)),
    C5(instance.getColor(R.color.chart_5)),
    C6(instance.getColor(R.color.chart_6));

    companion object {
        private val list = listOf(C1, C2, C3, C4, C5, C6)

        fun getColor(index: Int): Int {
            return list[index].value
        }
    }

}