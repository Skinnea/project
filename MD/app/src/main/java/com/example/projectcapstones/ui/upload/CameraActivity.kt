package com.example.projectcapstones.ui.upload

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.projectcapstones.configcamera.createFile
import com.example.projectcapstones.configcamera.reduceFileImage
import com.example.projectcapstones.configcamera.rotateFile
import com.example.projectcapstones.configcamera.uriToFile
import com.example.projectcapstones.databinding.ActivityCameraBinding
import com.example.projectcapstones.network.ApiConfig
import com.example.projectcapstones.response.FileUploadResponse
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
                    val myFile = output.savedUri?.let { uri ->
                        val file = File(uri.path.toString())
                        rotateFile(file, cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                        file
                    }
                    if (myFile != null) {
                        getFile = myFile
                        binding.previewImage.previewImageView.setImageBitmap(
                            BitmapFactory.decodeFile(
                                myFile.path
                            )
                        )
                        playAnimation()
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
            uploadImage()
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
                playAnimation()
            }
        }
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
            uploadImageRequest.enqueue(object : Callback<FileUploadResponse> {
                override fun onResponse(
                    call: Call<FileUploadResponse>,
                    response: Response<FileUploadResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val responseString = responseBody.responseBody
                            Log.d("uplod1", (responseString ?: "").toString())
                        } else {
                            Log.d("uplod2", "Tidak ada hasil yang ditemukan.")
                        }
                    }
                }

                override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                    Log.e("uplod3", t.message ?: "")
                }
            })
        } else {
            Toast.makeText(
                this@CameraActivity,
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun playAnimation() {
        binding.progressBar.visibility = View.VISIBLE
        val previewImg =
            ObjectAnimator.ofFloat(binding.previewImage.root, View.ALPHA, 1f).setDuration(200)
        val animatorSet = AnimatorSet().apply {
            playSequentially(previewImg)
            startDelay = 200
        }
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.progressBar.visibility = View.INVISIBLE
            }
        })
        animatorSet.start()
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

    public override fun onResume() {
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
}