package com.example.projectcapstones.ui.upload

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectcapstones.configcamera.reduceFileImage
import com.example.projectcapstones.data.ResultSkin
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
    private val _isPlayAnimation = MutableLiveData<Boolean>()
    val playAnimation: LiveData<Boolean> = _isPlayAnimation
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
                            if (uid == null) {
                                _isPlayAnimation.value = true
                            }
                            sendHistory(file)
                        } else {
                            _isLoading.value = false
                            _isPlayAnimation.value = false
                            _message.value = "Terjadi Kesalahan :("
                        }
                    }
                }
                override fun onFailure(call: Call<SkinneaResponse>, t: Throwable) {
                    _isLoading.value = false
                    _isPlayAnimation.value = false
                    _message.value = "Koneksimu bermasalah :("
                }
            })
        }
    }

    fun sendHistory(file: File) {
        _isLoading.value = true
        if (uid != null) {
            val imageRef = FirebaseStorage.getInstance().reference.child("Skinnea/${file.name}")
            val upload = imageRef.putFile(file.toUri())
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    val uploadTask = upload.await()
                    val downloadUri = uploadTask.storage.downloadUrl.await()
                    val imageUrl = downloadUri.toString()
                    val user = ResultSkin(
                        imageUrl,
                        _upload.value?.result,
                        _upload.value?.accuracy,
                        _upload.value?.deskripsi,
                        _upload.value?.imgObat,
                        _upload.value?.namaObat,
                        _upload.value?.pemakaianObat,
                        _upload.value?.detailObat,
                        Date().time
                    )
                    db.collection("users")
                        .document(uid)
                        .collection("historyMedic")
                        .add(user)
                        .addOnSuccessListener {
                            _isLoading.value = false
                            _isPlayAnimation.value = true
                        }
                        .addOnFailureListener {
                            _isLoading.value = false
                            _message.value = "Koneksimu bermasalah :("
                            _isPlayAnimation.value = false
                        }
                } catch (e: Exception) {
                    _isLoading.value = false
                    _message.value = "Gagal menyimpan riwayat :("
                    _isPlayAnimation.value = false
                }
            }
        }
    }
}