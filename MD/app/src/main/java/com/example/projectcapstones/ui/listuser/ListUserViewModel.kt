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

    fun getListUser() {
        db.collection("CHAT")
            .get()
            .addOnSuccessListener { result ->
                _listUser.value = result.documents
            }
    }
}