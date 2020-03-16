package studio.honidot.litsap.share.item

import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
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

    class PictureOutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            view.clipToOutline = true
            val radius: Int = LiTsapApplication.instance.resources
                .getDimensionPixelSize(R.dimen.radius_corner)
            outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
        }
    }

    class ShareViewHolder(private var binding: ItemShareGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            share: Share,
            onClickListener: OnClickListener
        ) {
            binding.share = share
            binding.imageShareMainImg.outlineProvider = PictureOutlineProvider()
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

