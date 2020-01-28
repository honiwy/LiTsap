package studio.honidot.litsap.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import studio.honidot.litsap.databinding.FragmentTaskBinding
import studio.honidot.litsap.task.create.TaskCreateDialog

class TaskFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTaskBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        // binding.recyclerAwait.adapter = TaskAdapter(viewModel)
        binding.fab.setOnClickListener {
            TaskCreateDialog().show(childFragmentManager, "abc")
        }

        return binding.root
    }
}