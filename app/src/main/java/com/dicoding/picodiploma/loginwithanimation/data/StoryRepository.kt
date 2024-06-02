package com.dicoding.picodiploma.loginwithanimation.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.pager.StoryPager
import com.dicoding.picodiploma.loginwithanimation.remote.ApiService
import com.dicoding.picodiploma.loginwithanimation.request.StoryRequest
import com.dicoding.picodiploma.loginwithanimation.response.ListStory
import com.dicoding.picodiploma.loginwithanimation.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun getStories(): StoryResponse {
        try {
            userPreference.getSession().firstOrNull()?.token
                ?: throw NullPointerException("Token is null")
            return apiService.storyList()
        } catch (e: Exception) {
            throw e
        }
    }

    companion object {
        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository {
            return StoryRepository(apiService, userPreference)
        }
    }

    fun getStoriesPager(): Flow<PagingData<ListStory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPager(apiService) }
        ).flow
    }
}
