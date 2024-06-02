package com.dicoding.picodiploma.loginwithanimation.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.remote.ApiClient
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this, ApiClient().getApiService("token"))
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(this.dataStore)
        userRepository = Injection.injectUserRepository(this, ApiClient().getApiService("token"))

        setupView()
        setupAction()
        setupPasswordValidation()
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

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty()) {
                binding.emailEditText.error = "Email harus diisi"
                return@setOnClickListener
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditText.error = "Format email tidak valid"
                return@setOnClickListener
            } else if (password.isEmpty()) {
                binding.passwordEditText.error = "Password harus diisi"
                return@setOnClickListener
            }

            showLoading(true)
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = viewModel.login(email, password)
                if (response.loginResult != null) {
                    val user = UserModel(email, response.loginResult.token, true)
                    viewModel.saveSession(user)
                    Log.d("LoginActivity", checkUserSession().toString())
                    withContext(Dispatchers.IO) {
                        checkUserSession()
                        intentMainActivity()
                    }
                } else {
                    showAlertDialog(
                        "Login Gagal",
                    )
                }
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    showAlertDialog(
                        "Email atau Password Salah",
                    )
                } else {
                    showAlertDialog(
                        "Login Gagal",
                    )
                }
            } catch (e: Exception) {
                showAlertDialog(
                    "Login Gagal",
                )
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this@LoginActivity).apply {
            setTitle("Oops!")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupPasswordValidation() {
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (password.length < 8) {
                    binding.passwordEditText.error = "Password tidak boleh kurang dari 8 karakter"
                    binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                } else {
                    binding.passwordEditText.error = null
                }
            }
        })
    }

    private fun checkUserSession() {
        Log.d("LoginActivity", "Checking user session")
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            val userModel = userPreference.getSession().first()
            if (userModel.token.isNotEmpty()) {
                if (userModel.isLogin) {
                    Log.d("LoginActivity", "User session found. Navigating to main activity.")
                    intentMainActivity()
                } else {
                    Log.d("LoginActivity", "User session not found.")
                    binding.progressBar.visibility = View.GONE
                }
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun intentMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(150)
        val emailbox = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(150)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordbox = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(150)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(150)
        val image = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(400)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(150)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(150)

        AnimatorSet().apply {
            playSequentially(title, message, image, email, emailbox, password, passwordbox, login)
            start()
        }
    }
}
