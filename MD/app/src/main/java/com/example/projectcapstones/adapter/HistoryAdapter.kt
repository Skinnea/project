package com.example.projectcapstones.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectcapstones.data.ResultSkin
import com.example.projectcapstones.databinding.ItemListHistoryBinding

class HistoryAdapter(private val onItemClick: (ResultSkin) -> Unit) :
    ListAdapter<ResultSkin, HistoryAdapter.MyViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val resultSkinData = getItem(position)
        holder.bind(resultSkinData)
    }

    class MyViewHolder(
        private val binding: ItemListHistoryBinding,
        val onItemClick: (ResultSkin) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultSkinData: ResultSkin) {
            binding.nama.text = resultSkinData.result
            binding.accuracy.text = resultSkinData.accuracy
            binding.isi.text = resultSkinData.deskripsi
            resultSkinData.timestamp?.let { timestamp ->
                binding.timestamp.text = DateUtils.getRelativeTimeSpanString(timestamp)
            }
            resultSkinData.imageUrl?.let { url ->
                Glide.with(binding.root)
                    .load(url)
                    .into(binding.img)
            }
            itemView.setOnClickListener {
                onItemClick(resultSkinData)
            }
        }
    }

    override fun submitList(list: List<ResultSkin>?) {
        super.submitList(list)
    }

    private class DiffCallback : DiffUtil.ItemCallback<ResultSkin>() {
        override fun areItemsTheSame(
            oldItem: ResultSkin,
            newItem: ResultSkin
        ): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
        }

        override fun areContentsTheSame(
            oldItem: ResultSkin,
            newItem: ResultSkin
        ): Boolean {
            return oldItem == newItem
        }
    }
}