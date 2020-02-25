package studio.honidot.litsap.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.data.TaskTab
import studio.honidot.litsap.databinding.ItemProfileCompetitionTabBinding

class CompetitionAdapter(val viewModel: ProfileViewModel) : ListAdapter<TaskTab, CompetitionAdapter.ModuleViewHolder>(DiffCallback) {

    class ModuleViewHolder(private var binding: ItemProfileCompetitionTabBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(taskTab:TaskTab) {
            binding.taskTab = taskTab
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        return ModuleViewHolder(ItemProfileCompetitionTabBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
