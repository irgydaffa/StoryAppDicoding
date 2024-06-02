package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.response.RegisterResponse
import com.google.gson.Gson
import retrofit2.HttpException

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return try {
            Log.d(
                "RegisterViewModel",
                "Requesting registration to server"
            )
            val response = repository.register(name, email, password)

            Log.d(
                "RegisterViewModel",
                "Registrasi Success"
            )
            response
        } catch (e: HttpException) {
            Log.e(
                "RegisterViewModel",
                "Registrasi Failed",
                e
            )

            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Log.e("RegisterViewModel", "Error response: $errorBody")
            throw e
        }
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }
}