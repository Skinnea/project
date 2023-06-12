package com.example.projectcapstones.ui.history

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectcapstones.R
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
            adapter.historySkin(history)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.progressText.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvListHistory.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        viewModel.isError.observe(this) { isError ->
            binding.viewError.root.visibility = if (isError) View.VISIBLE else View.GONE
            binding.viewError.tvError.text = getString(R.string.error)
            binding.rvListHistory.visibility = if (isError) View.GONE else View.VISIBLE
        }
        viewModel.isNotFound.observe(this) { isNotFound ->
            binding.viewError.root.visibility = if (isNotFound) View.VISIBLE else View.GONE
            binding.viewError.tvError.text = getString(R.string.notFound)
            binding.rvListHistory.visibility = if (isNotFound) View.GONE else View.VISIBLE
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
            duration = 1000
            startDelay = 500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}