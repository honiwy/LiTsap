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
import studio.honidot.litsap.LiTsapApplication
import androidx.navigation.fragment.findNavController
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.R
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.databinding.FragmentTaskBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.task.create.TaskCreateDialog
import studio.honidot.litsap.util.Logger

class TaskFragment : Fragment() {
    private val viewModel by viewModels<TaskViewModel> { getVmFactory(
        TaskFragmentArgs.fromBundle(
            arguments!!
        ).userIdKey
    ) }

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
        },TaskAdapter.OnLongClickListener {
            viewModel.longPressTaskItem(it)
        })
        binding.fab.setOnClickListener {
            viewModel.taskItems.value?.let {
                if (it.size >= (6+2)) {
                    Toast.makeText(context, LiTsapApplication.instance.getString(R.string.plenty_task_info), Toast.LENGTH_SHORT).show()
                } else
                    TaskCreateDialog().show(childFragmentManager, "abc")
            }
        }

        viewModel.navigateToDetail.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToDetailFragment(it))
                viewModel.onDetailNavigated()
            }
        })

        viewModel.longPressTaskItem.observe(this, Observer {
            it?.let{
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

        builder.setTitle("刪除 ${task.taskName} 任務")
        builder.setMessage("確定刪除任務嗎?\n所有歷史足跡將隨之刪除。")

        builder.setPositiveButton("確認"
        ) { dialog, which ->
            viewModel.deleteUserOngoingTask(task.userId,task.taskId)
            dialog.dismiss()
        }

        builder.setNegativeButton("取消"
        ) { dialog, which ->
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }
}