package com.student.githubdemo.data.api

import com.student.githubdemo.data.model.OAuthToken
import com.student.githubdemo.data.model.OAuthTokenRequest
import retrofit2.Response
import retrofit2.http.*

interface OAuthApi {
    
    @POST("https://github.com/login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun getAccessToken(@Body request: OAuthTokenRequest): Response<OAuthToken>
}
