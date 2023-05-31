package com.example.projectcapstones.repository

sealed class ResultNews<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultNews<T>()
    data class Error(val error: String) : ResultNews<Nothing>()
    object Loading : ResultNews<Nothing>()
}