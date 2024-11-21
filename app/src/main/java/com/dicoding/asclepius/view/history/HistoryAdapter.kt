package com.dicoding.asclepius.view.history

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.databinding.ItemRowHistoryBinding

class HistoryAdapter(
    private val listHistory: List<History>,
    private val onItemClick: (History) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(listHistory[position])
    }

    override fun getItemCount(): Int = listHistory.size

    inner class HistoryViewHolder(private val binding: ItemRowHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(history: History) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(Uri.parse(history.image))
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .into(imgItemPhoto)

                tvItemName.text = history.prediksi ?: "No Prediction Available"
                tvItemScore.text = "Confidence Score: ${history.score ?: "N/A"}"

                root.setOnClickListener { onItemClick(history) }
            }
        }
    }
}
