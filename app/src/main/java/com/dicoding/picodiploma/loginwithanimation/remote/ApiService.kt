package com.dicoding.picodiploma.loginwithanimation.remote

import com.dicoding.picodiploma.loginwithanimation.request.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.request.RegisterRequest
import com.dicoding.picodiploma.loginwithanimation.request.StoryRequest
import com.dicoding.picodiploma.loginwithanimation.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.response.NewStoryResponse
import com.dicoding.picodiploma.loginwithanimation.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    @POST("register")
    suspend fun register(
        @Body body: RegisterRequest
    ): RegisterResponse

    @GET("stories")
    suspend fun storyList(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file:MultipartBody.Part,
        @Part("description") description: RequestBody
    ): NewStoryResponse
}