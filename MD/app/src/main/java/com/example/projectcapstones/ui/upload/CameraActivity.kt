package com.example.projectcapstones.ui.upload

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.projectcapstones.configcamera.createFile
import com.example.projectcapstones.configcamera.reduceFileImage
import com.example.projectcapstones.configcamera.rotateFile
import com.example.projectcapstones.configcamera.uriToFile
import com.example.projectcapstones.databinding.ActivityCameraBinding
import com.example.projectcapstones.network.ApiConfig
import com.example.projectcapstones.response.SkinneaResponse
import com.example.projectcapstones.ui.detail.DetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.GONE
        setupButton()
        setupView()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri?.let { uri ->
                        val file = File(uri.path.toString())
                        val rotateImage = rotateFile(BitmapFactory.decodeFile(file.path), cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                        getFile = file
                        binding.previewImage.previewImageView.setImageBitmap(rotateImage)
                        uploadImage()
                        file
                    }
                }
            }
        )
    }

    private fun setupButton() {
        binding.previewImage.againButton.setOnClickListener {
            playAnimationRestart()
        }
        binding.galleryImage.setOnClickListener { startGallery() }
        binding.captureImage.setOnClickListener { takePhoto() }
        binding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
        binding.previewImage.uploadButton.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("imageResult", imageUrl)
            startActivity(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Silahkan Pilih Gambar")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@CameraActivity)
                getFile = myFile
                binding.previewImage.previewImageView.setImageURI(uri)
                uploadImage()
            }
        }
    }

    private fun playAnimation() {
        val previewImg =
            ObjectAnimator.ofFloat(binding.previewImage.root, View.ALPHA, 1f).setDuration(50)
        AnimatorSet().apply {
            playSequentially(previewImg)
            startDelay = 50
        }
        .start()
        binding.galleryImage.isClickable = false
        binding.captureImage.isClickable = false
        binding.switchCamera.isClickable = false
    }

    private fun playAnimationRestart() {
        val previewImg =
            ObjectAnimator.ofFloat(binding.previewImage.root, View.ALPHA, 0f).setDuration(50)
        AnimatorSet().apply {
            playSequentially(previewImg)
            startDelay = 50
        }.start()
        binding.galleryImage.isClickable = true
        binding.captureImage.isClickable = true
        binding.switchCamera.isClickable = true
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "input",
                file.name,
                requestImageFile
            )
            val apiService = ApiConfig.getApiSkinnea()
            val uploadImageRequest = apiService.uploadImage(imageMultipart)
            binding.progressBar.visibility = View.VISIBLE
            uploadImageRequest.enqueue(object : Callback<SkinneaResponse> {
                override fun onResponse(
                    call: Call<SkinneaResponse>,
                    response: Response<SkinneaResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            binding.progressBar.visibility = View.GONE
                            result = responseBody.result
                            accuracy = responseBody.accuracy
                            deskripsi = responseBody.deskripsi
                            imgObat = responseBody.imgObat
                            namaObat = responseBody.namaObat
                            pemakaianObat = responseBody.pemakaianObat
                            detailObat = responseBody.detailObat
                            binding.previewImage.result.text = result
                            binding.previewImage.accurate.text = accuracy
                            sendHistory(file)
                        } else {
                            binding.progressBar.visibility = View.GONE
                            alertErrorFailed()
                        }
                    }
                }

                override fun onFailure(call: Call<SkinneaResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    alertErrorConnect()
                }
            })
        }
    }

    private fun sendHistory(file: File) {
        binding.progressBar.visibility = View.VISIBLE
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("Skinnea/${file.name}")
        val upload= imageRef.putFile(file.toUri())
        val firestore = FirebaseFirestore.getInstance()
        upload.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                imageUrl = downloadUri.toString()
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                val userId = currentUser?.uid
                if (userId != null) {
                    val user = hashMapOf(
                        "imageUrl" to imageUrl,
                        "result" to result,
                        "accuracy" to accuracy,
                        "deskripsi" to deskripsi,
                        "imgObat" to imgObat,
                        "namaObat" to namaObat,
                        "pemakaianObat" to pemakaianObat,
                        "detailObat" to detailObat,
                    )
                    firestore.collection("users")
                        .document(userId)
                        .collection("historyMedic")
                        .document()
                        .set(user)
                        .addOnSuccessListener {
                            binding.progressBar.visibility = View.GONE
                            playAnimation()
                        }
                        .addOnFailureListener {
                            binding.progressBar.visibility = View.GONE
                            alertErrorConnect()
                        }
                }
            }
        }
    }
    fun alertErrorConnect() {
        AlertDialog.Builder(this@CameraActivity).apply {
            setTitle("Maaf")
            setMessage("Koneksimu bermasalah :(")
            setPositiveButton("Oke") { _, _ ->
            }
            setCancelable(false)
            create()
            show()
        }
    }

    fun alertErrorFailed() {
        AlertDialog.Builder(this@CameraActivity).apply {
            setTitle("Maaf")
            setMessage("Terjadi Kesalahan :(")
            setPositiveButton("Oke") { _, _ ->
            }
            setCancelable(false)
            create()
            show()
        }
    }

    companion object {
        var result: String? = null
        var accuracy: String? = null
        var deskripsi: String? = null
        var imgObat: String? = null
        var namaObat: String? = null
        var pemakaianObat: String? = null
        var detailObat: String? = null
        var imageUrl: String? = null
    }
}