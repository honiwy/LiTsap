package studio.honidot.litsap.task.finish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import studio.honidot.litsap.databinding.FragmentFinishBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.task.workout.RecordAdapter
import studio.honidot.litsap.task.workout.WorkoutFragmentArgs
import studio.honidot.litsap.task.workout.WorkoutViewModel
import studio.honidot.litsap.util.Logger

class FinishFragment : Fragment() {
    private val viewModel by viewModels<FinishViewModel> {
        getVmFactory(
            FinishFragmentArgs.fromBundle(
                arguments!!
            ).finishKey
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFinishBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerFootprint.adapter = FootprintAdapter()
        viewModel.workout.observe(this, Observer {
            Logger.w("Hello viewModel.workout.observe: ${it}")
        })

        return binding.root
    }


}