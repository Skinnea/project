package com.example.projectcapstones.ui.news

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectcapstones.R
import com.example.projectcapstones.ViewModelFactory
import com.example.projectcapstones.adapter.NewsAdapter
import com.example.projectcapstones.data.ResultNews
import com.example.projectcapstones.databinding.ActivityNewsBinding
import com.example.projectcapstones.ui.web.WebActivity

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private val factory: ViewModelFactory = ViewModelFactory.getInstance()
    private val viewModel: NewsViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        newsAdapter = NewsAdapter { news ->
            val intent = Intent(this@NewsActivity, WebActivity::class.java)
            intent.putExtra("url", news.url)
            Toast.makeText(this, "Mohon tunggu sebentar", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
        setupView()
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = newsAdapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.bannerSearch.searchBar.setText("")
        }
        binding.bannerSearch.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { getNews() }
            override fun afterTextChanged(s: Editable?) {}
        })
        getNews()
    }

    private fun getNews() {
        viewModel.getHeadlineNews(binding.bannerSearch.searchBar.text.toString())
            .observe(this@NewsActivity) { result ->
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