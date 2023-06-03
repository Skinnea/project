package com.example.projectcapstones.ui.home

import androidx.lifecycle.*
import com.example.projectcapstones.ui.login.LoginActivity

class MainViewModel : ViewModel() {
    private val _profileData = MutableLiveData<ProfileData>()
    private val profileData: LiveData<ProfileData> = _profileData

    fun loadProfileData() {
        val profile = ProfileData(
            LoginActivity.NAME,
            LoginActivity.EMAIL,
            LoginActivity.PHOTO
        )
        _profileData.postValue(profile)
    }

    fun observeProfileData(owner: LifecycleOwner, observer: Observer<ProfileData>) {
        profileData.observe(owner, observer)
    }


    data class ProfileData(
        val name: String,
        val email: String,
        val photoUrl: String
    )
}