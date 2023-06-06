package com.example.projectcapstones.ui.home

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainViewModel : ViewModel() {
    private val _profileData = MutableLiveData<FirebaseUser?>()
    val profileData: MutableLiveData<FirebaseUser?> = _profileData
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getProfile() {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            currentUser.displayName
            currentUser.photoUrl?.toString()
            currentUser.email
            _profileData.value = currentUser
        }
    }
}