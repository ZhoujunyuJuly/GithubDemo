package com.student.githubdemo.data.repository

import com.student.githubdemo.data.api.OAuthApi
import com.student.githubdemo.data.local.PreferencesManager
import com.student.githubdemo.data.model.OAuthToken
import com.student.githubdemo.data.model.OAuthTokenRequest
import com.student.githubdemo.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val oauthApi: OAuthApi,
    private val preferencesManager: PreferencesManager
) {
    
    suspend fun exchangeCodeForToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Resource<OAuthToken> {
        return try {
            val request = OAuthTokenRequest(clientId, clientSecret, code)
            val response = oauthApi.getAccessToken(request)
            
            if (response.isSuccessful) {
                val token = response.body()!!
                Resource.Success(token)
            } else {
                Resource.Error("获取访问令牌失败: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("网络错误: ${e.message}")
        }
    }
    
    suspend fun logout() {
        preferencesManager.clearUserData()
    }
}
