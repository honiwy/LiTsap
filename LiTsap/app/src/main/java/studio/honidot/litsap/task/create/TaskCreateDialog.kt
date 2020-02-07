package studio.honidot.litsap.task.create

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import studio.honidot.litsap.LiTsapApplication.Companion.appContext
import studio.honidot.litsap.R
import studio.honidot.litsap.TaskCategory
import studio.honidot.litsap.databinding.DialogCreateTaskBinding


class TaskCreateDialog : DialogFragment() {

    private val viewModel: TaskCreateViewModel by lazy {
        ViewModelProviders.of(this).get(TaskCreateViewModel::class.java)
    }//要用到的時候再創建才不會浪費記憶體資源

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DialogCreateTaskBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_create_task, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val arrayCategory = arrayOf(
            TaskCategory.EXERCISE,
            TaskCategory.FOOD,
            TaskCategory.STUDY,
            TaskCategory.NETWORKING,
            TaskCategory.WEALTH,
            TaskCategory.OTHER
        )
        binding.spinnerTaskCategories.adapter = CategorySpinnerAdapter(
            appContext.resources.getStringArray(
                R.array.task_category_list
            ), arrayCategory
        )
        val adapter = ModuleCreateAdapter(viewModel)
        binding.recyclerModule.adapter = adapter
        viewModel.moduleNameList.observe(this, Observer {
            binding.recyclerModule.adapter?.notifyDataSetChanged()
            binding.recyclerModule.smoothScrollToPosition(adapter.itemCount)
        })

//        viewModel.taskCategory.observe(this, Observer {
//            Log.i("HAHA","viewModel.taskCategory.observe, You select No ${it}")
//        })
        binding.endDate.apply {
            minDate = System.currentTimeMillis()
            setOnDateChangedListener { _, year, month, date ->
                viewModel.dueDate.value = "截止日期: ${year}年, ${month + 1}月, ${date}日"
            }
        }

        binding.editModule.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.addModule()
                true
            } else false
        }


        binding.buttonCreate.setOnClickListener {
            viewModel.createTask()
            dismiss()
        }
        binding.buttonClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.MessageDialog)
    }


}