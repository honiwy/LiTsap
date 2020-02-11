package studio.honidot.litsap

import android.text.format.DateFormat
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.task.TaskAdapter
import studio.honidot.litsap.task.create.ModuleCreateAdapter
import studio.honidot.litsap.task.detail.DetailModuleAdapter
import studio.honidot.litsap.util.CurrentFragmentType
import java.util.*

//Task List
@BindingAdapter("taskItems")
fun bindRecyclerViewWithTaskItems(recyclerView: RecyclerView, taskItems: List<TaskItem>?) {
    taskItems?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is TaskAdapter -> submitList(it)
            }
        }
    }
}


//Global
@BindingAdapter("taskCategory")
fun bindTaskCategories(imageView: ImageView, categoryId: Int) {

    val taskCategory= TaskCategory.values()[categoryId]

    imageView.background =
        when (taskCategory) {
            TaskCategory.EXERCISE -> instance.getDrawable(R.drawable.category_exercise)
            TaskCategory.FOOD -> instance.getDrawable(R.drawable.category_food)
            TaskCategory.STUDY -> instance.getDrawable(R.drawable.category_study)
            TaskCategory.WEALTH -> instance.getDrawable(R.drawable.category_wealth)
            TaskCategory.NETWORKING -> instance.getDrawable(R.drawable.category_networking)
            else -> instance.getDrawable(R.drawable.category_other)
        }
}

@BindingAdapter("toolbarVisibility")
fun bindToolbarVisibility(view: View, fragment: CurrentFragmentType) {
   view.visibility =
        when (fragment) {
            CurrentFragmentType.TASK  -> View.VISIBLE
            CurrentFragmentType.POST -> View.VISIBLE
            else -> View.GONE
        }
}

@BindingAdapter("bottomNavVisibility")
fun bindBottomNavVisibility(view: View, fragment: CurrentFragmentType) {
    view.visibility =
        when (fragment) {
            CurrentFragmentType.TASK  -> View.VISIBLE
            CurrentFragmentType.POST -> View.VISIBLE
            CurrentFragmentType.PROFILE -> View.VISIBLE
            else -> View.GONE
        }
}
//Task Create
@BindingAdapter("tags")
fun bindRecyclerViewWithTags(recyclerView: RecyclerView, tags: List<String>?) {
    tags?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is ModuleCreateAdapter -> submitList(it)
            }
        }
    }
}

//Task Detail
@BindingAdapter("modules")
fun bindRecyclerViewWithModules(recyclerView: RecyclerView, modules: List<Module>?) {
    modules?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is DetailModuleAdapter -> submitList(it)
            }
        }
    }
}

@BindingAdapter("layoutMarginStart")
fun setLayoutMarginStart(view: View, dimen: Float) {
    val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.marginStart = dimen.toInt()
    view.layoutParams = layoutParams
}

@BindingAdapter("time")
fun bindTime(textView: TextView, time: Int?) {
    time?.let { textView.text = instance.getString(R.string.task_time, it * 20) }
}

//Task CountDown
@BindingAdapter("countDownTime")
fun bindCountDownTime(textView: TextView, time: Int) {
    val min = time / 60
    val sec = time - min * 60
    textView.text = String.format("%02d:%02d", min, sec)
}


@BindingAdapter("timerStampConverter")
fun bindTimeStamp(textView: TextView, timeStamp: com.google.firebase.Timestamp) {
    textView.text = DateFormat.format("yyyy 年 MM 月 dd 日",timeStamp.toDate()).toString()
}

@BindingAdapter("timerLongConverter")
fun bindTimeLong(textView: TextView, timeLong: Long) {
    textView.text = DateFormat.format("yyyy 年 MM 月 dd 日",Date(timeLong)).toString()
}