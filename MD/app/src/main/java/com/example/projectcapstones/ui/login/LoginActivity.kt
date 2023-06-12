package com.example.projectcapstones.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.projectcapstones.ui.home.MainActivity
import com.example.projectcapstones.R
import com.example.projectcapstones.customview.MyButton
import com.example.projectcapstones.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var myButton: MyButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myButton = binding.terms.okeButton
        myButton.isEnabled = false
        binding.progressBar.visibility = View.GONE
        binding.progressText.visibility = View.GONE
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        binding.signInButton.setOnClickListener {
            if (binding.terms.check.isChecked) {
                signIn()
            } else {
                termAnimation()
            }
        }
        binding.terms.check.setOnClickListener {
            myButton.isEnabled = binding.terms.check.isChecked
        }
        binding.terms.okeButton.setOnClickListener{
            termAnimationClose()
            binding.terms.check.isChecked = true
        }
        binding.guestButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.terms.againButton.setOnClickListener {
            termAnimationClose()
            binding.terms.check.isChecked = false
            binding.terms.check.isChecked = false
        }
        setupView()
        playAnimation()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        binding.progressBar.visibility = View.VISIBLE
        binding.progressText.visibility = View.VISIBLE
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                binding.progressBar.visibility = View.GONE
                binding.progressText.visibility = View.GONE
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
                AlertDialog.Builder(this@LoginActivity).apply {
                    setTitle("Yeayy!!")
                    setMessage("Login sukses")
                    setPositiveButton("Lanjut") { _, _ ->
                    }
                    setCancelable(false)
                    create()
                    show()
                }
            } catch (_: ApiException) {
                binding.progressBar.visibility = View.GONE
                binding.progressText.visibility = View.GONE
                AlertDialog.Builder(this@LoginActivity).apply {
                    setTitle("Maaf")
                    setMessage("Login gagal :(")
                    setPositiveButton("Oke") { _, _ ->
                    }
                    setCancelable(false)
                    create()
                    show()
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun termAnimation(){
        binding.signInButton.isEnabled = false
        binding.guestButton.isEnabled = false
        val term = ObjectAnimator.ofFloat(binding.terms.root, View.ALPHA, 1f).setDuration(50)
        AnimatorSet().apply {
            playSequentially(term)
            startDelay = 50
        }.start()
    }

    private fun termAnimationClose(){
        binding.signInButton.isEnabled = true
        binding.guestButton.isEnabled = true
        val term = ObjectAnimator.ofFloat(binding.terms.root, View.ALPHA, 0f).setDuration(50)
        AnimatorSet().apply {
            playSequentially(term)
            startDelay = 50
        }.start()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageViewWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3000
            startDelay = 300
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val login = ObjectAnimator.ofFloat(binding.imageViewWelcome, View.ALPHA, 1f).setDuration(200)
        val header = ObjectAnimator.ofFloat(binding.imageViewHeader, View.ALPHA, 1f).setDuration(200)
        val buttonLogin = ObjectAnimator.ofFloat(binding.signInButton, View.ALPHA, 1f).setDuration(200)
        val buttonGuest = ObjectAnimator.ofFloat(binding.guestButton, View.ALPHA, 1f).setDuration(200)
        val together = AnimatorSet().apply {
            playTogether(buttonLogin, buttonGuest)
        }
        AnimatorSet().apply {
            playSequentially(header, login, together)
            startDelay = 200
        }.start()
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
