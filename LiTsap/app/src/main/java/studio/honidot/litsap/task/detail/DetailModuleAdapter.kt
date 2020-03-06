package studio.honidot.litsap.task.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.databinding.ItemDetailModuleBinding


class DetailModuleAdapter(val viewModel: DetailViewModel) :
    ListAdapter<Module, DetailModuleAdapter.ModuleViewHolder>(DiffCallback) {

    private var checkedPosition = 0

    class ModuleViewHolder(private var binding: ItemDetailModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val radioModule = binding.radioModule

        fun bind(module: Module, isChecked: Boolean) {
            module.let {
                binding.module = it
                binding.radioModule.isChecked = isChecked
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
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            checkedPosition == position
        )
        holder.radioModule.setOnClickListener {
            if (checkedPosition != position) {
                notifyItemChanged(checkedPosition)
                checkedPosition = position
                viewModel.changeModule(checkedPosition)
            }
        }

    }

}

