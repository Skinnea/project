package com.example.projectcapstones.ui.detail

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.projectcapstones.data.ResultSkin
import com.example.projectcapstones.databinding.ActivityDetailBinding
import com.example.projectcapstones.ui.upload.CameraActivity

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDetail()
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

    private fun getDetail() {
        binding.progressBar.visibility = View.VISIBLE
        binding.progressText.visibility = View.VISIBLE
        val intent = intent
        val results: ResultSkin?
        if (intent.hasExtra(EXTRA_RESULT_SKIN)) {
            results = intent.getParcelableExtra(EXTRA_RESULT_SKIN)
            binding.imgResult.setImageBitmap(BitmapFactory.decodeFile(CameraActivity.getFile?.path))
        } else if (intent.hasExtra(EXTRA_RESULT_SKIN_HISTORY)) {
            results = intent.getParcelableExtra(EXTRA_RESULT_SKIN_HISTORY)
            Glide.with(this)
                .load(results?.imageUrl)
                .into(binding.imgResult)
        } else {
            return
        }
        binding.detailCard.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.progressText.visibility = View.GONE
        binding.resultSkin.text = results?.result
        binding.resultAccuracy.text = results?.accuracy
        binding.descSkin.resultDescSkin.text = results?.deskripsi
        binding.nameMedic.nameMedic.text = results?.namaObat
        binding.nameMedic.suggestMedic.text = results?.pemakaianObat
        Glide.with(this)
            .load(results?.imgObat)
            .into(binding.imgMedic)
        binding.descMedic.descMedic.text = results?.detailObat
    }

    companion object {
        const val EXTRA_RESULT_SKIN = "extra_result_skin"
        const val EXTRA_RESULT_SKIN_HISTORY = "extra_result_skin_history"
    }
}