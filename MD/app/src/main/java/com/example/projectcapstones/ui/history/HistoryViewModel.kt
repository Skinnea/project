package com.example.projectcapstones.ui.history
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class HistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    private val _historyList = MutableLiveData<List<DocumentSnapshot>>()
    val historyList: LiveData<List<DocumentSnapshot>> = _historyList
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

        db.collection("users")
            .document(uid.toString())
            .collection("historyMedic")
            .get()
            .addOnSuccessListener { history ->
                _isLoading.value = false
                _isError.value = false
                if (history.isEmpty) {
                    _isNotFound.value = true
                } else {
                    _historyList.value = history.documents
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                _isError.value = true
            }
    }
}
