package studio.honidot.litsap

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
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