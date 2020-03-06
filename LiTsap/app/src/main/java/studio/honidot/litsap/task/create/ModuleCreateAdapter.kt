package studio.honidot.litsap.task.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.databinding.ItemTaskModuleBinding

class ModuleCreateAdapter(val viewModel: TaskCreateViewModel) :
    ListAdapter<String, ModuleCreateAdapter.ModuleViewHolder>(DiffCallback) {

    class ModuleViewHolder(private var binding: ItemTaskModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(module: String, viewModel: TaskCreateViewModel) {
            binding.module = module
            binding.viewModel = viewModel
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return (oldItem === newItem)
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        return ModuleViewHolder(
            ItemTaskModuleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel)
    }
}