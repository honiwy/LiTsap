package studio.honidot.litsap.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
import studio.honidot.litsap.databinding.FragmentTaskBinding
import studio.honidot.litsap.task.create.TaskCreateDialog

class TaskFragment : Fragment() {
    private val viewModel: TaskViewModel by lazy {
        ViewModelProviders.of(this).get(TaskViewModel::class.java)
    }//要用到的時候再創建才不會浪費記憶體資源

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTaskBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerTask.adapter = TaskAdapter(TaskAdapter.OnClickListener {
            //viewModel.navigateToDetail(it)
        })
        binding.fab.setOnClickListener {
            viewModel.taskItems.value?.let {
                if (it.size >= 8) {
                    Toast.makeText(context, LiTsapApplication.instance.getString(R.string.plenty_task_info), Toast.LENGTH_SHORT).show()
                } else
                    TaskCreateDialog().show(childFragmentManager, "abc")
            }
        }

        return binding.root
    }
}