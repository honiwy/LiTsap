package studio.honidot.litsap.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.data.FireTask
import studio.honidot.litsap.data.TaskInfo
import studio.honidot.litsap.data.TaskItem
import studio.honidot.litsap.databinding.ItemTaskBinding
import studio.honidot.litsap.databinding.ItemTaskClassBinding

class TaskAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<TaskItem, RecyclerView.ViewHolder>(DiffCallback) {
    class OnClickListener(val clickListener: (taskInfo: FireTask) -> Unit) {
        fun onClick(taskInfo: FireTask) = clickListener(taskInfo)
    }

    class TitleViewHolder(private var binding: ItemTaskClassBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String) {
            binding.title = title
            binding.executePendingBindings()
        }
    }

    class AssignmentViewHolder(private var binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(taskInfo: FireTask, onClickListener: OnClickListener) {
            binding.taskInfo = taskInfo
            binding.root.setOnClickListener { onClickListener.onClick(taskInfo) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TaskItem>() {
        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return oldItem.id == newItem.id
        }

        private const val ITEM_VIEW_TYPE_TITLE = 0x00
        private const val ITEM_VIEW_TYPE_ASSIGNMENT = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_TITLE -> TitleViewHolder(
                ItemTaskClassBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ITEM_VIEW_TYPE_ASSIGNMENT -> AssignmentViewHolder(
                ItemTaskBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is TitleViewHolder -> {
                holder.bind((getItem(position) as TaskItem.Title).title)
            }
            is AssignmentViewHolder -> {
                holder.bind((getItem(position) as TaskItem.Assignment).taskInfo, onClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TaskItem.Title -> ITEM_VIEW_TYPE_TITLE
            is TaskItem.Assignment -> ITEM_VIEW_TYPE_ASSIGNMENT
        }
    }
}