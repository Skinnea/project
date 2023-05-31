package com.example.projectcapstones.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.projectcapstones.BuildConfig
import com.example.projectcapstones.network.ApiService

class NewsRepository(
    private val apiService: ApiService
) {
    fun getHeadlineNews(query: String = ""): LiveData<ResultNews<List<NewsEntity>>> = liveData {
        emit(ResultNews.Loading)
        try {
            val response = apiService.getNews(BuildConfig.API_KEY_NEWS, q = query)
            val articles = response.articles
            val newsList = articles.map { article ->
                NewsEntity(
                    article.title,
                    article.publishedAt,
                    article.author,
                    article.url
                )
            }
            val filteredNewsList = if (query.isBlank()) {
                newsList
            } else {
                newsList.filter { it.title.contains(query, ignoreCase = true) }
            }
            emit(ResultNews.Success(filteredNewsList))
        } catch (e: Exception) {
            emit(ResultNews.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiService: ApiService
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService)
            }.also { instance = it }
    }
}