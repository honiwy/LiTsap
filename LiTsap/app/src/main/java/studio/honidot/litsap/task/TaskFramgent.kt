package studio.honidot.litsap.task

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.R
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.databinding.FragmentTaskBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.task.create.TaskCreateDialog
import studio.honidot.litsap.util.Logger

class TaskFragment : Fragment() {
    private val viewModel by viewModels<TaskViewModel> {
        getVmFactory(
            TaskFragmentArgs.fromBundle(
                arguments!!
            ).userIdKey
        )
    }

    companion object {
        private const val DIALOG_CREATE = "create"
        private const val TASK_COUNT_LIMIT = 6
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTaskBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerTask.adapter = TaskAdapter(TaskAdapter.OnClickListener {
            viewModel.navigateToDetail(it)
        }, TaskAdapter.OnLongClickListener {
            viewModel.longPressTaskItem(it)
        })
        binding.fab.setOnClickListener {
            Logger.w("YOu press fab, viewModel.taskCount.value: ${viewModel.taskCount.value}")
            viewModel.taskCount.value?.let {
                if (it >= TASK_COUNT_LIMIT) {
                    Toast.makeText(
                        context,
                        LiTsapApplication.instance.getString(R.string.plenty_task_info),
                        Toast.LENGTH_SHORT
                    ).show()
                } else
                    TaskCreateDialog().show(childFragmentManager, DIALOG_CREATE)
            }
        }


        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.actionTaskFragmentToDetailFragment(
                        it
                    )
                )
                viewModel.onDetailNavigated()
            }
        })

        viewModel.longPressTaskItem.observe(this, Observer {
            it?.let {
                attemptToDelete(it)
                viewModel.onlongPressTaskItemFinish()
            }
        })

        viewModel.user.observe(this, Observer {
            it?.let {
                viewModel.retrieveOngoingTasks(it.ongoingTasks)
            }
        })
        return binding.root
    }

    private fun attemptToDelete(task: Task) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setTitle(getString(R.string.task_delete, task.taskName))
        builder.setMessage(getString(R.string.task_delete_info))

        builder.setPositiveButton(
            getString(R.string.task_delete_confirm)
        ) { dialog, which ->
            viewModel.deleteUserOngoingTask(task.userId, task.taskId)
            dialog.dismiss()
        }

        builder.setNegativeButton(
            getString(R.string.task_delete_cancel)
        ) { dialog, which ->
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }
}