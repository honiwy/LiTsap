package studio.honidot.litsap.task.finish

import android.Manifest.permission
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_finish.*
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.R
import studio.honidot.litsap.bindImage
import studio.honidot.litsap.data.Task
import studio.honidot.litsap.databinding.FragmentFinishBinding
import studio.honidot.litsap.extension.getVmFactory
import java.io.IOException

class FinishFragment : Fragment() {

    private val viewModel by viewModels<FinishViewModel> {
        getVmFactory(
            FinishFragmentArgs.fromBundle(
                arguments!!
            ).finishKey
        )
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = FOLDER_OPEN_DEFAULT
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, INTENT_LAUNCH_GALLERY),
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

        binding.imageChoose.setOnClickListener {
            getPermissions()
        }
        binding.imageDisplay.setOnClickListener {
            getPermissions()
        }

        viewModel.count.observe(this, Observer {
            if (it == 5) {
                findNavController().navigate(
                    NavigationDirections.navigateToTaskFragment(
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )
                )
                viewModel.onTaskNavigated()
            }
        })
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                attemptToLeave()
            }
        })

        return binding.root
    }

    private fun attemptToLeave() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setTitle(getString(R.string.finish_leave_title))
        builder.setMessage(getString(R.string.finish_leave_content))

        builder.setPositiveButton(
            getString(R.string.task_delete_confirm)
        ) { dialog, which ->
            findNavController().navigate(
                NavigationDirections.navigateToTaskFragment(
                    FirebaseAuth.getInstance().currentUser!!.uid
                )
            )
            viewModel.onTaskNavigated()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                bindImage(image_display, uri.toString())
                viewModel.filePath.value = uri
            }
        }
    }


    private fun getPermissions() {
        val permissions = arrayOf(permission.READ_EXTERNAL_STORAGE)
        when (ContextCompat.checkSelfPermission(
            LiTsapApplication.instance,
            permission.READ_EXTERNAL_STORAGE
        )) {
            PackageManager.PERMISSION_GRANTED -> {
//                        isUploadPermissionsGranted = true
                launchGallery()
            }
            else -> {
                requestPermissions(
                    permissions,
                    PICK_IMAGE_REQUEST
                )

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        isUploadPermissionsGranted = false
        when (requestCode) {
            PICK_IMAGE_REQUEST ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
//                    isUploadPermissionsGranted = true
                    try {
                        launchGallery()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
//                    isUploadPermissionsGranted = false
                    return
                }
        }
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 71
        const val INTENT_LAUNCH_GALLERY = "Select Picture"
        const val FOLDER_OPEN_DEFAULT = "image/*"
    }

}