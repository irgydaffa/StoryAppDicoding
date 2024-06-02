package com.dicoding.picodiploma.loginwithanimation.response

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult? = null
)

data class LoginResult(
    val name: String,
    val userId: String,
    val token: String,
    val isLogin: Boolean
)