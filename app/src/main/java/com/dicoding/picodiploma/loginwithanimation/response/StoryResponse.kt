package com.dicoding.picodiploma.loginwithanimation.response

data class StoryResponse(
    val listStory: List<ListStory>,
    val error: Boolean? = null,
    val message: String? = null
)

data class ListStory(
    val photoUrl: String? = null,
    val createdAt: String,
    val name: String? = null,
    val description: String? = null,
    val lon: Double? = null,
    val id: String? = null,
    val lat: Double? = null
)