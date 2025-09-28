package com.student.githubdemo.ui.home

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
class HomeViewModel @Inject constructor(
    private val gitHubRepository: GitHubRepository
) : ViewModel() {
    
    private val _trendingRepositories = MutableStateFlow<Resource<List<Repository>>>(Resource.Loading())
    val trendingRepositories: StateFlow<Resource<List<Repository>>> = _trendingRepositories.asStateFlow()
    
    private val _searchResults = MutableStateFlow<Resource<List<Repository>>>(Resource.Loading())
    val searchResults: StateFlow<Resource<List<Repository>>> = _searchResults.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedLanguage = MutableStateFlow("All")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()
    
    init {
        loadTrendingRepositories()
    }
    
    fun loadTrendingRepositories() {
        viewModelScope.launch {
            _trendingRepositories.value = Resource.Loading()
            val result = gitHubRepository.searchRepositories(
                query = "stars:>1000",
                sort = "stars",
                order = "desc"
            )
            when (result) {
                is Resource.Success -> {
                    _trendingRepositories.value = Resource.Success(result.data?.items ?: emptyList())
                }
                is Resource.Error -> {
                    _trendingRepositories.value = Resource.Error(result.message ?: "加载失败")
                }
                is Resource.Loading -> {
                    _trendingRepositories.value = Resource.Loading()
                }
            }
        }
    }
    
    fun searchRepositories(query: String, language: String = "All") {
        viewModelScope.launch {
            _searchResults.value = Resource.Loading()
            _searchQuery.value = query
            _selectedLanguage.value = language
            
            val searchQuery = buildString {
                append(query)
                if (language != "All") {
                    append(" language:$language")
                }
            }
            
            val result = gitHubRepository.searchRepositories(
                query = searchQuery,
                sort = "stars",
                order = "desc"
            )
            when (result) {
                is Resource.Success -> {
                    _searchResults.value = Resource.Success(result.data?.items ?: emptyList())
                }
                is Resource.Error -> {
                    _searchResults.value = Resource.Error(result.message ?: "搜索失败")
                }
                is Resource.Loading -> {
                    _searchResults.value = Resource.Loading()
                }
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = Resource.Loading()
    }
}
