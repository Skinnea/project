package com.example.projectcapstones.network

import com.example.projectcapstones.BuildConfig
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private var BASE_URL = "https://newsapi.org/v2/"
    private const val BASE_URL_CHAT = "https://api.openai.com/v1/completions"
    private val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()

    fun getApiNews(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    fun getApiChat(chat: String, callback: Callback) {
        val jsonBody = JSONObject()
        try {
            jsonBody.put("model", "text-davinci-003")
            jsonBody.put("prompt", chat)
            jsonBody.put("max_tokens", 4000)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body = jsonBody.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(BASE_URL_CHAT)
            .header("Authorization", "Bearer ${BuildConfig.API_KEY_CHAT}")
            .post(body)
            .build()
        OkHttpClient().newCall(request).enqueue(callback)
    }
}