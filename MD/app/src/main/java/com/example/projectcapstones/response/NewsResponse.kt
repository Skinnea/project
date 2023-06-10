package com.example.projectcapstones.response

import com.google.gson.annotations.SerializedName

data class NewsResponse(
	@field:SerializedName("totalResults")
	val totalResults: Int,
	@field:SerializedName("articles")
	val articles: List<ArticlesItem>,
	@field:SerializedName("status")
	val status: String
)

data class ArticlesItem(
	@field:SerializedName("title")
	val title: String,
	@field:SerializedName("publishedAt")
	val publishedAt: String,
	@field:SerializedName("author")
	val author: String,
	@field:SerializedName("url")
	val url: String,
)