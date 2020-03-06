package studio.honidot.litsap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.CurrentFragmentType
import studio.honidot.litsap.util.DrawerToggleType

class MainViewModel(private val liTsapRepository: LiTsapRepository) : ViewModel() {

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    // According to current fragment to change different drawer toggle
    val currentDrawerToggleType: LiveData<DrawerToggleType> =
        Transformations.map(currentFragmentType) {
            when (it) {
                CurrentFragmentType.DETAIL -> DrawerToggleType.BACK
                else -> DrawerToggleType.NORMAL
            }
        }
}