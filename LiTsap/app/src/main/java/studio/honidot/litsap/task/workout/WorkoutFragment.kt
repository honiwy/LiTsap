package studio.honidot.litsap.task.workout

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import studio.honidot.litsap.databinding.FragmentWorkoutBinding
import studio.honidot.litsap.extension.getVmFactory
import androidx.navigation.fragment.findNavController
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.task.create.ModuleCreateAdapter
import studio.honidot.litsap.util.Logger


class WorkoutFragment : Fragment() {
    private val viewModel by viewModels<WorkoutViewModel> {
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
        val binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigateToFinish.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToFinishFragment(it))
                viewModel.onFinishNavigated()
            }
        })

        val adapter = RecordAdapter(viewModel)
        binding.recyclerMessage.adapter = adapter
        viewModel.messageList.observe(this, Observer {
            binding.recyclerMessage.adapter?.notifyDataSetChanged()
            binding.recyclerMessage.smoothScrollToPosition(adapter.itemCount)
        })


        binding.editTalk.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.addMessage()
                true
            } else false
        }

        viewModel.leaveWorkout.observe(this, Observer {
            it?.let {
                if (it) findNavController().navigateUp()
            }
        })
        return binding.root
    }


}