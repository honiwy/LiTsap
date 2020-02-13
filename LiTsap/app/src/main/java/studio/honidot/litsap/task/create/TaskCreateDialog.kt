package studio.honidot.litsap.task.create

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import studio.honidot.litsap.LiTsapApplication.Companion.appContext
import studio.honidot.litsap.R
import studio.honidot.litsap.TaskCategory
import studio.honidot.litsap.databinding.DialogCreateTaskBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.task.TaskViewModel
import java.text.SimpleDateFormat


class TaskCreateDialog : DialogFragment() {

    private val viewModel by viewModels<TaskCreateViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DialogCreateTaskBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_create_task, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.spinnerTaskCategories.adapter = CategorySpinnerAdapter(
            appContext.resources.getStringArray(
                R.array.task_category_list
            )
        )
        val adapter = ModuleCreateAdapter(viewModel)
        binding.recyclerModule.adapter = adapter
        viewModel.moduleNameList.observe(this, Observer {
            binding.recyclerModule.adapter?.notifyDataSetChanged()
            binding.recyclerModule.smoothScrollToPosition(adapter.itemCount)
        })

        binding.endDate.apply {
            minDate = System.currentTimeMillis()
            setOnDateChangedListener { _, year, month, date ->
                val stringDate = "$date/$month/$year"
                val format = SimpleDateFormat("dd/MM/yyyy")
                viewModel.dueDate.value = format.parse(stringDate).time
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