package com.example.projectcapstones.network

import com.example.projectcapstones.BuildConfig.API_KEY_CHAT
import com.example.projectcapstones.BuildConfig.API_KEY_NEWS
import com.example.projectcapstones.response.ChatResponse
import com.example.projectcapstones.response.NewsResponse
import com.example.projectcapstones.data.ResultChat
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @Headers("Authorization: Bearer $API_KEY_NEWS")
    @GET("top-headlines?country=id&category=health")
    suspend fun getNews(
        @Query("q") q: String
    ): NewsResponse

    @Headers("Authorization: Bearer $API_KEY_CHAT")
    @POST("completions")
    suspend fun getChat(
        @Body completionResponse: ResultChat
    ): Response<ChatResponse>
}