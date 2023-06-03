package com.example.projectcapstones.data

data class ResultChat(
    val model: String = "text-davinci-003",
    val prompt: String,
    val max_tokens: Int =4000,
    val temperature: Int = 0,
)