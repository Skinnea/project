package com.example.projectcapstones.ui.news

import androidx.lifecycle.ViewModel
import com.example.projectcapstones.repository.NewsRepository

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {
    fun getHeadlineNews(query: String = "") = newsRepository.getHeadlineNews(query)
}