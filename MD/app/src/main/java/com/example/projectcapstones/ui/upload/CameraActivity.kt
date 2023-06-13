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
import androidx.lifecycle.ViewModelProvider
import com.example.projectcapstones.configcamera.createFile
import com.example.projectcapstones.configcamera.rotateFile
import com.example.projectcapstones.configcamera.uriToFile
import com.example.projectcapstones.data.ResultSkin
import com.example.projectcapstones.databinding.ActivityCameraBinding
import com.example.projectcapstones.ui.detail.DetailActivity
import java.io.File
import java.util.*

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var viewModel: UploadViewModel
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var result: String? = null
    private var accuracy: String? = null
    private var deskripsi: String? = null
    private var imgObat: String? = null
    private var namaObat: String? = null
    private var pemakaianObat: String? = null
    private var detailObat: String? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[UploadViewModel::class.java]
        binding.progressBar.visibility = View.GONE
        binding.progressText.visibility = View.GONE
        setupButton()
        setupView()
        setupViewModel()
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
                        val rotateImage = rotateFile(
                            BitmapFactory.decodeFile(file.path),
                            cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                        )
                        getFile = file
                        binding.previewImage.previewImageView.setImageBitmap(rotateImage)
                        viewModel.uploadImage()
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
            val results = ResultSkin(
                imageUrl,
                result,
                accuracy,
                deskripsi,
                imgObat,
                namaObat,
                pemakaianObat,
                detailObat,
            )
            val intent = Intent(this@CameraActivity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_RESULT_SKIN, results)
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
                viewModel.uploadImage()
            }
        }
    }

    private fun playAnimation() {
        binding.progressBar.visibility = View.GONE
        binding.progressText.visibility = View.GONE
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
        binding.progressBar.visibility = View.GONE
        binding.progressText.visibility = View.GONE
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

    private fun setupViewModel(){
        viewModel.upload.observe(this) { response ->
            result = response.result
            accuracy = response.accuracy
            deskripsi = response.deskripsi
            namaObat = response.namaObat
            pemakaianObat = response.pemakaianObat
            detailObat = response.detailObat
            imgObat = response.imgObat
            binding.previewImage.result.text = result
            binding.previewImage.accurate.text = accuracy
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.progressText.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.playAnimation.observe(this) { isPlayAnimation ->
            if (isPlayAnimation) playAnimation() else playAnimationRestart()
        }
        viewModel.message.observe(this) { errorMessage ->
            AlertDialog.Builder(this).apply {
                setTitle("Maaf")
                setMessage(errorMessage)
                setPositiveButton("Oke") { _, _ -> }
                create()
                show()
            }
        }
    }

    companion object {
        var getFile: File? = null
    }
}