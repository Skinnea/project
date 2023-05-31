package com.example.projectcapstones.ui.news

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.projectcapstones.ViewModelFactory
import com.example.projectcapstones.adapter.NewsAdapter
import com.example.projectcapstones.databinding.ActivityNewsBinding
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectcapstones.R
import com.example.projectcapstones.repository.ResultNews

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getNews()
        setupView()
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.bannerSearch.searchBar.setText("")
            getNews()
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

    private fun getNews() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance()
        val viewModel: NewsViewModel by viewModels {
            factory
        }
        val newsAdapter = NewsAdapter { news ->
            val intent = Intent(this@NewsActivity, WebActivity::class.java)
            intent.putExtra("url", news.url)
            Toast.makeText(this, "Mohon tunggu sebentar", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }

        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = newsAdapter
        viewModel.getHeadlineNews().observe(this) { result ->
            when (result) {
                is ResultNews.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ResultNews.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val newsData = result.data
                    newsAdapter.submitList(newsData)
                }
                is ResultNews.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.viewError.root.visibility = View.VISIBLE
                    binding.viewError.tvError.text = getString(R.string.error)
                }
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
        binding.bannerSearch.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.getHeadlineNews(s.toString()).observe(this@NewsActivity) { result ->
                    when (result) {
                        is ResultNews.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is ResultNews.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvList.visibility = View.VISIBLE
                            val newsData = result.data
                            newsAdapter.submitList(newsData)
                            if (newsData.isEmpty()) {
                                binding.viewError.root.visibility = View.VISIBLE
                                binding.rvList.visibility = View.GONE
                                binding.viewError.tvError.text = getString(R.string.notFound)
                            } else {
                                binding.viewError.root.visibility = View.GONE
                            }
                        }
                        is ResultNews.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvList.visibility = View.GONE
                            binding.viewError.root.visibility = View.VISIBLE
                            binding.viewError.tvError.text = getString(R.string.error)
                        }
                    }
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}