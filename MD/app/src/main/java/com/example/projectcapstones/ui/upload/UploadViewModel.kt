package com.example.projectcapstones.ui.upload

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectcapstones.configcamera.reduceFileImage
import com.example.projectcapstones.network.ApiConfig
import com.example.projectcapstones.response.SkinneaResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class UploadViewModel : ViewModel() {
    private val _upload = MutableLiveData<SkinneaResponse>()
    val upload: LiveData<SkinneaResponse> = _upload
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    fun uploadImage() {
        if (CameraActivity.getFile != null) {
            val file = reduceFileImage(CameraActivity.getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "input",
                file.name,
                requestImageFile
            )
            val apiService = ApiConfig.getApiSkinnea()
            val uploadImageRequest = apiService.uploadImage(imageMultipart)
            _isLoading.value = true
            uploadImageRequest.enqueue(object : Callback<SkinneaResponse> {
                override fun onResponse(
                    call: Call<SkinneaResponse>,
                    response: Response<SkinneaResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _upload.value = response.body()
                            _isLoading.value = false
                            sendHistory(file)
                        } else {
                            _isLoading.value = false
                            _message.value = "Terjadi Kesalahan :("
                        }
                    }
                }

                override fun onFailure(call: Call<SkinneaResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = "Koneksimu bermasalah :("
                }
            })
        }
    }

    fun sendHistory(file: File) {
        if (uid != null) {
            val timestamp: Long = Date().time
            val imageRef = FirebaseStorage.getInstance().reference.child("Skinnea/${file.name}")
            val upload = imageRef.putFile(file.toUri())
            viewModelScope.launch {
                try {
                    val uploadTask = upload.await()
                    val downloadUri = uploadTask.storage.downloadUrl.await()
                    val imageUrl = downloadUri.toString()
                    val user = hashMapOf(
                        "imageUrl" to imageUrl,
                        "result" to _upload.value?.result,
                        "accuracy" to _upload.value?.accuracy,
                        "deskripsi" to _upload.value?.deskripsi,
                        "imgObat" to _upload.value?.imgObat,
                        "namaObat" to _upload.value?.namaObat,
                        "pemakaianObat" to _upload.value?.pemakaianObat,
                        "detailObat" to _upload.value?.detailObat,
                        "timestamp" to timestamp
                    )
                    _isLoading.value = false
                    db.collection("users")
                        .document(uid)
                        .collection("historyMedic")
                        .add(user)
                        .addOnSuccessListener {
                            _isLoading.value = false
                        }
                        .addOnFailureListener {
                            _isLoading.value = false
                            _message.value = "Koneksimu bermasalah :("
                        }
                } catch (e: Exception) {
                    _isLoading.value = false
                    _message.value = "Gagal menyimpan riwayat :("
                }
            }
        }
    }
}
