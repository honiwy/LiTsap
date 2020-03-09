package studio.honidot.litsap.share.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import studio.honidot.litsap.databinding.FragmentPostBinding
import studio.honidot.litsap.extension.getVmFactory

class PostFragment : Fragment() {
    private val viewModel by viewModels<PostViewModel> {
        getVmFactory(
            PostFragmentArgs.fromBundle(
                arguments!!
            ).shareKey, PostFragmentArgs.fromBundle(
                arguments!!
            ).isSameUser
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.recyclerPostGallery.adapter = PostGalleryAdapter()
        binding.recyclerPostCircles.adapter = PostCircleAdapter()

        val linearSnapHelper = LinearSnapHelper().apply {
            attachToRecyclerView(binding.recyclerPostGallery)
        }

        binding.recyclerPostGallery.setOnScrollChangeListener { _, _, _, _, _ ->
            viewModel.onGalleryScrollChange(
                binding.recyclerPostGallery.layoutManager,
                linearSnapHelper
            )
        }

        // set the initial position to the center of infinite gallery
        viewModel.share.value?.let { share ->
            binding.recyclerPostGallery
                .scrollToPosition(share.imageUris.size * 100)

            viewModel.snapPosition.observe(this, Observer {
                (binding.recyclerPostCircles.adapter as PostCircleAdapter).selectedPosition.value =
                    (it % share.imageUris.size)
            })
        }

        viewModel.leavePost.observe(this, Observer {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        })

        return binding.root
    }

}