package com.example.projectcapstones.ui.web

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.projectcapstones.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding
    private var ispageSuccess = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Skinnea News Web"
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (!ispageSuccess) {
                    view.loadUrl("javascript:alert('Web berhasil dimuat')")
                    ispageSuccess = true
                }
            }
        }
        binding.webView.webChromeClient = WebChromeClient()
        val url = intent.getStringExtra("url")
        binding.webView.loadUrl(url.toString())
    }
}