package com.student.githubdemo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.githubdemo.BuildConfig
import com.student.githubdemo.data.model.GitHubUser
import com.student.githubdemo.data.repository.AuthRepository
import com.student.githubdemo.data.repository.GitHubRepository
import com.student.githubdemo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val gitHubRepository: GitHubRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<Resource<GitHubUser>?>(null)
    val authState: StateFlow<Resource<GitHubUser>?> = _authState.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        checkAuthStatus()
    }
    
    private fun checkAuthStatus() {
        viewModelScope.launch {
            gitHubRepository.getAccessToken().collect { token ->
                if (!token.isNullOrEmpty()) {
                    _isLoggedIn.value = true
                    loadCurrentUser()
                } else {
                    _isLoggedIn.value = false
                    _authState.value = null
                }
            }
        }
    }
    
    fun handleOAuthCallback(code: String) {
        viewModelScope.launch {
            _authState.value = Resource.Loading()
            
            val tokenResult = authRepository.exchangeCodeForToken(
                BuildConfig.GITHUB_CLIENT_ID,
                BuildConfig.GITHUB_CLIENT_SECRET,
                code
            )
            
            when (tokenResult) {
                is Resource.Success -> {
                    val token = tokenResult.data!!
                    // 先保存token，然后获取用户信息
                    gitHubRepository.saveUserData(token.accessToken, "")
                    
                    val userResult = gitHubRepository.getCurrentUser()
                    when (userResult) {
                        is Resource.Success -> {
                            val user = userResult.data!!
                            gitHubRepository.saveUserData(token.accessToken, user.login)
                            _authState.value = Resource.Success(user)
                            _isLoggedIn.value = true
                        }
                        is Resource.Error -> {
                            _authState.value = Resource.Error(userResult.message ?: "获取用户信息失败")
                        }
                        else -> {}
                    }
                }
                is Resource.Error -> {
                    _authState.value = Resource.Error(tokenResult.message ?: "认证失败")
                }
                else -> {}
            }
        }
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            _authState.value = Resource.Loading()
            val result = gitHubRepository.getCurrentUser()
            _authState.value = result
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = null
            _isLoggedIn.value = false
        }
    }
    
    fun getAuthUrl(): String {
        return "https://github.com/login/oauth/authorize" +
                "?client_id=${BuildConfig.GITHUB_CLIENT_ID}" +
                "&redirect_uri=${BuildConfig.GITHUB_REDIRECT_URI}" +
                "&scope=repo,user"
    }
}
