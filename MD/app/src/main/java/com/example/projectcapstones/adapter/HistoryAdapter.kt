package com.example.projectcapstones.adapter

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectcapstones.databinding.ItemListHistoryBinding
import com.example.projectcapstones.ui.detail.DetailActivity
import com.google.firebase.firestore.DocumentSnapshot

class HistoryAdapter(private val context: Context) :
    ListAdapter<DocumentSnapshot, HistoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val document = getItem(position)
        holder.bind(
            document.getString("result"),
            document.getString("accuracy"),
            document.getString("deskripsi"),
            document.getString("imageUrl"),
            document.getLong("timestamp"),
        )
        val imageUrl = document.getString("imageUrl")
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("imageResult", imageUrl)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(private val binding: ItemListHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            name: String?,
            accuracy: String?,
            description: String?,
            imageUrl: String?,
            timestamp: Long? = null
        ) {
            binding.nama.text = name
            binding.accuracy.text = accuracy
            binding.isi.text = description
            if (timestamp != null) {
                binding.timestamp.text = DateUtils.getRelativeTimeSpanString(timestamp)
            }
            imageUrl?.let { url ->
                Glide.with(binding.root)
                    .load(url)
                    .into(binding.img)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<DocumentSnapshot>() {
        override fun areItemsTheSame(
            oldItem: DocumentSnapshot,
            newItem: DocumentSnapshot
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DocumentSnapshot,
            newItem: DocumentSnapshot
        ): Boolean {
            return oldItem == newItem
        }
    }
}