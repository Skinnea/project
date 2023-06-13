package com.example.projectcapstones.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectcapstones.databinding.ItemListNewsBinding
import com.example.projectcapstones.response.ArticlesItem

class NewsAdapter(private val onItemClick: (ArticlesItem) -> Unit) :
    ListAdapter<ArticlesItem, NewsAdapter.MyViewHolder>(
        DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemListNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    class MyViewHolder(
        private val binding: ItemListNewsBinding,
        val onItemClick: (ArticlesItem) -> Unit
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(news: ArticlesItem) {
            binding.tvItemTitle.text = news.title
            val dateTime = news.publishedAt
            val date = dateTime.substring(0, 10)
            val time = dateTime.substring(11, 16)
            val formattedDate = "$date $time"
            binding.tvItemPublish.text = formattedDate
            binding.tvItemAuthor.text = news.author
            itemView.setOnClickListener {
                onItemClick(news)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ArticlesItem> =
            object : DiffUtil.ItemCallback<ArticlesItem>() {
                override fun areItemsTheSame(
                    oldUser: ArticlesItem,
                    newUser: ArticlesItem
                ): Boolean {
                    return oldUser.title == newUser.title
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldUser: ArticlesItem,
                    newUser: ArticlesItem
                ): Boolean {
                    return oldUser == newUser
                }
            }
    }
}