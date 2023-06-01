package com.example.projectcapstones.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines?country=id&category=health")
    suspend fun getNews(
        @Query("apiKey") apiKey: String,
        @Query("q") q: String
    ): NewsResponse
}
