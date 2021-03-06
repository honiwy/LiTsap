package studio.honidot.litsap.share.post

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import studio.honidot.litsap.LiTsapApplication
import studio.honidot.litsap.R
import studio.honidot.litsap.bindImageNoCorner
import studio.honidot.litsap.data.Module
import studio.honidot.litsap.databinding.FragmentPostBinding
import studio.honidot.litsap.extension.getVmFactory
import java.io.IOException


class PostFragment : Fragment() {
    private val viewModel by viewModels<PostViewModel> {
        getVmFactory(
            PostFragmentArgs.fromBundle(
                arguments!!
            ).shareKey, PostFragmentArgs.fromBundle(
                arguments!!
            ).isSameUser
        )
    }

    private fun dispatchLaunchGalleryIntent() {
        val intent = Intent()
        intent.type = FOLDER_OPEN_DEFAULT
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, INTENT_LAUNCH_GALLERY),
            PICK_IMAGE_REQUEST
        )
    }

    private fun launchGallery() {
        if (ContextCompat.checkSelfPermission(
                LiTsapApplication.instance,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PICK_IMAGE_REQUEST
            )
        } else {
            dispatchLaunchGalleryIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PICK_IMAGE_REQUEST ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        dispatchLaunchGalleryIntent()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    return
                }
        }
    }

    lateinit var binding: FragmentPostBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // set the initial position to the center of infinite gallery
        viewModel.share.value?.let { share ->
            binding.recyclerPostGallery.adapter = PostGalleryAdapter()
            binding.recyclerPostCircles.adapter = PostCircleAdapter()
            val linearSnapHelper = LinearSnapHelper().apply {
                attachToRecyclerView(binding.recyclerPostGallery)
            }
            binding.recyclerPostGallery.setOnScrollChangeListener { _, _, _, _, _ ->
                viewModel.onGalleryScrollChange(
                    binding.recyclerPostGallery.layoutManager,
                    linearSnapHelper
                )
            }
            binding.recyclerPostGallery
                .scrollToPosition(share.imageUris.size * 100)

            binding.imagePostChoose.setOnClickListener {
                launchGallery()
            }
            binding.imagePostDisplay.setOnClickListener {
                launchGallery()
            }
            viewModel.share.observe(this, Observer {
                binding.recyclerPostGallery.adapter?.notifyDataSetChanged()
                binding.recyclerPostCircles.adapter?.notifyDataSetChanged()
            })

            viewModel.snapPosition.observe(this, Observer {
                (binding.recyclerPostCircles.adapter as PostCircleAdapter).selectedPosition.value =
                    (it % share.imageUris.size)
            })
        }

        viewModel.leavePost.observe(this, Observer {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        })

        viewModel.modules.observe(this, Observer {
            if (it.size >= 3) {
                drawRadarChart(binding.radarChart, it)
            }
        })

        return binding.root
    }

    private fun drawRadarChart(chart: RadarChart, modules: List<Module>) {
        val radarEntries = ArrayList<RadarEntry>()
        val names = ArrayList<String>()
        modules.forEach {
            radarEntries.add(RadarEntry(it.achieveSection.toFloat()))
            names.add(it.moduleName)
        }
        val radarDataSet = RadarDataSet(radarEntries, "")
        radarDataSet.colors = listOf(LiTsapApplication.instance.getColor(R.color.honey))
        radarDataSet.fillColor = LiTsapApplication.instance.getColor(R.color.honey)
        radarDataSet.setDrawFilled(true)

        chart.apply {
            data = RadarData(radarDataSet)
            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            setExtraOffsets(35f, 5f, 35f, 5f)
            xAxis.valueFormatter = IndexAxisValueFormatter(names)
            animateY(CHART_ANIMATION_TIME)
            invalidate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                bindImageNoCorner(binding.imagePostDisplay, uri.toString())
                viewModel.filePath.value = uri
            }
        }
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 71
        const val INTENT_LAUNCH_GALLERY = "Select Picture"
        const val FOLDER_OPEN_DEFAULT = "image/*"
        private const val CHART_ANIMATION_TIME = 1000
    }
}