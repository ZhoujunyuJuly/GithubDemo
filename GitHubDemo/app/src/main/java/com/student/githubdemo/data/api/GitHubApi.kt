package com.student.githubdemo.data.api

import com.student.githubdemo.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface GitHubApi {
    
    // 获取热门仓库
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<SearchResponse>
    
    // 获取用户信息
    @GET("user")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<GitHubUser>
    
    // 获取用户仓库
    @GET("user/repos")
    suspend fun getUserRepositories(
        @Header("Authorization") token: String,
        @Query("sort") sort: String = "updated",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<List<Repository>>
    
    // 获取指定仓库信息
    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<Repository>
    
    // 创建Issue
    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issue: CreateIssueRequest
    ): Response<Issue>
    
    // 获取仓库Issues
    @GET("repos/{owner}/{repo}/issues")
    suspend fun getRepositoryIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "all",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<List<Issue>>
}
