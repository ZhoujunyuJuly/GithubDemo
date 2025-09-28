package com.student.githubdemo.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Issue(
    @Json(name = "id") val id: Long,
    @Json(name = "number") val number: Int,
    @Json(name = "title") val title: String,
    @Json(name = "body") val body: String?,
    @Json(name = "state") val state: String,
    @Json(name = "html_url") val htmlUrl: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "user") val user: Owner
)

@JsonClass(generateAdapter = true)
data class CreateIssueRequest(
    @Json(name = "title") val title: String,
    @Json(name = "body") val body: String?
)
