package studio.honidot.litsap.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import studio.honidot.litsap.databinding.FragmentShareBinding

class ShareFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentShareBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ShareFragment
            viewpagerShare.let {
                tabsShare.setupWithViewPager(it)
                it.adapter = ShareAdapter(childFragmentManager)
                it.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabsShare))
            }
            return@onCreateView root
        }
    }
}