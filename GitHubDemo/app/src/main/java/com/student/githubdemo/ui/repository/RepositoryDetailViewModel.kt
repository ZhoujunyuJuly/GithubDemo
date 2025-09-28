package com.student.githubdemo.ui.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.githubdemo.data.model.Issue
import com.student.githubdemo.data.model.Repository
import com.student.githubdemo.data.repository.GitHubRepository
import com.student.githubdemo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryDetailViewModel @Inject constructor(
    private val gitHubRepository: GitHubRepository
) : ViewModel() {
    
    private val _repository = MutableStateFlow<Resource<Repository>?>(null)
    val repository: StateFlow<Resource<Repository>?> = _repository.asStateFlow()
    
    private val _issues = MutableStateFlow<Resource<List<Issue>>>(Resource.Loading())
    val issues: StateFlow<Resource<List<Issue>>> = _issues.asStateFlow()
    
    private val _createIssueState = MutableStateFlow<Resource<Issue>?>(null)
    val createIssueState: StateFlow<Resource<Issue>?> = _createIssueState.asStateFlow()
    
    fun loadRepository(owner: String, repo: String) {
        viewModelScope.launch {
            _repository.value = Resource.Loading()
            val result = gitHubRepository.getRepository(owner, repo)
            _repository.value = result
            
            // 同时加载Issues
            loadIssues(owner, repo)
        }
    }
    
    fun loadIssues(owner: String, repo: String) {
        viewModelScope.launch {
            _issues.value = Resource.Loading()
            val result = gitHubRepository.getRepositoryIssues(owner, repo)
            _issues.value = result
        }
    }
    
    fun createIssue(owner: String, repo: String, title: String, body: String?) {
        viewModelScope.launch {
            _createIssueState.value = Resource.Loading()
            val result = gitHubRepository.createIssue(owner, repo, title, body)
            _createIssueState.value = result
            
            // 如果创建成功，重新加载Issues
            if (result is Resource.Success) {
                loadIssues(owner, repo)
            }
        }
    }
    
    fun clearCreateIssueState() {
        _createIssueState.value = null
    }
}
