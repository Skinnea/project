package com.example.projectcapstones.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.projectcapstones.databinding.ActivityMainBinding
import android.Manifest
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.projectcapstones.ui.about.AboutActivity
import com.example.projectcapstones.ui.login.LoginActivity
import com.example.projectcapstones.ui.chat.ChatActivity
import com.example.projectcapstones.ui.news.NewsActivity
import com.example.projectcapstones.ui.upload.CameraActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.bumptech.glide.request.target.Target
import com.example.projectcapstones.R
import com.example.projectcapstones.ui.history.HistoryActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: MainViewModel
    private var isButtonClicked = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.loadProfileData()
        auth = Firebase.auth
        setupView()
        setupButton()
        profile()
        playAnimation()
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

    private fun profile() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.loadProfileData()
        viewModel.observeProfileData(this) { profileData ->
            binding.itemProfile.nameProfile.text = profileData.name
            binding.itemProfile.emailProfile.text = profileData.email
            Glide.with(this)
                .load(profileData.photoUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.itemProfile.imgProfile.setImageDrawable(resource)
                        binding.menuCard.greetingText.text =
                            getString(R.string.welcome, profileData.name.substringBefore(" "))
                        return true
                    }
                })
                .into(binding.itemProfile.imgProfile)
        }
    }

    override fun onResume() {
        binding.buttonMenu.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.blue_500)
        )
        super.onResume()
        val camera = ObjectAnimator.ofFloat(
            binding.buttonCamera,
            View.TRANSLATION_Y,
            0f
        )
        val history = ObjectAnimator.ofFloat(
            binding.buttonChat,
            View.TRANSLATION_Y,
            0f
        )
        val about = ObjectAnimator.ofFloat(
            binding.buttonAbout,
            View.TRANSLATION_Y,
            0f
        )
        val menu = ObjectAnimator.ofFloat(
            binding.buttonMenu,
            View.ROTATION,
            0f
        )
        val set = AnimatorSet()
        set.playTogether(menu, about, history, camera)
        set.duration = 0
        set.start()
        isButtonClicked = false
    }

    private fun setupButton() {
        binding.buttonMenu.setOnClickListener {
            val newButtonClicked = !isButtonClicked
            val camera = ObjectAnimator.ofFloat(
                binding.buttonCamera,
                View.TRANSLATION_Y,
                if (newButtonClicked) -600f else 0f
            )
            val history = ObjectAnimator.ofFloat(
                binding.buttonChat,
                View.TRANSLATION_Y,
                if (newButtonClicked) -400f else 0f
            )
            val about = ObjectAnimator.ofFloat(
                binding.buttonAbout,
                View.TRANSLATION_Y,
                if (newButtonClicked) -200f else 0f
            )
            val menu = ObjectAnimator.ofFloat(
                binding.buttonMenu,
                View.ROTATION,
                if (newButtonClicked) 45f else 0f
            )
            binding.buttonMenu.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    if (newButtonClicked) R.color.red_200 else R.color.blue_500
                )
            )
            val set = AnimatorSet()
            set.playTogether(menu, about, history, camera)
            set.duration = 300
            set.start()
            isButtonClicked = newButtonClicked
        }
        binding.buttonCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        binding.buttonChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
        binding.buttonAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        binding.menuCard.cardMenu2.setOnClickListener {
            auth = Firebase.auth
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle("Maaf!")
                    setMessage("Anda perlu login google terlebih dahulu")
                    setPositiveButton("Oke") { _, _ ->
                    }
                    setCancelable(false)
                    create()
                    show()
                }
            } else {
                startActivity(Intent(this, HistoryActivity::class.java))
            }
        }
        binding.menuCard.cardMenu1.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
        binding.itemProfile.buttonLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun playAnimation() {
        val cardMenu1 =
            ObjectAnimator.ofFloat(binding.menuCard.cardMenu1, View.ALPHA, 1f).setDuration(200)
        val cardMenu2 =
            ObjectAnimator.ofFloat(binding.menuCard.cardMenu2, View.ALPHA, 1f).setDuration(200)
        val cardMenuText =
            ObjectAnimator.ofFloat(binding.menuCard.root, View.ALPHA, 1f).setDuration(200)
        val onBoard =
            ObjectAnimator.ofFloat(binding.onboardBanner.root, View.ALPHA, 1f).setDuration(200)
        val menu = ObjectAnimator.ofFloat(binding.buttonMenu, View.ALPHA, 1f).setDuration(200)
        val cam = ObjectAnimator.ofFloat(binding.buttonCamera, View.ALPHA, 1f).setDuration(200)
        val history = ObjectAnimator.ofFloat(binding.buttonChat, View.ALPHA, 1f).setDuration(200)
        val about = ObjectAnimator.ofFloat(binding.buttonAbout, View.ALPHA, 1f).setDuration(200)
        val profile =
            ObjectAnimator.ofFloat(binding.itemProfile.root, View.ALPHA, 1f).setDuration(200)
        val together = AnimatorSet().apply {
            playTogether(menu, about, history, cam)
        }
        AnimatorSet().apply {
            playSequentially(profile, cardMenuText, cardMenu1, cardMenu2, onBoard, together)
            startDelay = 200
        }.start()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}