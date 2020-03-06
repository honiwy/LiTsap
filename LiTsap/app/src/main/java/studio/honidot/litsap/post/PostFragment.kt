package studio.honidot.litsap.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.PagerSnapHelper
import studio.honidot.litsap.databinding.FragmentPostBinding
import studio.honidot.litsap.extension.getVmFactory


class PostFragment : Fragment() {

    private val viewModel by viewModels<PostViewModel> {
        getVmFactory(
            PostFragmentArgs.fromBundle(
                arguments!!
            ).userIdKey
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = HistoryAdapter(viewModel)
        binding.recyclerHistory.adapter = adapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerHistory)

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            viewModel.getHistoryOnThatDay("$dayOfMonth/${month + 1}/$year")
            binding.recyclerHistory.smoothScrollToPosition(0)
        }

        return binding.root
    }
}