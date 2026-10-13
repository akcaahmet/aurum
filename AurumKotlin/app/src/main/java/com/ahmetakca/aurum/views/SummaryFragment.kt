package com.ahmetakca.aurum.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmetakca.aurum.R
import com.ahmetakca.aurum.adapter.SummaryListAdapter
import com.ahmetakca.aurum.databinding.FragmentSummaryBinding
import com.ahmetakca.aurum.viewmodel.SnapshotViewModel
import com.google.firebase.auth.FirebaseAuth

class SummaryFragment : Fragment() {
    private lateinit var binding: FragmentSummaryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var summaryListAdapter: SummaryListAdapter
    private val viewModel: SnapshotViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSummaryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        summaryListAdapter = SummaryListAdapter()
        binding.summaryRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = summaryListAdapter
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            setRecyclerLoading(isLoading)
        }
        observeViewModel()
        viewModel.getSummary()
    }

    private fun observeViewModel() {
        viewModel.summaries.observe(viewLifecycleOwner) { summaries ->
            summaryListAdapter.setSummaries(summaries)
            if (summaries.summaries.isEmpty()) {
                binding.emptyTextView.visibility = View.VISIBLE
                binding.summaryRecyclerView.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                binding.summaryRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setRecyclerLoading(isLoading: Boolean) {
        binding.summaryProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (isLoading) {
            binding.summaryRecyclerView.visibility = View.INVISIBLE
            binding.emptyTextView.visibility = View.GONE
        } else {
            binding.summaryRecyclerView.visibility = View.VISIBLE
        }
    }
}