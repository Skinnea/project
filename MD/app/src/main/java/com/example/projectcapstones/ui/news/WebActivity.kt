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
    private var ispageSuccess = false
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Skinnea News Web"
        webView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (!ispageSuccess) {
                    view.loadUrl("javascript:alert('Web berhasil dimuat')")
                    ispageSuccess = true
                }
            }
        }
        webView.webChromeClient = object : WebChromeClient() {}
        savedInstanceState?.let {
            webView.restoreState(it)
            ispageSuccess = true
        } ?: run {
            val url = intent.getStringExtra("url")
            webView.loadUrl(url.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
        ispageSuccess = true
    }
}
