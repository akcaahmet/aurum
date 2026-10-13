package com.ahmetakca.aurum.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmetakca.aurum.databinding.SnapshotRowBinding
import com.ahmetakca.aurum.models.GetSnapshotAndId
import com.ahmetakca.aurum.models.SnapshotResponse
import java.text.SimpleDateFormat
import java.util.Locale

class SnapshotListAdapter(private var snapshotList: SnapshotResponse? = null) :
    RecyclerView.Adapter<SnapshotListAdapter.SnapshotHolder>() {
    private val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    fun addSnapshot(snapshot: GetSnapshotAndId) {
        val currentList = snapshotList?.snapshots?.toMutableList() ?: mutableListOf()
        currentList.add(0, snapshot)
        snapshotList = SnapshotResponse(currentList)
        notifyItemInserted(0)
    }

    fun setSnapshots(newSnapshots: SnapshotResponse) {
        snapshotList = newSnapshots
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SnapshotHolder {
        val binding = SnapshotRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SnapshotHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SnapshotHolder,
        position: Int
    ) {
        val snapshot = snapshotList?.snapshots?.get(position) ?: return
        println(snapshot.id)
        holder.binding.dateTextView.text = formatter.format(snapshot.createdAt)
        holder.binding.gramBuyPriceTextView.text = snapshot.gramBuy.toString()
        holder.binding.gramSellPriceTextView.text = snapshot.gramSell.toString()
        holder.binding.quarterBuyPriceTextView.text = snapshot.quarterBuy.toString()
        holder.binding.quarterSellPriceTextView.text = snapshot.quarterSell.toString()
        holder.binding.halfBuyPriceTextView.text = snapshot.halfBuy.toString()
        holder.binding.halfSellPriceTextView.text = snapshot.halfSell.toString()
        holder.binding.fullBuyPriceTextView.text = snapshot.fullBuy.toString()
        holder.binding.fullSellPriceTextView.text = snapshot.fullSell.toString()
    }

    override fun getItemCount(): Int {
        return snapshotList?.snapshots?.size ?: 0
    }

    class SnapshotHolder(val binding: SnapshotRowBinding) : RecyclerView.ViewHolder(binding.root)
}