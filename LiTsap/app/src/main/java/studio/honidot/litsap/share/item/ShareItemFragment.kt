package studio.honidot.litsap.share.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.databinding.FragmentShareItemBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.network.LoadApiStatus
import studio.honidot.litsap.share.ShareTypeFilter
import studio.honidot.litsap.util.Logger

class ShareItemFragment(private val shareType: ShareTypeFilter) : Fragment() {

    private val viewModel by viewModels<ShareItemViewModel> { getVmFactory(shareType) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentShareItemBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.findUser(FirebaseAuth.getInstance().currentUser!!)


        binding.recyclerShareItems.adapter = ShareItemAdapter(viewModel,ShareItemAdapter.OnClickListener {
            viewModel.navigateToPost(it)
        })

        viewModel.shareList.observe(this, Observer {
                Logger.w("Find $it")
        })

        viewModel.navigateToPost.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToPostFragment(it))
                viewModel.onPostNavigated()
            }
        })


        return binding.root
    }


}