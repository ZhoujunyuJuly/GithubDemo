package com.student.githubdemo.data.repository

import com.student.githubdemo.data.api.GitHubApi
import com.student.githubdemo.data.local.PreferencesManager
import com.student.githubdemo.data.model.*
import com.student.githubdemo.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepository @Inject constructor(
    private val api: GitHubApi,
    private val preferencesManager: PreferencesManager
) {
    
    suspend fun searchRepositories(
        query: String,
        sort: String = "stars",
        order: String = "desc",
        page: Int = 1
    ): Resource<SearchResponse> {
        return try {
            val response = api.searchRepositories(query, sort, order, 30, page)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("搜索失败: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("网络错误: ${e.message}")
        }
    }
    
    suspend fun getCurrentUser(): Resource<GitHubUser> {
        return try {
            val token = preferencesManager.accessToken.first()
            if (token.isNullOrEmpty()) {
                return Resource.Error("未登录")
            }
            
            val response = api.getCurrentUser("Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("获取用户信息失败: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("网络错误: ${e.message}")
        }
    }
    
    suspend fun getUserRepositories(page: Int = 1): Resource<List<Repository>> {
        return try {
            val token = preferencesManager.accessToken.first()
            if (token.isNullOrEmpty()) {
                return Resource.Error("未登录")
            }
            
            val response = api.getUserRepositories("Bearer $token", "updated", 30, page)
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("获取仓库列表失败: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("网络错误: ${e.message}")
        }
    }
    
    suspend fun getRepository(owner: String, repo: String): Resource<Repository> {
        return try {
            val response = api.getRepository(owner, repo)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("获取仓库信息失败: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("网络错误: ${e.message}")
        }
    }
    
    suspend fun createIssue(
        owner: String,
        repo: String,
        title: String,
        body: String?
    ): Resource<Issue> {
        return try {
            val token = preferencesManager.accessToken.first()
            if (token.isNullOrEmpty()) {
                return Resource.Error("未登录")
            }
            
            val request = CreateIssueRequest(title, body)
            val response = api.createIssue("Bearer $token", owner, repo, request)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("创建Issue失败: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("网络错误: ${e.message}")
        }
    }
    
    suspend fun getRepositoryIssues(
        owner: String,
        repo: String,
        page: Int = 1
    ): Resource<List<Issue>> {
        return try {
            val response = api.getRepositoryIssues(owner, repo, "all", 30, page)
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("获取Issues失败: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("网络错误: ${e.message}")
        }
    }
    
    fun getAccessToken(): Flow<String?> = preferencesManager.accessToken
    fun getUserLogin(): Flow<String?> = preferencesManager.userLogin
    
    suspend fun saveUserData(token: String, login: String) {
        preferencesManager.saveAccessToken(token)
        preferencesManager.saveUserLogin(login)
    }
    
    suspend fun clearUserData() {
        preferencesManager.clearUserData()
    }
}
