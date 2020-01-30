package studio.honidot.litsap.util

import studio.honidot.litsap.R
import studio.honidot.litsap.util.Util.getString

enum class CurrentFragmentType(val value: String) {
    TASKLIST(""),
    TASKCREATED(getString(R.string.create_task)),
    DETAIL(getString(R.string.task_detail))
}