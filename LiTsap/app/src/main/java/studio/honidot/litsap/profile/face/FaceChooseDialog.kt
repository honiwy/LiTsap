package studio.honidot.litsap.profile.face

import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
import studio.honidot.litsap.databinding.DialogChooseFaceBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.util.Logger


class FaceChooseDialog : DialogFragment() {

    private val viewModel by viewModels<FaceChooseViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DialogChooseFaceBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_choose_face, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.recyclerFacelist.adapter = FaceAdapter(viewModel, FaceAdapter.OnClickListener {
            //            viewModel.getMurmur(it.groupId)
        })

val space = LiTsapApplication.instance.resources.getDimensionPixelSize(R.dimen.face_size)
        binding.recyclerFacelist.layoutManager= GridLayoutManager(context,Resources.getSystem().displayMetrics.widthPixels/ space)

        binding.buttonConfirm.setOnClickListener {
            //viewModel.createTask()
            dismiss()
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.MessageDialog)
    }


}