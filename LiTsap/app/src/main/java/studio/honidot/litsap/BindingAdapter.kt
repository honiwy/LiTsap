package studio.honidot.litsap

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.LiTsapApplication.Companion.instance
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.task.TaskAdapter

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

@BindingAdapter("taskCategory")
fun bindTaskCategories(imageView: ImageView, category: TaskCategory) {
    category?.let {
        imageView.background =
            when (it) {
            TaskCategory.EXERCISE ->  instance.getDrawable(R.drawable.category_exercise)
                TaskCategory.FOOD ->  instance.getDrawable(R.drawable.category_food)
                TaskCategory.STUDY ->  instance.getDrawable(R.drawable.category_study)
                TaskCategory.WEALTH ->  instance.getDrawable(R.drawable.category_wealth)
                TaskCategory.NETWORKING ->  instance.getDrawable(R.drawable.category_networking)
            else -> instance.getDrawable(R.drawable.category_other)
        }
    }
}