package com.student.githubdemo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.githubdemo.data.model.Repository
import com.student.githubdemo.data.repository.GitHubRepository
import com.student.githubdemo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val gitHubRepository: GitHubRepository
) : ViewModel() {
    
    private val _userRepositories = MutableStateFlow<Resource<List<Repository>>>(Resource.Loading())
    val userRepositories: StateFlow<Resource<List<Repository>>> = _userRepositories.asStateFlow()
    
    fun loadUserRepositories() {
        viewModelScope.launch {
            _userRepositories.value = Resource.Loading()
            val result = gitHubRepository.getUserRepositories()
            _userRepositories.value = result
        }
    }
    
    fun refreshRepositories() {
        loadUserRepositories()
    }
}
