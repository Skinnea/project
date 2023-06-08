package com.example.projectcapstones.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.projectcapstones.R
import com.example.projectcapstones.databinding.ActivityDetailBinding

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        val getImgUrl = intent.getStringExtra("imageResult")
        if (getImgUrl != null) {
            viewModel.getDetail(getImgUrl)
            resultScan()
            Glide.with(this)
                .load(getImgUrl)
                .into(binding.imgResult)
        }
        setupView()
    }


    private fun resultScan() {
        viewModel.result.observe(this) { result ->
            binding.resultSkin.text = result
        }
        viewModel.deskripsi.observe(this) { deskripsi ->
            binding.descSkin.resultDescSkin.text = deskripsi
        }
        viewModel.namaObat.observe(this) { namaObat ->
            binding.nameMedic.nameMedic.text = namaObat
        }
        viewModel.pemakaianObat.observe(this) { pemakaianObat ->
            binding.nameMedic.suggestMedic.text = pemakaianObat
        }
        viewModel.imgObat.observe(this) { imgObat ->
            Glide.with(this)
                .load(imgObat)
                .into(binding.imgMedic)
        }
        viewModel.detailObat.observe(this) { detailObat ->
            binding.descMedic.descMedic.text = detailObat
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.detailCard.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        viewModel.isError.observe(this) { isError ->
            binding.viewError.root.visibility = if (isError) View.VISIBLE else View.GONE
            binding.detailCard.visibility = if (isError) View.GONE else View.VISIBLE
            binding.viewError.tvError.text = getString(R.string.error)
        }
        viewModel.isNotFound.observe(this) { isNotFound ->
            binding.viewError.root.visibility = if (isNotFound) View.VISIBLE else View.GONE
            binding.detailCard.visibility = if (isNotFound) View.GONE else View.VISIBLE
            binding.viewError.tvError.text = getString(R.string.notFound)
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