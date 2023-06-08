package com.example.projectcapstones.adapter

import android.content.Context
import android.content.Intent
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
    internal val skinData: MutableList<DocumentSnapshot> = mutableListOf()
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
            document.getString("deskripsi"),
            document.getString("imageUrl")
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
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = skinData.size
            override fun getNewListSize(): Int = newskinData.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldItemPosition == newItemPosition

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                skinData[oldItemPosition] == newskinData[newItemPosition]
        })
        skinData.clear()
        skinData.addAll(newskinData)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(private val binding: ItemListHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String?, description: String?, imageUrl: String?) {
            binding.nama.text = name
            binding.isi.text = description
            imageUrl?.let { url ->
                Glide.with(binding.root)
                    .load(url)
                    .into(binding.img)
            }
        }
    }
}