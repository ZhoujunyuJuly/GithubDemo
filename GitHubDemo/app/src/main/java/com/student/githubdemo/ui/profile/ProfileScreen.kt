package com.student.githubdemo.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.student.githubdemo.ui.auth.AuthViewModel
import com.student.githubdemo.ui.components.RepositoryItem
import com.student.githubdemo.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onRepositoryClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val userRepositories by profileViewModel.userRepositories.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        profileViewModel.loadUserRepositories()
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("个人资料") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { showLogoutDialog = true }) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                }
            }
        )
        
        when (val currentAuthState = authState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                val user = currentAuthState.data ?: return
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 用户信息卡片
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = user.name ?: user.login,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = "@${user.login}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                if (!user.bio.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = user.bio,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // 统计信息
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    StatItem(
                                        icon = Icons.Default.Face,
                                        count = user.publicRepos,
                                        label = "仓库"
                                    )
                                    
                                    StatItem(
                                        icon = Icons.Default.Face,
                                        count = user.followers,
                                        label = "关注者"
                                    )
                                    
                                    StatItem(
                                        icon = Icons.Default.Person,
                                        count = user.following,
                                        label = "关注中"
                                    )
                                }
                                
                                if (!user.company.isNullOrEmpty() || !user.location.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    if (!user.company.isNullOrEmpty()) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Face,
                                                contentDescription = "Company",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = user.company,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                    
                                    if (!user.location.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.LocationOn,
                                                contentDescription = "Location",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = user.location,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // 仓库列表标题
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "我的仓库",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            
                            IconButton(onClick = { profileViewModel.refreshRepositories() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            }
                        }
                    }
                    
                    // 仓库列表
                    when (userRepositories) {
                        is Resource.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        is Resource.Success -> {
                            val repositories = userRepositories.data ?: emptyList()
                            if (repositories.isEmpty()) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "暂无仓库",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(repositories) { repository ->
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
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = userRepositories.message ?: "加载失败",
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { profileViewModel.refreshRepositories() }
                                        ) {
                                            Text("重试")
                                        }
                                    }
                                }
                            }
                        }
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
                            text = currentAuthState.message ?: "加载用户信息失败",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onBackClick) {
                            Text("返回")
                        }
                    }
                }
            }
            null -> {
                // 未登录状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("请先登录")
                }
            }
        }
    }
    
    // 退出登录确认对话框
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("退出登录") },
            text = { Text("确定要退出登录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.logout()
                        showLogoutDialog = false
                        onBackClick()
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
