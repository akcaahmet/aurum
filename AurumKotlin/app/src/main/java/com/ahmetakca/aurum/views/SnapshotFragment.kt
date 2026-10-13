package com.ahmetakca.aurum.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmetakca.aurum.NetworkClient
import com.ahmetakca.aurum.R
import com.ahmetakca.aurum.adapter.SnapshotListAdapter
import com.ahmetakca.aurum.databinding.FragmentSnapshotBinding
import com.ahmetakca.aurum.service.ApiService
import com.ahmetakca.aurum.viewmodel.SnapshotViewModel
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SnapshotFragment : Fragment() {
    private lateinit var binding: FragmentSnapshotBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var snapshotAdapter: SnapshotListAdapter
    private val viewModel: SnapshotViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSnapshotBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snapshotAdapter = SnapshotListAdapter()
        binding.snapshotRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = snapshotAdapter
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            setRecyclerLoading(isLoading)
        }
        observeViewModel()
        observeSnapshotAdded()
        viewModel.getSnapshots()
    }

    private fun observeSnapshotAdded() {
        viewModel.snapshotAdded.observe(viewLifecycleOwner) { newSnapshot ->
            snapshotAdapter.addSnapshot(newSnapshot)
            binding.emptyTextView.visibility = View.GONE
            binding.snapshotRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun observeViewModel() {
        viewModel.snapshots.observe(viewLifecycleOwner) { snapshots ->
            snapshotAdapter.setSnapshots(snapshots)
            if (snapshots.snapshots.isEmpty()) {
                binding.emptyTextView.visibility = View.VISIBLE
                binding.snapshotRecyclerView.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                binding.snapshotRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setRecyclerLoading(isLoading: Boolean) {
        binding.recyclerProgressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        if (isLoading) {
            binding.snapshotRecyclerView.visibility = View.INVISIBLE
            binding.emptyTextView.visibility = View.GONE
        } else {
            binding.snapshotRecyclerView.visibility = View.VISIBLE
        }
    }
}