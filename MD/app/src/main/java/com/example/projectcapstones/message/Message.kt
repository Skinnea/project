package com.example.projectcapstones.message


class Message(var message: String, var sentBy: String) {
    companion object {
        const val SENT_BY_USER = "user"
        const val SENT_BY_BOTCHAT = "bot_chat"
    }
}