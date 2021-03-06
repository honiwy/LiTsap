package studio.honidot.litsap.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.data.History
import studio.honidot.litsap.databinding.ItemHistoryBinding

class HistoryAdapter(val viewModel: DiaryViewModel) :
    ListAdapter<History, HistoryAdapter.ModuleViewHolder>(DiffCallback) {

    class ModuleViewHolder(private var binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: History) {
            history.note = history.note.trim()
            binding.history = history
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return (oldItem === newItem)
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        return ModuleViewHolder(
            ItemHistoryBinding.inflate(
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
        holder.bind(getItem(position))
    }
}