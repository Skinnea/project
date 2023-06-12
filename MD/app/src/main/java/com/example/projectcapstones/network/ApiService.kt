package com.example.projectcapstones.network

import com.example.projectcapstones.BuildConfig.API_KEY_NEWS
import com.example.projectcapstones.response.NewsResponse
import com.example.projectcapstones.response.SkinneaResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Headers("Authorization: Bearer $API_KEY_NEWS")
    @GET("top-headlines?country=id&category=health")
    suspend fun getNews(
        @Query("q") q: String
    ): NewsResponse

    @Multipart
    @POST("predict")
    fun uploadImage(
        @Part file: MultipartBody.Part,
    ): Call<SkinneaResponse>
}