package studio.honidot.litsap.task.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import studio.honidot.litsap.databinding.FragmentDetailBinding
import studio.honidot.litsap.extension.getVmFactory
import androidx.navigation.fragment.findNavController
import studio.honidot.litsap.NavigationDirections


class DetailFragment : Fragment() {
    private val viewModel by viewModels<DetailViewModel> {
        getVmFactory(
            DetailFragmentArgs.fromBundle(
                arguments!!
            ).taskKey
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.leaveDetail.observe(this, Observer {
            it?.let {
                if (it) findNavController().navigateUp()
            }
        })
        viewModel.navigateToWorkout.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToWorkoutFragment(it))
                viewModel.onWorkoutNavigated()
            }
        })
        binding.recyclerModuleDetail.adapter = DetailModuleAdapter(viewModel)
        viewModel.addDataSet(binding.piechart)

        return binding.root
    }


}