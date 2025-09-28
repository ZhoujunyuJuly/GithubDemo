package com.student.githubdemo.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OAuthToken(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "scope") val scope: String?
)

@JsonClass(generateAdapter = true)
data class OAuthTokenRequest(
    @Json(name = "client_id") val clientId: String,
    @Json(name = "client_secret") val clientSecret: String,
    @Json(name = "code") val code: String
)
