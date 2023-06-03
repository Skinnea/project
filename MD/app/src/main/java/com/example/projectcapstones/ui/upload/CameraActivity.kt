package com.example.projectcapstones.ui.upload

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.example.projectcapstones.configcamera.*
import com.example.projectcapstones.data.ResultSkin
import com.example.projectcapstones.database.SkinData
import com.example.projectcapstones.databinding.ActivityCameraBinding
import com.example.projectcapstones.ui.detail.DetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var classifierSkin: ClassifierSkin
    private lateinit var image: Bitmap
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val model = "model.tflite"
    private val label = "labels.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButton()
        setupView()
        classifierSkin = ClassifierSkin(assets, model, label)
    }

    private fun setupButton() {
        binding.captureImage.setOnClickListener {
            takePhoto()
        }
        binding.galleryImage.setOnClickListener {
            startGallery()
        }
        binding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
        binding.previewImage.uploadButton.setOnClickListener {
            playAnimationRestart()
            uploadHistory()
            val intent = Intent(this@CameraActivity, DetailActivity::class.java)
            val resultText = binding.previewImage.result.text.toString()
            intent.putExtra("resultText", resultText)
            intent.putExtra("imageResult", image)
            startActivity(intent)
        }
        binding.previewImage.againButton.setOnClickListener {
            playAnimationRestart()
        }
    }

    private fun resultScan(imgScan: Bitmap): ClassifierSkin.Recognition? {
        val results = classifierSkin.scanImage(imgScan).firstOrNull()
        binding.previewImage.result.text = results?.title
        binding.previewImage.accurate.text = results?.confidence.toString()
        return results
    }

    private fun uploadHistory() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val data = outputStream.toByteArray()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val folderName = "images/$user"
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val imageRef = storageRef.child("$folderName/$fileName")
            val uploadTask = imageRef.putBytes(data)
            uploadTask.addOnFailureListener {
            }.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val firestore = FirebaseFirestore.getInstance()
                    val results = resultScan(image)
                    val resultSkinData = SkinData.results.find { it.nameSkin == results?.title }
                    if (resultSkinData != null) {
                        val result = ResultSkin(
                            nameSkin = resultSkinData.nameSkin,
                            descSkin = resultSkinData.descSkin,
                            urlImgMedic = resultSkinData.urlImgMedic,
                            nameMedic = resultSkinData.nameMedic,
                            suggestMedic = resultSkinData.suggestMedic,
                            descMedic = resultSkinData.descMedic,
                            imageUrl = downloadUrl,
                            timestamp = System.currentTimeMillis()
                        )
                        firestore.collection("users")
                            .document(user.uid)
                            .collection("results")
                            .document()
                            .set(result)
                    }
                }
            }
        }
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
                        val imgScan = reduceImage(myFile.path)
                        binding.previewImage.previewImageView.setImageBitmap(imgScan)
                        resultScan(imgScan)
                        image = imgScan
                        playAnimation()
                    }
                }
            }
        )
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@CameraActivity)
                val imgScan = reduceImage(myFile.path)
                binding.previewImage.previewImageView.setImageBitmap(imgScan)
                resultScan(imgScan)
                image = imgScan
                playAnimation()
            }
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