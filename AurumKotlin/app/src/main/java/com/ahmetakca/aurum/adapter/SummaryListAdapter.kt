package com.ahmetakca.aurum.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ahmetakca.aurum.R
import com.ahmetakca.aurum.databinding.SummaryRowBinding
import com.ahmetakca.aurum.models.GetSummary
import com.ahmetakca.aurum.models.Summary
import java.text.SimpleDateFormat
import java.util.Locale

class SummaryListAdapter(private var summaryList: GetSummary? = null) :
    RecyclerView.Adapter<SummaryListAdapter.SummaryHolder>() {
    private val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    fun setSummaries(summaries: GetSummary) {
        summaryList = summaries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SummaryHolder {
        val binding = SummaryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SummaryHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SummaryHolder,
        position: Int
    ) {
        val summary = summaryList?.summaries?.get(position) ?: return
        val binding = holder.binding
        binding.dateTextView.text = formatter.format(summary.createdAt)
        var selectedButton = 0
        binding.detailLayout.visibility = View.GONE
        binding.gramButton.setBackgroundResource(R.drawable.bg_button_unclicked)
        binding.quarterButton.setBackgroundResource(R.drawable.bg_button_unclicked)
        binding.halfButton.setBackgroundResource(R.drawable.bg_button_unclicked)
        binding.fullButton.setBackgroundResource(R.drawable.bg_button_unclicked)
        fun showDetails(buttonNo: Int) {
            if (selectedButton == buttonNo) {
                binding.detailLayout.visibility = View.GONE
                when (buttonNo) {
                    1 -> binding.gramButton.setBackgroundResource(R.drawable.bg_button_unclicked)
                    2 -> binding.quarterButton.setBackgroundResource(R.drawable.bg_button_unclicked)
                    3 -> binding.halfButton.setBackgroundResource(R.drawable.bg_button_unclicked)
                    4 -> binding.fullButton.setBackgroundResource(R.drawable.bg_button_unclicked)
                }
                selectedButton = 0
                return
            }
            when (selectedButton) {
                1 -> binding.gramButton.setBackgroundResource(R.drawable.bg_button_unclicked)
                2 -> binding.quarterButton.setBackgroundResource(R.drawable.bg_button_unclicked)
                3 -> binding.halfButton.setBackgroundResource(R.drawable.bg_button_unclicked)
                4 -> binding.fullButton.setBackgroundResource(R.drawable.bg_button_unclicked)
            }
            when (buttonNo) {
                1 -> {
                    binding.gramButton.setBackgroundResource(R.drawable.bg_button_clicked)
                    binding.highTextView.text = summary.gramHigh.toString()
                    binding.lowTextView.text = summary.gramLow.toString()
                    binding.closeBuyTextView.text = summary.gramCloseBuy.toString()
                    binding.closeSellTextView.text = summary.gramCloseSell.toString()
                }

                2 -> {
                    binding.quarterButton.setBackgroundResource(R.drawable.bg_button_clicked)
                    binding.highTextView.text = summary.quarterHigh.toString()
                    binding.lowTextView.text = summary.quarterLow.toString()
                    binding.closeBuyTextView.text = summary.quarterCloseBuy.toString()
                    binding.closeSellTextView.text = summary.quarterCloseSell.toString()
                }

                3 -> {
                    binding.halfButton.setBackgroundResource(R.drawable.bg_button_clicked)
                    binding.highTextView.text = summary.halfHigh.toString()
                    binding.lowTextView.text = summary.halfLow.toString()
                    binding.closeBuyTextView.text = summary.halfCloseBuy.toString()
                    binding.closeSellTextView.text = summary.halfCloseSell.toString()
                }

                4 -> {
                    binding.fullButton.setBackgroundResource(R.drawable.bg_button_clicked)
                    binding.highTextView.text = summary.fullHigh.toString()
                    binding.lowTextView.text = summary.fullLow.toString()
                    binding.closeBuyTextView.text = summary.fullCloseBuy.toString()
                    binding.closeSellTextView.text = summary.fullCloseSell.toString()
                }
            }
            binding.changeRateTextView.text = summary.changeRate.toString()
            if (summary.changeRate > 0.toBigDecimal()) {
                binding.changeRateImageView.setImageResource(R.drawable.trending_up_24px)
            } else if (summary.changeRate < 0.toBigDecimal()) {
                binding.changeRateImageView.setImageResource(R.drawable.trending_down_24px)
            } else {
                binding.changeRateImageView.setImageResource(R.drawable.trending_flat_24px)
            }
            binding.detailLayout.visibility = View.VISIBLE
            selectedButton = buttonNo
        }
        binding.gramButton.setOnClickListener {
            showDetails(1)
        }
        binding.quarterButton.setOnClickListener {
            showDetails(2)
        }
        binding.halfButton.setOnClickListener {
            showDetails(3)
        }
        binding.fullButton.setOnClickListener {
            showDetails(4)
        }
        binding.detailButton.setOnClickListener {
            val isVisible = binding.detailCardView.visibility == View.VISIBLE
            if (isVisible) {
                binding.detailButton.setImageResource(R.drawable.arrow_drop_down_24px)
                binding.detailCardView.visibility = View.GONE
            } else {
                binding.detailButton.setImageResource(R.drawable.arrow_drop_up_24px)
                binding.detailCardView.visibility = View.VISIBLE
            }
        }
//        val summary = summaryList?.summaries?.get(position) ?: return
//        holder.binding.dateTextView.text = formatter.format(summary.createdAt)
//        holder.binding.detailButton.setOnClickListener {
//            val isVisible = holder.binding.detailCardView.visibility == View.VISIBLE
//            if (isVisible) {
//                holder.binding.detailButton.setImageResource(R.drawable.arrow_drop_down_24px)
//                holder.binding.detailCardView.visibility = View.GONE
//            } else {
//                holder.binding.detailButton.setImageResource(R.drawable.arrow_drop_up_24px)
//                holder.binding.detailCardView.visibility = View.VISIBLE
//            }
//        }
//        holder.binding.gramButton.setOnClickListener {
//            val isVisible = holder.binding.detailLayout.visibility == View.VISIBLE
//            if (isVisible) {
//                holder.binding.detailLayout.visibility = View.GONE
//                holder.binding.gramButton.setBackgroundResource(R.drawable.bg_button_unclicked)
//            } else {
//                holder.binding.gramButton.setBackgroundResource(R.drawable.bg_button_clicked)
//                holder.binding.highTextView.text = summary.gramHigh.toString()
//                holder.binding.lowTextView.text = summary.gramLow.toString()
//                holder.binding.closeBuyTextView.text = summary.gramCloseBuy.toString()
//                holder.binding.closeSellTextView.text = summary.gramCloseSell.toString()
//                holder.binding.changeRateTextView.text = summary.changeRate.toString()
//                if (summary.changeRate > 0.toBigDecimal()) {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_up_24px)
//                } else if (summary.changeRate < 0.toBigDecimal()) {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_down_24px)
//                } else {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_flat_24px)
//                }
//                holder.binding.detailLayout.visibility = View.VISIBLE
//            }
//        }
//        holder.binding.quarterButton.setOnClickListener {
//            val isVisible = holder.binding.detailLayout.visibility == View.VISIBLE
//            if (isVisible) {
//                holder.binding.detailLayout.visibility = View.GONE
//            } else {
//                holder.binding.highTextView.text = summary.quarterHigh.toString()
//                holder.binding.lowTextView.text = summary.quarterLow.toString()
//                holder.binding.closeBuyTextView.text = summary.quarterCloseBuy.toString()
//                holder.binding.closeSellTextView.text = summary.quarterCloseSell.toString()
//                holder.binding.changeRateTextView.text = summary.changeRate.toString()
//                if (summary.changeRate > 0.toBigDecimal()) {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_up_24px)
//                } else if (summary.changeRate < 0.toBigDecimal()) {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_down_24px)
//                } else {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_flat_24px)
//                }
//                holder.binding.detailLayout.visibility = View.VISIBLE
//            }
//        }
//        holder.binding.halfButton.setOnClickListener {
//            val isVisible = holder.binding.detailLayout.visibility == View.VISIBLE
//            if (isVisible) {
//                holder.binding.detailLayout.visibility = View.GONE
//            } else {
//                holder.binding.highTextView.text = summary.halfHigh.toString()
//                holder.binding.lowTextView.text = summary.halfLow.toString()
//                holder.binding.closeBuyTextView.text = summary.halfCloseBuy.toString()
//                holder.binding.closeSellTextView.text = summary.halfCloseSell.toString()
//                holder.binding.changeRateTextView.text = summary.changeRate.toString()
//                if (summary.changeRate > 0.toBigDecimal()) {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_up_24px)
//                } else if (summary.changeRate < 0.toBigDecimal()) {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_down_24px)
//                } else {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_flat_24px)
//                }
//                holder.binding.detailLayout.visibility = View.VISIBLE
//            }
//        }
//        holder.binding.fullButton.setOnClickListener {
//            val isVisible = holder.binding.detailLayout.visibility == View.VISIBLE
//            if (isVisible) {
//                holder.binding.detailLayout.visibility = View.GONE
//            } else {
//                holder.binding.highTextView.text = summary.fullHigh.toString()
//                holder.binding.lowTextView.text = summary.fullLow.toString()
//                holder.binding.closeBuyTextView.text = summary.fullCloseBuy.toString()
//                holder.binding.closeSellTextView.text = summary.fullCloseSell.toString()
//                holder.binding.changeRateTextView.text = summary.changeRate.toString()
//                if (summary.changeRate > 0.toBigDecimal()) {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_up_24px)
//                } else if (summary.changeRate < 0.toBigDecimal()) {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_down_24px)
//                } else {
//                    holder.binding.changeRateImageView.setImageResource(R.drawable.trending_flat_24px)
//                }
//                holder.binding.detailLayout.visibility = View.VISIBLE
//            }
//        }
    }

    override fun getItemCount(): Int {
        return summaryList?.summaries?.size ?: 0
    }

    class SummaryHolder(val binding: SummaryRowBinding) : RecyclerView.ViewHolder(binding.root)
}