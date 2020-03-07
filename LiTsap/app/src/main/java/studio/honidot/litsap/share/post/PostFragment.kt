package studio.honidot.litsap.share.post

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import studio.honidot.litsap.databinding.FragmentPostBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.util.Logger

class PostFragment : Fragment() {
    private val viewModel by viewModels<PostViewModel> {
        getVmFactory(
            PostFragmentArgs.fromBundle(
                arguments!!
            ).shareKey
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
        viewModel.leavePost.observe(this, Observer {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        })

        viewModel.share.observe(this, Observer {
            it?.let {
                Logger.d("$it")
            }
        })

        return binding.root
    }

}