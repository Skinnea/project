package com.example.projectcapstones.ui.listuser

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
        binding.rvListHistory.adapter = adapter
        binding.rvListHistory.layoutManager = LinearLayoutManager(this)
        setupView()
        viewModel = ViewModelProvider(this)[ListUserViewModel::class.java]
        viewModel.listUser.observe(this) { userList ->
            binding.progressBar.visibility = View.VISIBLE
            adapter.setUserList(userList)
            binding.progressBar.visibility = View.GONE
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
