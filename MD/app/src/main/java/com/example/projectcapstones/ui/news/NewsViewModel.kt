package com.example.projectcapstones.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.projectcapstones.response.ArticlesItem
import com.example.projectcapstones.repository.NewsRepository
import com.example.projectcapstones.result.ResultNews

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {
    fun getHeadlineNews(query: String = ""): LiveData<ResultNews<List<ArticlesItem>>> {
        val headlineNewsData = MediatorLiveData<ResultNews<List<ArticlesItem>>>()
        headlineNewsData.addSource(newsRepository.getHeadlineNews(query)) { result ->
            headlineNewsData.value = result
        }
        return headlineNewsData
    }
}