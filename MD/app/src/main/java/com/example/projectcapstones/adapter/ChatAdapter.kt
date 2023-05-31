package com.example.projectcapstones.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectcapstones.databinding.ItemMessageBinding
import com.example.projectcapstones.message.Message
import com.example.projectcapstones.ui.login.LoginActivity.Companion.NAME
import com.example.projectcapstones.ui.login.LoginActivity.Companion.PHOTO

class ChatAdapter(private val chat: List<Message>) :
    RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = chat[position]
        if (message.sentBy == Message.SENT_BY_USER) {
            holder.binding.leftChat.visibility = View.GONE
            holder.binding.rightChat.visibility = View.VISIBLE
            holder.binding.tvMessageRight.text = message.message
            holder.binding.tvMessengerRight.text = NAME
            Glide.with(holder.itemView)
                .load(PHOTO)
                .into(holder.binding.ivMessengerRight)
        } else {
            holder.binding.rightChat.visibility = View.GONE
            holder.binding.leftChat.visibility = View.VISIBLE
            holder.binding.tvMessageLeft.text = message.message
        }
    }

    override fun getItemCount(): Int {
        return chat.size
    }

    inner class MyViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)
}