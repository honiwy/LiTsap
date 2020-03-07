package studio.honidot.litsap.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.android.material.tabs.TabLayout
import studio.honidot.litsap.databinding.FragmentDiaryBinding
import studio.honidot.litsap.databinding.FragmentShareBinding
import studio.honidot.litsap.diary.DiaryFragmentArgs
import studio.honidot.litsap.diary.DiaryViewModel
import studio.honidot.litsap.diary.HistoryAdapter
import studio.honidot.litsap.extension.getVmFactory

class ShareFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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