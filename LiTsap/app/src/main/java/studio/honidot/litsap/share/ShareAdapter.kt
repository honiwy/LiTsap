package studio.honidot.litsap.share

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import studio.honidot.litsap.share.item.ShareItemFragment

class ShareAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return ShareItemFragment(ShareTypeFilter.values()[position])
    }

    override fun getCount() = ShareTypeFilter.values().size

    override fun getPageTitle(position: Int): CharSequence? {
        return ShareTypeFilter.values()[position].value
    }
}