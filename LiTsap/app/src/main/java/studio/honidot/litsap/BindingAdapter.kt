package studio.honidot.litsap

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.task.TaskAdapter
import studio.honidot.litsap.task.detail.DetailModuleAdapter
import studio.honidot.litsap.util.CurrentFragmentType
import studio.honidot.litsap.util.Util.getColor

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
fun bindTaskCategories(imageView: ImageView, category: TaskCategory) {
    imageView.background =
        when (category) {
            TaskCategory.EXERCISE -> instance.getDrawable(R.drawable.category_exercise)
            TaskCategory.FOOD -> instance.getDrawable(R.drawable.category_food)
            TaskCategory.STUDY -> instance.getDrawable(R.drawable.category_study)
            TaskCategory.WEALTH -> instance.getDrawable(R.drawable.category_wealth)
            TaskCategory.NETWORKING -> instance.getDrawable(R.drawable.category_networking)
            else -> instance.getDrawable(R.drawable.category_other)
        }
}

@BindingAdapter("upDownVisibility")
fun bindVisibility(view: View, fragment: CurrentFragmentType) {
   view.visibility =
        when (fragment) {
            CurrentFragmentType.TASK  -> View.VISIBLE
            CurrentFragmentType.POST -> View.VISIBLE
            CurrentFragmentType.PROFILE -> View.VISIBLE
            else -> View.GONE
        }
}

//Tssk Create
@BindingAdapter("editorControllerStatus")
fun bindEditorControllerStatus(imageButton: ImageButton, enabled: Boolean) {

    imageButton.apply {
        foreground = ShapeDrawable(object : Shape() {
            override fun draw(canvas: Canvas, paint: Paint) {

                paint.color = getColor(R.color.black)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = instance.resources
                    .getDimensionPixelSize(R.dimen.edge_create_select).toFloat()
                canvas.drawRect(0f, 0f, this.width, this.height, paint)
            }
        })
        isClickable = enabled
        backgroundTintList = ColorStateList.valueOf(
            getColor(
                when (enabled) {
                    true -> R.color.black_3f3a3a
                    false -> R.color.gray_999999
                }))
        foregroundTintList = ColorStateList.valueOf(
            getColor(
                when (enabled) {
                    true -> R.color.black_3f3a3a
                    false -> R.color.gray_999999
                }))
    }
}

@BindingAdapter("amount", "stock")
fun bindEditorStatus(textView: TextView, amount: Long, stock: Int) {
    textView.apply {
        background = ShapeDrawable(object : Shape() {
            override fun draw(canvas: Canvas, paint: Paint) {

                paint.color = android.graphics.Color.BLACK
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = instance.resources
                    .getDimensionPixelSize(R.dimen.edge_create_select).toFloat()
                canvas.drawRect(0f, 0f, this.width, this.height, paint)
            }
        })
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