package com.example.projectcapstones.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectcapstones.data.ResultSkin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val _historyList = MutableLiveData<List<ResultSkin>>()
    val historyList: LiveData<List<ResultSkin>> = _historyList
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError
    private val _isNotFound = MutableLiveData<Boolean>()
    val isNotFound: LiveData<Boolean> = _isNotFound

    fun getHistory() {
        _isLoading.value = true
        _isError.value = false
        _isNotFound.value = false
        viewModelScope.launch {
            try {
                db.collection("users")
                    .document(uid.toString())
                    .collection("historyMedic")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { history  ->
                        val resultSkinList = history.documents.map { documentSnapshot ->
                            documentSnapshot.toObject(ResultSkin::class.java) ?: ResultSkin()
                        }
                        _isLoading.value = false
                        _isError.value = false
                        if (resultSkinList.isEmpty()) {
                            _isNotFound.value = true
                        } else {
                            _historyList.value = resultSkinList
                        }
                    }
                    .addOnFailureListener {
                        _isLoading.value = false
                        _isError.value = true
                    }
            } catch (e: Exception) {
                _isLoading.value = false
                _isError.value = true
            }
        }
    }
}