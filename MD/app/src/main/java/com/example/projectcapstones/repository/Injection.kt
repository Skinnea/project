package com.example.projectcapstones.repository

import com.example.projectcapstones.network.ApiConfig

object Injection {
    fun provideRepository(): NewsRepository {
        val apiService = ApiConfig.getApiNews()
        return NewsRepository.getInstance(apiService)
    }
}