package com.student.githubdemo.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Repository(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "description") val description: String?,
    @Json(name = "html_url") val htmlUrl: String,
    @Json(name = "clone_url") val cloneUrl: String,
    @Json(name = "language") val language: String?,
    @Json(name = "stargazers_count") val stargazersCount: Int,
    @Json(name = "watchers_count") val watchersCount: Int,
    @Json(name = "forks_count") val forksCount: Int,
    @Json(name = "open_issues_count") val openIssuesCount: Int,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "pushed_at") val pushedAt: String?,
    @Json(name = "private") val private: Boolean,
    @Json(name = "owner") val owner: Owner
)

@JsonClass(generateAdapter = true)
data class Owner(
    @Json(name = "id") val id: Long,
    @Json(name = "login") val login: String,
    @Json(name = "avatar_url") val avatarUrl: String
)
