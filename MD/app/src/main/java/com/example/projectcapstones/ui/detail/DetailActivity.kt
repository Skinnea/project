package com.example.projectcapstones.ui.detail

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.projectcapstones.MainActivity
import com.example.projectcapstones.databinding.ActivityDetailBinding
import com.example.projectcapstones.repository.ResultSkinData

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        val resultText = intent.getStringExtra("resultText")
        val bitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
        binding.imgResult.setImageBitmap(bitmap)
        if (resultText != null) {
            val result = ResultSkinData.results.find { it.nameSkin == resultText }
            if (result != null) {
                binding.resultSkin.text = result.nameSkin
                binding.resultDescSkin.text = result.descSkin
                binding.nameMedic.nameMedic.text = result.nameMedic
                binding.nameMedic.suggestMedic.text = result.suggestMedic
                binding.descMedic.descMedic.text = result.descMedic
                Glide.with(this)
                    .load(result.urlImgMedic)
                    .into(binding.imgMedic)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
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