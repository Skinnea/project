package com.example.projectcapstones.ui.news

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.projectcapstones.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding
    private var pageSuccess = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Skinnea News Web"
        val webView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (!pageSuccess) {
                    view.loadUrl("javascript:alert('Web berhasil dimuat')")
                    pageSuccess = true
                }
            }
        }
        webView.webChromeClient = object : WebChromeClient() {}
        val url = intent.getStringExtra("url")
        webView.loadUrl(url.toString())
    }
}