package studio.honidot.litsap.task.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import studio.honidot.litsap.LiTsapApplication.Companion.appContext
import studio.honidot.litsap.R
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

        binding.spinnerTaskCategories.adapter = CategorySpinnerAdapter( appContext.resources.getStringArray(
            R.array.task_category_list) )

        binding.numberPicker.apply {
            minValue = 2
            maxValue = 20
        }

       // np.setOnValueChangedListener(onValueChangeListener)

        binding.viewModel = viewModel

        binding.buttonCreate.setOnClickListener {
            viewModel.createTask()
            dismiss()
        }
//        binding.buttonClose.setOnClickListener {
//            dismiss()
//        }

        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.MessageDialog)
    }
}