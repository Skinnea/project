package com.example.projectcapstones.adapter

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectcapstones.databinding.ItemListHistoryBinding
import com.example.projectcapstones.ui.detail.DetailActivity
import com.google.firebase.firestore.DocumentSnapshot

class HistoryAdapter(private val context: Context) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private val skinData: MutableList<DocumentSnapshot> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemListHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val document = skinData[position]
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

    override fun getItemCount(): Int = skinData.size

    fun historySkin(newskinData: List<DocumentSnapshot>) {
        val sortedData = newskinData.sortedByDescending { it.getLong("timestamp") }
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = skinData.size
            override fun getNewListSize(): Int = sortedData.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                skinData[oldItemPosition].id == sortedData[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                skinData[oldItemPosition] == sortedData[newItemPosition]
        })
        skinData.clear()
        skinData.addAll(sortedData)
        diffResult.dispatchUpdatesTo(this)
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
}
