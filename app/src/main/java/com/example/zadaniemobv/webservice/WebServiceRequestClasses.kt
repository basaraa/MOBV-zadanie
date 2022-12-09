package com.example.zadaniemobv.webservice

import com.airbnb.lottie.animation.content.Content
import com.google.gson.annotations.SerializedName
import retrofit2.http.Headers

data class UserRegisterLoginPostBody(
    val name: String,
    val password: String,
)

data class AddFriendPostBody(
    val contact: String
)

data class RefreshTokenPostBody(
    val refresh: String
)

data class CheckIntoPubPostBody(
    val id: String,
    val name: String,
    val type: String,
    val lat: Double,
    val lon: Double
)



