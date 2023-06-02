package com.example.projectcapstones.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectcapstones.R
import com.example.projectcapstones.message.Message
import java.util.*
import com.example.projectcapstones.adapter.ChatAdapter
import com.example.projectcapstones.databinding.ActivityChatBinding
import com.example.projectcapstones.network.ApiConfig
import com.example.projectcapstones.result.ResultChat
import com.example.projectcapstones.ui.login.LoginActivity.Companion.NAME
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var messages: MutableList<Message>
    private lateinit var messageAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.adminProfile.adminStatus.text = getString(R.string.statusAdminProfile, NAME.substringBefore(" "))
        messages = ArrayList()
        messageAdapter = ChatAdapter(messages)
        binding.rvlistChat.adapter = messageAdapter
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.rvlistChat.layoutManager = manager
        binding.adminProfile.buttonCall.setOnClickListener {
            val phoneNumber = "089520480880"
            val dialPhoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(dialPhoneIntent)
        }
        binding.sendButton.setOnClickListener {
            val chat = binding.messageEditText.text.toString().trim()
            if (chat.isNotEmpty()) {
                addToChat(chat, Message.SENT_BY_USER)
                binding.messageEditText.setText("")
                lifecycleScope.launch {
                    botChatAi(chat)
                }
            } else {
                Toast.makeText(this, "Tulis pesan terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
        setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messages.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            binding.rvlistChat.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    override fun onResume() {
        super.onResume()
        if (messages.isEmpty()) {
            val initialMessage = "Halo, saya ingin bertanya terkait kesehatan kulit!"
            addToChat(initialMessage, Message.SENT_BY_USER)
            lifecycleScope.launch {
                botChatAi(initialMessage)
            }
        }
    }

    private fun addResponse(response: String) {
        messages.removeAt(messages.size - 1)
        addToChat(response, Message.SENT_BY_BOTCHAT)
    }

    private suspend fun botChatAi(chat: String) {
        messages.add(Message("Sedang Mengetik... ", Message.SENT_BY_BOTCHAT))
        val resultChat = ResultChat(prompt = chat)
        val response = try {
            ApiConfig.getApiChat().getChat(resultChat)
        } catch (e: SocketTimeoutException) {
            val errorMessage = getString(R.string.send_error) + " (" + e.message + ")"
            addResponse(errorMessage)
            return
        }
        if (response.isSuccessful) {
            val result = response.body()?.choices?.firstOrNull()?.text
            if (result != null) {
                addResponse(result.trim())
            } else {
                addResponse(getString(R.string.send_error) + " (" + response.body()?.choices?.toString()+ ")")
            }
        } else {
            addResponse(getString(R.string.send_error) + " (" + response.code() + ")")
        }
    }
}