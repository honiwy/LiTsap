package studio.honidot.litsap.task.finish

import android.Manifest.permission
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.R
import studio.honidot.litsap.bindImage
import studio.honidot.litsap.databinding.FragmentFinishBinding
import studio.honidot.litsap.extension.getVmFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FinishFragment : Fragment() {

    private val viewModel by viewModels<FinishViewModel> {
        getVmFactory(
            FinishFragmentArgs.fromBundle(
                arguments!!
            ).finishKey
        )
    }

    private fun dispatchTakePictureIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        getString(R.string.finish_provider),
                        it
                    )
                    this.photoURI = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, OPEN_CAMERA_REQUEST)
                }
            }
        }
    }

    private var photoURI: Uri? = null

    private lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            EXTENSION_SUFFIX,
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    lateinit var binding: FragmentFinishBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinishBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.recyclerFootprint.adapter = FootprintAdapter()
        binding.imageFinishChoose.setOnClickListener {
            loadCamera()
        }
        binding.imageFinishDisplay.setOnClickListener {
            loadCamera()
        }

        viewModel.count.observe(this, Observer {
            if (it == viewModel.apiCount) {
                findNavController().navigate(
                    NavigationDirections.navigateToTaskFragment(
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )
                )
                viewModel.onTaskNavigated()
            }
        })
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
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
        if (requestCode == OPEN_CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }
            photoURI?.let { uri ->
                bindImage(binding.imageFinishDisplay, uri.toString())
                viewModel.filePath.value = uri
            }
        }
    }


    private fun loadCamera() {
        if (ContextCompat.checkSelfPermission(
                LiTsapApplication.instance,
                permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    permission.CAMERA
                ),
                OPEN_CAMERA_REQUEST
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            OPEN_CAMERA_REQUEST ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        dispatchTakePictureIntent()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    return
                }
        }
    }

    companion object {
        const val OPEN_CAMERA_REQUEST = 7
        const val EXTENSION_SUFFIX = ".jpg"
    }
}