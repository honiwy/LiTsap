package studio.honidot.litsap.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.data.TaskTab
import studio.honidot.litsap.databinding.ItemProfileCompetitionTabBinding
import studio.honidot.litsap.util.Logger

class CompetitionAdapter(val viewModel: ProfileViewModel, private val onClickListener: OnClickListener) : ListAdapter<TaskTab, CompetitionAdapter.TaskTabViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (taskTab:TaskTab) -> Unit) {
        fun onClick(taskTab:TaskTab) = clickListener(taskTab)
    }

    class TaskTabViewHolder(
        private var binding: ItemProfileCompetitionTabBinding,
        private val viewModel: ProfileViewModel): RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        val isSelected: LiveData<Boolean> = Transformations.map(viewModel.selectedTaskPosition) {
            it == adapterPosition
        }

        fun bind(taskTab:TaskTab, onClickListener: OnClickListener) {
            binding.lifecycleOwner = this
            binding.taskTab = taskTab
            binding.viewHolder = this
            binding.viewModel = viewModel
            binding.root.setOnClickListener {
                viewModel.selectedTaskPosition.value = adapterPosition
                onClickListener.onClick(taskTab)
            }
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun markAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun markDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Product]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<TaskTab>() {
        override fun areItemsTheSame(oldItem: TaskTab, newItem: TaskTab): Boolean {
            return (oldItem === newItem)
        }

        override fun areContentsTheSame(oldItem: TaskTab, newItem: TaskTab): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskTabViewHolder {
        return TaskTabViewHolder(ItemProfileCompetitionTabBinding.inflate(LayoutInflater.from(parent.context), parent, false), viewModel)
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: TaskTabViewHolder, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }

    override fun onViewAttachedToWindow(holder: TaskTabViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: TaskTabViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}
