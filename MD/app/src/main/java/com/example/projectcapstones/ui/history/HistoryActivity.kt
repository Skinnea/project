package com.example.projectcapstones.ui.history

import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectcapstones.adapter.HistoryAdapter
import com.example.projectcapstones.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = HistoryAdapter(this)
        binding.rvListHistory.adapter = adapter
        binding.rvListHistory.layoutManager = LinearLayoutManager(this)
        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        viewModel.historyList.observe(this) { history ->
            binding.progressBar.visibility = View.VISIBLE
            adapter.historySkin(history)
            binding.progressBar.visibility = View.GONE
        }
        viewModel.getHistory()
        playAnimation()
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageViewHistory, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            startDelay = 500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}