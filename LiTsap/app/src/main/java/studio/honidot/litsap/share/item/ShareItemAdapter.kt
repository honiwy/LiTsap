package studio.honidot.litsap.share.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.data.Share
import studio.honidot.litsap.databinding.ItemShareGridBinding

class ShareItemAdapter(
    val viewModel: ShareItemViewModel,
    private val onClickListener: OnClickListener
) :
    ListAdapter<Share, ShareItemAdapter.ShareViewHolder>(DiffCallback) {


    class OnClickListener(val clickListener: (share: Share) -> Unit) {
        fun onClick(share: Share) = clickListener(share)
    }

    class ShareViewHolder(private var binding: ItemShareGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            share: Share,
            onClickListener: OnClickListener
        ) {
            binding.share = share
            binding.root.setOnClickListener { onClickListener.onClick(share) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Share>() {
        override fun areItemsTheSame(oldItem: Share, newItem: Share): Boolean {
            return oldItem.shareId == newItem.shareId
        }

        override fun areContentsTheSame(oldItem: Share, newItem: Share): Boolean {
            return oldItem.shareId == newItem.shareId
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareViewHolder {
        return ShareViewHolder(
            ItemShareGridBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ShareViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            onClickListener
        )

    }
}

