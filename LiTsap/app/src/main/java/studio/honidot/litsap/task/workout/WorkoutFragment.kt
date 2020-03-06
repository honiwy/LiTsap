package studio.honidot.litsap.task.workout

import android.media.MediaPlayer
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import studio.honidot.litsap.NavigationDirections
import studio.honidot.litsap.R
import studio.honidot.litsap.databinding.FragmentWorkoutBinding
import studio.honidot.litsap.extension.getVmFactory


class WorkoutFragment : Fragment() {

    lateinit var mediaPlayer: MediaPlayer

    private val viewModel by viewModels<WorkoutViewModel> {
        getVmFactory(
            WorkoutFragmentArgs.fromBundle(
                arguments!!
            ).workoutKey
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.findUser(FirebaseAuth.getInstance().currentUser!!.uid)

        viewModel.navigateToFinish.observe(this, Observer {
            it?.let {
                findNavController().navigate(
                    NavigationDirections.actionWorkoutFragmentToFinishFragment(
                        it
                    )
                )
                viewModel.onFinishNavigated()
            }
        })

        val adapter = RecordAdapter(viewModel)
        binding.recyclerMessage.adapter = adapter
        viewModel.messageList.observe(this, Observer {
            binding.recyclerMessage.adapter?.notifyDataSetChanged()
            binding.recyclerMessage.smoothScrollToPosition(adapter.itemCount)
        })

        binding.editTalk.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.addMessage()
                true
            } else false
        }

        viewModel.leaveWorkout.observe(this, Observer {
            it?.let {
                mediaPlayer.release()
                if (it) findNavController().popBackStack()
            }
        })

        viewModel.musicPlay.observe(this, Observer {
            when (it) {
                null -> {
                    mediaPlayer.release()
                }
                true -> {
                    mediaPlayer = MediaPlayer.create(activity, R.raw.ocean)
                    mediaPlayer.seekTo(0)
                    mediaPlayer.start()
                }
                else -> {
                    mediaPlayer.pause()
                }
            }
        })

        return binding.root
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer.stop()
//        mediaPlayer.release()
//    }
}