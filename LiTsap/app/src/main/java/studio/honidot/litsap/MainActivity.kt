package studio.honidot.litsap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import studio.honidot.litsap.LiTsapApplication.Companion.db
import studio.honidot.litsap.databinding.ActivityMainBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.util.CurrentFragmentType

class MainActivity : AppCompatActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()


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
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToTaskFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_post-> {
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToPostFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.detailFragment -> CurrentFragmentType.DETAIL
                R.id.postFragment -> CurrentFragmentType.POST
                R.id.taskFragment -> CurrentFragmentType.TASK
                else -> viewModel.currentFragmentType.value
            }
        }
    }


}
