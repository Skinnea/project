package com.example.projectcapstones.ui.history

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectcapstones.R
import com.example.projectcapstones.adapter.HistoryAdapter
import com.example.projectcapstones.data.ResultSkin
import com.example.projectcapstones.databinding.ActivityHistoryBinding
import com.example.projectcapstones.ui.detail.DetailActivity

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        historyAdapter = HistoryAdapter { ResultSkinData ->
            val results = ResultSkin(
                imageUrl = ResultSkinData.imageUrl,
                result = ResultSkinData.result,
                accuracy = ResultSkinData.accuracy,
                deskripsi = ResultSkinData.deskripsi,
                imgObat = ResultSkinData.imgObat,
                namaObat = ResultSkinData.namaObat,
                pemakaianObat = ResultSkinData.pemakaianObat,
                detailObat = ResultSkinData.detailObat
            )
            Toast.makeText(this, "Mohon tunggu sebentar", Toast.LENGTH_LONG).show()
            val intent = Intent(this@HistoryActivity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_RESULT_SKIN_HISTORY, results)
            startActivity(intent)
        }
        binding.rvListHistory.adapter = historyAdapter
        binding.rvListHistory.layoutManager = LinearLayoutManager(this)
        viewModel.historyList.observe(this) { historyList ->
            historyAdapter.submitList(historyList)
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