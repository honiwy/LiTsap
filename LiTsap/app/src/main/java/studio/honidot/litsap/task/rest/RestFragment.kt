package studio.honidot.litsap.task.rest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import studio.honidot.litsap.databinding.FragmentRestBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.task.workout.WorkoutFragmentArgs

class RestFragment : Fragment() {
    private val viewModel by viewModels<RestViewModel> {
        getVmFactory(
            WorkoutFragmentArgs.fromBundle(
                arguments!!
            ).workoutKey
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRestBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.leaveRest.observe(this, Observer {
            it?.let {
                if (it) findNavController().navigateUp()
            }
        })
        return binding.root
    }


}