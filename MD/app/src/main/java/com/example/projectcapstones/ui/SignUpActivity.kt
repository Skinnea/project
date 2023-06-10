package com.example.projectcapstones.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.projectcapstones.R
import com.example.projectcapstones.customview.MyButton
import com.example.projectcapstones.customview.MyEmailText
import com.example.projectcapstones.customview.MyNameText
import com.example.projectcapstones.customview.MyPasswordText
import com.example.projectcapstones.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var myButton: MyButton
    private lateinit var myNameText: MyNameText
    private lateinit var myEmailText: MyEmailText
    private lateinit var myPasswordText: MyPasswordText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myButton = binding.signupButton
        myEmailText = binding.edRegisterEmail
        myNameText = binding.edRegisterName
        myPasswordText = binding.edRegisterPassword
        setupView()
        playAnimation()
        setMyButtonEnable()
        myNameText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
        })

        myEmailText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
        })

        myPasswordText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
        })

        myButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && email.contains("@") && email.endsWith(
                    ".com"
                ) && password.isNotEmpty() && password.length >= 8
            ) {
                register(name, email, password)
                binding.progressBar.visibility = View.VISIBLE
                binding.progressText.visibility = View.VISIBLE
            }
        }
    }

    private fun register(name: String, email: String, password: String) {
        auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(name, email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    AlertDialog.Builder(this@SignUpActivity).apply {
                        setTitle("Yeah!")
                        setMessage("Akunnya sudah jadi nih. Yuk, login.")
                        setPositiveButton("Lanjut") { _, _ ->
                            finish()
                        }
                        setCancelable(false)
                        create()
                        show()
                    }
                    val user: FirebaseUser? = auth.currentUser
                    // Lakukan tindakan setelah registrasi berhasil
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.progressText.visibility = View.GONE
                    AlertDialog.Builder(this@SignUpActivity).apply {
                        setTitle("Maaf")
                        setMessage("Terjadi Kesalahan")
                        setPositiveButton("Oke") { _, _ ->
                        }
                        create()
                        show()
                    }
                }
            }
    }

    private fun setMyButtonEnable() {
        val name = myNameText.text.toString()
        val email = myEmailText.text.toString()
        val password = myPasswordText.text.toString()
        val validName = name.isNotEmpty()
        val validEmail = email.isNotEmpty() && email.contains("@") && email.endsWith(".com")
        val validPassword = password.isNotEmpty() && password.length >= 8
        myButton.isEnabled = validName && validEmail && validPassword
        if (!validName || !validEmail || !validPassword) {
            myButton.isEnabled = false
        }
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageViewWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameTextCustomView =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val nameTextCustomViewLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextCustomView =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val emailTextCustomViewLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordCustomView =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f)
                .setDuration(500)
        val passwordTextCustomViewLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        val together = AnimatorSet().apply {
            playTogether(
                nameTextView,
                nameTextCustomView,
                nameTextCustomViewLayout,
                emailTextView,
                emailTextCustomView,
                emailTextCustomViewLayout,
                passwordTextView,
                passwordCustomView,
                passwordTextCustomViewLayout
            )
        }

        AnimatorSet().apply {
            playSequentially(
                together,
                signup
            )
            startDelay = 500
        }.start()
    }
}