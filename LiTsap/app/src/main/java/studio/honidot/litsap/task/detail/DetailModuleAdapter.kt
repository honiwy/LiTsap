package studio.honidot.litsap.task.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.databinding.ItemDetailModuleBinding

class DetailModuleAdapter : ListAdapter<Module, DetailModuleAdapter.ModuleViewHolder>(DiffCallback) {

    class ModuleViewHolder(private var binding: ItemDetailModuleBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(module: Module) {
            module.let {
                binding.module = it
                binding.executePendingBindings()
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Module>() {
        override fun areItemsTheSame(oldItem: Module, newItem: Module): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Module, newItem: Module): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        return ModuleViewHolder(
            ItemDetailModuleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}