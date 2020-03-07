package studio.honidot.litsap.share.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import studio.honidot.litsap.databinding.FragmentShareItemBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.share.ShareTypeFilter

class ShareItemFragment(private val shareType: ShareTypeFilter) : Fragment() {

    private val viewModel by viewModels<ShareItemViewModel> { getVmFactory(shareType) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentShareItemBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        return binding.root
    }

}