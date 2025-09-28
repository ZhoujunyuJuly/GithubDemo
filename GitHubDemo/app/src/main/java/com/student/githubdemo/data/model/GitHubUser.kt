package com.student.githubdemo.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubUser(
    @Json(name = "id") val id: Long,
    @Json(name = "login") val login: String,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "name") val name: String?,
    @Json(name = "company") val company: String?,
    @Json(name = "blog") val blog: String?,
    @Json(name = "location") val location: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "bio") val bio: String?,
    @Json(name = "public_repos") val publicRepos: Int,
    @Json(name = "followers") val followers: Int,
    @Json(name = "following") val following: Int,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String
)
