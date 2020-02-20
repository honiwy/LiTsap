package studio.honidot.litsap.task.finish

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_finish.*
import studio.honidot.litsap.MainViewModel
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.R
import studio.honidot.litsap.databinding.FragmentFinishBinding
import studio.honidot.litsap.extension.getVmFactory
import studio.honidot.litsap.task.workout.RecordAdapter
import studio.honidot.litsap.task.workout.WorkoutFragmentArgs
import studio.honidot.litsap.task.workout.WorkoutViewModel
import studio.honidot.litsap.util.Logger
import java.io.IOException

class FinishFragment : Fragment() {
    private val viewModel by viewModels<FinishViewModel> {
        getVmFactory(
            FinishFragmentArgs.fromBundle(
                arguments!!
            ).finishKey
        )
    }

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFinishBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerFootprint.adapter = FootprintAdapter()
        viewModel.workout.observe(this, Observer {
            Logger.w("Hello viewModel.workout.observe: ${it}")
        })


        binding.imageChoose.setOnClickListener {
            launchGallery()
        }
        binding.imageDisplay.setOnClickListener {
            launchGallery()
        }


        viewModel.navigateToProfile.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToProfileFragment(
                    FirebaseAuth.getInstance().currentUser!!.uid))
                viewModel.onProfileNavigated()
            }
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST  && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, filePath)
//                uploadImage.setImageBitmap(bitmap)
                Glide.with(this).load(filePath)
                    .apply(
                        RequestOptions().transform(CenterCrop(), RoundedCorners(15))
                            .placeholder(R.drawable.gallery)
                            .error(R.drawable.gallery)
                    )
                    .into(image_display)
                viewModel.imageUri.value = filePath

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}