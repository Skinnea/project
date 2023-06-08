package com.example.projectcapstones.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result
    private val _imgObat = MutableLiveData<String>()
    val imgObat: LiveData<String> = _imgObat
    private val _namaObat = MutableLiveData<String>()
    val namaObat: LiveData<String> = _namaObat
    private val _pemakaianObat = MutableLiveData<String>()
    val pemakaianObat: LiveData<String> = _pemakaianObat
    private val _detailObat = MutableLiveData<String>()
    val detailObat: LiveData<String> = _detailObat
    private val _deskripsi = MutableLiveData<String>()
    val deskripsi: LiveData<String> = _deskripsi
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError
    private val _isNotFound = MutableLiveData<Boolean>()
    val isNotFound: LiveData<Boolean> = _isNotFound

    fun getDetail(getImgUrl: String?) {
        _isLoading.value = true
        _isError.value = false
        _isNotFound.value = false
        if (getImgUrl != null) {
            db.collection("users")
                .document(uid.toString())
                .collection("historyMedic")
                .whereEqualTo("imageUrl", getImgUrl)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result ) {
                        _isLoading.value = false
                        _isError.value = false
                        if (result.isEmpty) {
                            _isNotFound.value = true
                        } else {
                            _result.value = document.getString("result")
                            _deskripsi.value = document.getString("deskripsi")
                            _namaObat.value = document.getString("namaObat")
                            _pemakaianObat.value = document.getString("pemakaianObat")
                            _imgObat.value = document.getString("imgObat")
                            _detailObat.value = document.getString("detailObat")
                        }
                    }
                }
                .addOnFailureListener {
                    _isLoading.value = false
                    _isError.value = true
                }
        }
    }
}