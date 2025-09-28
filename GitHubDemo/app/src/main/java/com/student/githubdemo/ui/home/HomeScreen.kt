package com.student.githubdemo.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.student.githubdemo.data.model.Repository
import com.student.githubdemo.ui.auth.AuthViewModel
import com.student.githubdemo.ui.components.RepositoryItem
import com.student.githubdemo.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRepositoryClick: (String, String) -> Unit,
    onLoginClick: () -> Unit,
    onProfileClick: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val trendingRepositories by homeViewModel.trendingRepositories.collectAsStateWithLifecycle()
    val searchResults by homeViewModel.searchResults.collectAsStateWithLifecycle()
    val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedLanguage by homeViewModel.selectedLanguage.collectAsStateWithLifecycle()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val languages = listOf(
        "All", "Kotlin", "Java", "Python", "JavaScript", "TypeScript", 
        "Swift", "Go", "Rust", "C++", "C#", "PHP", "Ruby"
    )
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("GitHub Demo") },
            actions = {
                if (isLoggedIn) {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                } else {
                    TextButton(onClick = onLoginClick) {
                        Text("登录")
                    }
                }
            }
        )
        
        // 搜索栏
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("搜索仓库") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = {
                                searchText = ""
                                isSearching = false
                                homeViewModel.clearSearch()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchText.isNotEmpty()) {
                                isSearching = true
                                homeViewModel.searchRepositories(searchText, selectedLanguage)
                                keyboardController?.hide()
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 语言筛选
                Text(
                    text = "编程语言:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 使用FlowRow布局显示语言筛选器
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 第一行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.take(4).forEach { language ->
                            FilterChip(
                                onClick = {
                                    if (searchText.isNotEmpty()) {
                                        isSearching = true
                                        homeViewModel.searchRepositories(searchText, language)
                                    }
                                },
                                label = { Text(language) },
                                selected = selectedLanguage == language
                            )
                        }
                    }
                    
                    // 第二行
                    if (languages.size > 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            languages.drop(4).take(4).forEach { language ->
                                FilterChip(
                                    onClick = {
                                        if (searchText.isNotEmpty()) {
                                            isSearching = true
                                            homeViewModel.searchRepositories(searchText, language)
                                        }
                                    },
                                    label = { Text(language) },
                                    selected = selectedLanguage == language
                                )
                            }
                        }
                    }
                    
                    // 第三行
                    if (languages.size > 8) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            languages.drop(8).forEach { language ->
                                FilterChip(
                                    onClick = {
                                        if (searchText.isNotEmpty()) {
                                            isSearching = true
                                            homeViewModel.searchRepositories(searchText, language)
                                        }
                                    },
                                    label = { Text(language) },
                                    selected = selectedLanguage == language
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // 内容区域
        when {
            isSearching -> {
                // 搜索结果
                Text(
                    text = "搜索结果",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                when (searchResults) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is Resource.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(searchResults.data ?: emptyList()) { repository ->
                                RepositoryItem(
                                    repository = repository,
                                    onClick = {
                                        onRepositoryClick(repository.owner.login, repository.name)
                                    }
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = searchResults.message ?: "搜索失败",
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        homeViewModel.searchRepositories(searchText, selectedLanguage)
                                    }
                                ) {
                                    Text("重试")
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                // 热门仓库
                Text(
                    text = "热门仓库",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                when (trendingRepositories) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is Resource.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(trendingRepositories.data ?: emptyList()) { repository ->
                                RepositoryItem(
                                    repository = repository,
                                    onClick = {
                                        onRepositoryClick(repository.owner.login, repository.name)
                                    }
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = trendingRepositories.message ?: "加载失败",
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { homeViewModel.loadTrendingRepositories() }) {
                                    Text("重试")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
