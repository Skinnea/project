package com.example.projectcapstones.response

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("obj")
    val obj: String,
    @field:SerializedName("created")
    val created: Long,
    @field:SerializedName("model")
    val model: String,
    @field:SerializedName("choices")
    val choices: List<Choice>,
    @field:SerializedName("usage")
    val usage: Usage
)

data class Choice(
    @field:SerializedName("text")
    val text: String,
    @field:SerializedName("index")
    val index: Int,
    @field:SerializedName("logprobs")
    val logprobs: Any?,
    @field:SerializedName("finish_reason")
    val finish_reason: String
)

data class Usage(
    @field:SerializedName("prompt_tokens")
    val prompt_tokens: Int,
    @field:SerializedName("completion_tokens")
    val completion_tokens: Int,
    @field:SerializedName("total_tokens")
    val total_tokens: Int
)