package studio.honidot.litsap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import studio.honidot.litsap.databinding.ActivityMainBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.util.CurrentFragmentType
import studio.honidot.litsap.util.Logger

class MainActivity : AppCompatActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)




        setupNavController()
    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navigation_task -> {
                    findNavController(R.id.myNavHostFragment).navigate(
                        NavigationDirections.navigateToTaskFragment(
                            FirebaseAuth.getInstance().currentUser!!.uid
                        )
                    )
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_share -> {
                    findNavController(R.id.myNavHostFragment).navigate(
                        NavigationDirections.navigateToShareFragment(
                        )
                    )
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_diary -> {
                    findNavController(R.id.myNavHostFragment).navigate(
                        NavigationDirections.navigateToDiaryFragment(
                            FirebaseAuth.getInstance().currentUser!!.uid
                        )
                    )
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    findNavController(R.id.myNavHostFragment).navigate(
                        NavigationDirections.navigateToProfileFragment(
                            FirebaseAuth.getInstance().currentUser!!.uid
                        )
                    )
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.shareFragment -> CurrentFragmentType.SHARE
                R.id.detailFragment -> CurrentFragmentType.DETAIL
                R.id.diaryFragment -> CurrentFragmentType.DIARY
                R.id.taskFragment -> CurrentFragmentType.TASK
                R.id.postFragment -> CurrentFragmentType.POST
                R.id.loginFragment -> CurrentFragmentType.LOGIN
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.d("activity onActivityResult")
    }
}
