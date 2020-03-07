package studio.honidot.litsap.task.create

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import studio.honidot.litsap.LiTsapApplication.Companion.appContext
import studio.honidot.litsap.R
import studio.honidot.litsap.databinding.DialogCreateTaskBinding
import studio.honidot.litsap.extension.getVmFactory
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

        viewModel.findUser(FirebaseAuth.getInstance().currentUser!!.uid)

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
                val format = SimpleDateFormat(getString(R.string.diary_record_date))
                viewModel.dueDate.value = format.parse("$date/${month + 1}/$year").time
            }
        }

        binding.editModule.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && viewModel.newModule.value != "") {
                viewModel.addModule()
                true
            } else false
        }

        binding.buttonCreate.setOnClickListener {
            viewModel.create()
        }
        binding.buttonClose.setOnClickListener {
            dismiss()
        }

        viewModel.count.observe(this, Observer {
            if (it == 6) {
                viewModel.onTaskCreated()
                dismiss()
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.MessageDialog)
    }


}