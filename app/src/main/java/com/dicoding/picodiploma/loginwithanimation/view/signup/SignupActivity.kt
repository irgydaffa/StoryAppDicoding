package com.dicoding.picodiploma.loginwithanimation.view.signup

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
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.remote.ApiClient
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this, ApiClient().getApiService("token"))
    }
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = Injection.injectUserRepository(this, ApiClient().getApiService("token"))

        setupValidation()
        setupView()
        setupAction()
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
        binding.signupButton.setOnClickListener {
            val password = binding.passwordEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val name = binding.nameEditText.text.toString()

            Log.d("SignupActivity", "Button Daftar ditekan")

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                Log.d(
                    "SignupActivity",
                    "Data lengkap: Name=$name, Email=$email, Password=$password"
                )
                binding.progressBar.visibility = View.VISIBLE
                binding.signupButton.visibility = View.INVISIBLE

                lifecycleScope.launch {
                    try {
                        val response = viewModel.register(name, email, password)
                        Log.d("SignupActivity", "Percobaan registrasi selesai")

                        val dialogTitle =
                            if (!response.error!!) "Welcome!" else if (response.message == "Email is already taken") "Email has been used" else "Failed registration"
                        val dialogMessage =
                            if (!response.error) "Your account $email is successfully created." else if (response.message == "Email is already taken") "$email has been used. Try another." else response.message
                                ?: "Please try again later."

                        val isError = response.error
                        showDialog(dialogTitle, dialogMessage, isError)

                    } catch (e: Exception) {
                        Log.e("SignupActivity", "Gagal melakukan registrasi", e)
                        showDialog("Failed registration", "Please try again later.", true)
                    } finally {
                        binding.progressBar.visibility = View.GONE
                        binding.signupButton.visibility = View.VISIBLE
                    }
                }
            } else {
                Log.w("SignupActivity", "Data is not complete, yet.")
                showDialog("Failed registration", "Please fill out the required data.", true)
            }
        }
    }

    private fun showDialog(title: String, message: String, isError: Boolean) {
        AlertDialog.Builder(this@SignupActivity).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                if (!isError) {
                    val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            create()
            show()
        }
    }



    private fun setupValidation() {
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (!viewModel.isPasswordValid(password)) {
                    binding.passwordEditText.error = "Password tidak boleh kurang dari 8 karakter"
                    binding.passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                } else {
                    binding.passwordEditText.error = null
                }
            }
        })
    }
}