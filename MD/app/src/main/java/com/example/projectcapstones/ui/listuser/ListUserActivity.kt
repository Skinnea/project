package com.example.projectcapstones.ui.listuser

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectcapstones.R
import com.example.projectcapstones.adapter.ListUserAdapter
import com.example.projectcapstones.databinding.ActivityListUserBinding

class ListUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListUserBinding
    private lateinit var adapter: ListUserAdapter
    private lateinit var viewModel: ListUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = ListUserAdapter(this)
        binding.rvListUser.adapter = adapter
        binding.rvListUser.layoutManager = LinearLayoutManager(this)
        setupView()
        viewModel = ViewModelProvider(this)[ListUserViewModel::class.java]
        viewModel.listUser.observe(this) { listUser ->
            adapter.list(listUser)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.progressText.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvListUser.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        viewModel.isError.observe(this) { isError ->
            binding.viewError.root.visibility = if (isError) View.VISIBLE else View.GONE
            binding.viewError.tvError.text = getString(R.string.error)
            binding.rvListUser.visibility = if (isError) View.GONE else View.VISIBLE
        }
        viewModel.isNotFound.observe(this) { isNotFound ->
            binding.viewError.root.visibility = if (isNotFound) View.VISIBLE else View.GONE
            binding.viewError.tvError.text = getString(R.string.notFound)
            binding.rvListUser.visibility = if (isNotFound) View.GONE else View.VISIBLE
        }
        viewModel.getListUser()
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
