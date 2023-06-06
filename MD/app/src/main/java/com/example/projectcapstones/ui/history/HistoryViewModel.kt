package com.example.projectcapstones.ui.history
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class HistoryViewModel : ViewModel() {
    private val _historyList = MutableLiveData<List<DocumentSnapshot>>()
    val historyList: LiveData<List<DocumentSnapshot>> = _historyList
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    fun getHistory() {
        db.collection("users")
            .document(uid.toString())
            .collection("results")
            .get()
            .addOnSuccessListener { result ->
                _historyList.value = result.documents
            }
    }
}
