package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    suspend fun login (email: String, password: String): LoginResponse {
        return try {
            val response = repository.login(email, password)
            if (response.loginResult != null) {
                response.loginResult.token?.let { token ->
                    val user = UserModel(email, token, true)
                    saveSession(user)
                }
            }
            response
        } catch (e: Exception) {
            throw e
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}