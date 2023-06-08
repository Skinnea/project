package com.example.projectcapstones.ui.listuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ListUserViewModel : ViewModel() {
    private val _listUser = MutableLiveData<List<DocumentSnapshot>>()
    val listUser: LiveData<List<DocumentSnapshot>> = _listUser
    private val db = FirebaseFirestore.getInstance()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError
    private val _isNotFound = MutableLiveData<Boolean>()
    val isNotFound: LiveData<Boolean> = _isNotFound

    fun getListUser() {
        _isLoading.value = true
        _isError.value = false
        _isNotFound.value = false
        db.collection("CHAT")
            .get()
            .addOnSuccessListener { chat ->
                _isLoading.value = false
                _isError.value = false
                if (chat.isEmpty) {
                    _isNotFound.value = true
                } else {
                    _listUser.value = chat.documents
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                _isError.value = true
            }
    }
}