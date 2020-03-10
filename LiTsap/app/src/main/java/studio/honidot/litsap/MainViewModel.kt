package studio.honidot.litsap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.honidot.litsap.source.LiTsapRepository
import studio.honidot.litsap.util.CurrentFragmentType

class MainViewModel(private val liTsapRepository: LiTsapRepository) : ViewModel() {

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

}