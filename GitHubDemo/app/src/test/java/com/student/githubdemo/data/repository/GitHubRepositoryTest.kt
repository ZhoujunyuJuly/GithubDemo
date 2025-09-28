package com.student.githubdemo.data.repository

import com.student.githubdemo.data.api.GitHubApi
import com.student.githubdemo.data.local.PreferencesManager
import com.student.githubdemo.data.model.*
import com.student.githubdemo.util.Resource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import retrofit2.Response

class GitHubRepositoryTest {

    @Mock
    private lateinit var api: GitHubApi

    @Mock
    private lateinit var preferencesManager: PreferencesManager

    private lateinit var repository: GitHubRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = GitHubRepository(api, preferencesManager)
    }

    @Test
    fun `searchRepositories returns success when api call succeeds`() = runTest {
        // Given
        val mockRepository = Repository(
            id = 1,
            name = "test-repo",
            fullName = "user/test-repo",
            description = "Test repository",
            htmlUrl = "https://github.com/user/test-repo",
            cloneUrl = "https://github.com/user/test-repo.git",
            language = "Kotlin",
            stargazersCount = 100,
            watchersCount = 50,
            forksCount = 25,
            openIssuesCount = 5,
            createdAt = "2023-01-01T00:00:00Z",
            updatedAt = "2023-01-01T00:00:00Z",
            pushedAt = "2023-01-01T00:00:00Z",
            private = false,
            owner = Owner(1, "user", "https://avatar.url")
        )
        
        val searchResponse = SearchResponse(
            totalCount = 1,
            incompleteResults = false,
            items = listOf(mockRepository)
        )
        
        whenever(api.searchRepositories("kotlin", "stars", "desc", 30, 1))
            .thenReturn(Response.success(searchResponse))

        // When
        val result = repository.searchRepositories("kotlin")

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data.items.size)
        assertEquals("test-repo", result.data.items[0].name)
    }

    @Test
    fun `getCurrentUser returns error when not logged in`() = runTest {
        // Given
        whenever(preferencesManager.accessToken).thenReturn(flowOf(null))

        // When
        val result = repository.getCurrentUser()

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("未登录", (result as Resource.Error).message)
    }

    @Test
    fun `getCurrentUser returns success when logged in and api succeeds`() = runTest {
        // Given
        val mockUser = GitHubUser(
            id = 1,
            login = "testuser",
            avatarUrl = "https://avatar.url",
            name = "Test User",
            company = "Test Company",
            blog = "https://blog.url",
            location = "Test Location",
            email = "test@example.com",
            bio = "Test bio",
            publicRepos = 10,
            followers = 100,
            following = 50,
            createdAt = "2023-01-01T00:00:00Z",
            updatedAt = "2023-01-01T00:00:00Z"
        )
        
        whenever(preferencesManager.accessToken).thenReturn(flowOf("test-token"))
        whenever(api.getCurrentUser("Bearer test-token")).thenReturn(Response.success(mockUser))

        // When
        val result = repository.getCurrentUser()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals("testuser", (result as Resource.Success).data.login)
        assertEquals("Test User", result.data.name)
    }
}
