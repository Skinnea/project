package com.example.projectcapstones.ui.detail

import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.projectcapstones.databinding.ActivityDetailBinding
import com.example.projectcapstones.data.SkinData

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        val resultText = intent.getStringExtra("resultText")
        val getImgResult = intent.getParcelableExtra<Bitmap>("imageResult")
        binding.imgResult.setImageBitmap(getImgResult)
        if (resultText != null) {
            val result = SkinData.results.find { it.nameSkin == resultText }
            if (result != null) {
                binding.resultSkin.text = result.nameSkin
                binding.descSkin.resultDescSkin.text = result.descSkin
                binding.nameMedic.nameMedic.text = result.nameMedic
                binding.nameMedic.suggestMedic.text = result.suggestMedic
                binding.descMedic.descMedic.text = result.descMedic
                Glide.with(this)
                    .load(result.urlImgMedic)
                    .into(binding.imgMedic)
            }
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
}