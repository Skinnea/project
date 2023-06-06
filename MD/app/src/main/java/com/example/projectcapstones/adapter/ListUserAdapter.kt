package com.example.projectcapstones.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectcapstones.databinding.ItemListHistoryBinding
import com.example.projectcapstones.ui.chat.ChatActivity
import com.google.firebase.firestore.DocumentSnapshot

class ListUserAdapter(private val context: Context) :
    RecyclerView.Adapter<ListUserAdapter.ViewHolder>() {
    private val userList: MutableList<DocumentSnapshot> = mutableListOf()
    private val uidSet: HashSet<String> = hashSetOf()
    fun setUserList(users: List<DocumentSnapshot>) {
        val diffCallback = UserDiffCallback(userList, users)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        userList.clear()
        uidSet.clear()

        for (user in users) {
            val uid = user.getString("uid")
            if (uid != null && !uidSet.contains(uid)) {
                userList.add(user)
                uidSet.add(uid)
            }
        }
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemListHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val document = userList[position]
        holder.bind(
            document.getString("uid"),
            document.getString("nama"),
            document.getString("poto")
        )
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("uid", document.getString("uid"))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = userList.size
    inner class ViewHolder(private val binding: ItemListHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uid: String?, name: String?, img: String?) {
            binding.nama.text = name
            binding.isi.text = uid
            img?.let { url ->
                Glide.with(binding.root)
                    .load(url)
                    .into(binding.img)
            }
        }
    }

    private class UserDiffCallback(
        private val oldList: List<DocumentSnapshot>,
        private val newList: List<DocumentSnapshot>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldUser = oldList[oldItemPosition]
            val newUser = newList[newItemPosition]
            return oldUser.getString("uid") == newUser.getString("uid")
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldUser = oldList[oldItemPosition]
            val newUser = newList[newItemPosition]
            return oldUser == newUser
        }
    }
}