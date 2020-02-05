package studio.honidot.litsap.task.Finish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import studio.honidot.litsap.databinding.FragmentFinishBinding
import studio.honidot.litsap.extension.getVmFactory

class FinishFragment : Fragment() {
    private val viewModel by viewModels<FinishViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFinishBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }


}