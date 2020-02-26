package studio.honidot.litsap.profile.face

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studio.honidot.litsap.data.TaskTab
import studio.honidot.litsap.databinding.ItemProfileCompetitionTabBinding
import studio.honidot.litsap.databinding.ItemProfileFaceBinding
import studio.honidot.litsap.profile.ProfileViewModel

class FaceAdapter(val viewModel: FaceChooseViewModel, private val onClickListener: OnClickListener) : ListAdapter<Int, FaceAdapter.FaceViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (faceId: Int) -> Unit) {
        fun onClick(faceId: Int) = clickListener(faceId)
    }

    class FaceViewHolder(
        private var binding: ItemProfileFaceBinding,
        private val viewModel: FaceChooseViewModel
    ): RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        val isSelected: LiveData<Boolean> = Transformations.map(viewModel.selectedFacePosition) {
            it == adapterPosition
        }

        fun bind(faceId: Int, onClickListener: OnClickListener) {
            binding.lifecycleOwner = this
            binding.viewHolder = this
            binding.iconId = adapterPosition
            binding.viewModel = viewModel
            binding.root.setOnClickListener {
                viewModel.selectedFacePosition.value = adapterPosition
                onClickListener.onClick(faceId)
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
    companion object DiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return (oldItem === newItem)
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaceViewHolder {
        return FaceViewHolder(ItemProfileFaceBinding.inflate(LayoutInflater.from(parent.context), parent, false), viewModel)
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: FaceViewHolder, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }

    override fun onViewAttachedToWindow(holder: FaceViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: FaceViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}