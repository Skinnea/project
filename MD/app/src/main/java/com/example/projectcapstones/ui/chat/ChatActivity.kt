package com.example.projectcapstones.ui.chat

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectcapstones.R
import com.example.projectcapstones.adapter.ChatAdapter
import com.example.projectcapstones.databinding.ActivityChatBinding
import com.example.projectcapstones.message.Message
import com.example.projectcapstones.ui.login.LoginActivity
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: ChatAdapter
    private lateinit var userMessagesRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        if (firebaseUser.uid == "VZQb2hCvPpbLgCAt3c8kMfYXrGN2") {
            binding.adminProfile.root.visibility = View.GONE
            val layoutParams = binding.rvlistChat.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.BELOW, R.id.parent)
            binding.rvlistChat.layoutParams = layoutParams
        } else {
            binding.adminProfile.root.visibility = View.VISIBLE
            binding.adminProfile.adminStatus.text = getString(
                R.string.statusAdminProfile,
                firebaseUser.displayName?.substringBefore(" ") ?: ""
            )
            binding.adminProfile.buttonCall.setOnClickListener {
                val phoneNumber = "089520480880"
                val dialPhoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                startActivity(dialPhoneIntent)
            }
        }
        db = Firebase.database
        val getUid = intent.getStringExtra("uid")
        val messagesRef = db.reference.child(MESSAGES_CHILD)
        val uid = firebaseUser.uid
        userMessagesRef = if (getUid != null) {
            messagesRef.child(getUid)
        } else {
            messagesRef.child(uid)
        }
        setupView()
        binding.sendButton.setOnClickListener {
            val friendlyMessage = Message(
                binding.messageEditText.text.toString(),
                firebaseUser.displayName.toString(),
                firebaseUser.photoUrl.toString(),
                Date().time
            )
            userMessagesRef.push().setValue(friendlyMessage) { error, _ ->
                if (error != null) {
                    Toast.makeText(this, getString(R.string.send_error) + error.message, Toast.LENGTH_SHORT).show()
                } else {
                    val user = hashMapOf(
                        "uid" to firebaseUser.uid,
                        "pesan" to binding.messageEditText.text.toString(),
                        "nama" to firebaseUser.displayName.toString(),
                        "poto" to firebaseUser.photoUrl.toString(),
                        "waktu" to Date().time
                    )
                    Firebase.firestore.collection("CHAT")
                        .add(user)
                    Toast.makeText(this, getString(R.string.send_success), Toast.LENGTH_SHORT).show()
                }
            }
            binding.messageEditText.setText("")
        }
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.rvlistChat.layoutManager = manager
        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(userMessagesRef, Message::class.java)
            .build()
        adapter = ChatAdapter(options, firebaseUser.displayName)
        binding.rvlistChat.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
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

    companion object {
        const val MESSAGES_CHILD = "messages"
    }
}