package com.example.projectcapstones.ui.history

import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectcapstones.adapter.HistoryAdapter
import com.example.projectcapstones.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = HistoryAdapter(this)
        binding.rvListHistory.adapter = adapter
        binding.rvListHistory.layoutManager = LinearLayoutManager(this)
        getHistory()
        playAnimation()
        setupView()
    }

    private fun getHistory() {
        db.collection("users")
            .document(uid.toString())
            .collection("results")
            .get()
            .addOnSuccessListener { result ->
                binding.progressBar.visibility = View.VISIBLE
                adapter.historySkin(result.documents)
                binding.progressBar.visibility = View.GONE
            }
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageViewHistory, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            startDelay = 500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}