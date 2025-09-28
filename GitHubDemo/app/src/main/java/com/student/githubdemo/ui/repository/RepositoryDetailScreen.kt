package com.student.githubdemo.ui.repository

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.student.githubdemo.data.model.Issue
import com.student.githubdemo.data.model.Repository
import com.student.githubdemo.ui.auth.AuthViewModel
import com.student.githubdemo.util.Resource
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryDetailScreen(
    owner: String,
    repo: String,
    onBackClick: () -> Unit,
    repositoryDetailViewModel: RepositoryDetailViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val repository by repositoryDetailViewModel.repository.collectAsStateWithLifecycle()
    val issues by repositoryDetailViewModel.issues.collectAsStateWithLifecycle()
    val createIssueState by repositoryDetailViewModel.createIssueState.collectAsStateWithLifecycle()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    
    var showCreateIssueDialog by remember { mutableStateOf(false) }
    var issueTitle by remember { mutableStateOf("") }
    var issueBody by remember { mutableStateOf("") }
    
    val uriHandler = LocalUriHandler.current
    
    LaunchedEffect(owner, repo) {
        repositoryDetailViewModel.loadRepository(owner, repo)
    }
    
    // 处理创建Issue的结果
    LaunchedEffect(createIssueState) {
        when (createIssueState) {
            is Resource.Success -> {
                showCreateIssueDialog = false
                issueTitle = ""
                issueBody = ""
                repositoryDetailViewModel.clearCreateIssueState()
            }
            else -> {}
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("仓库详情") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                // 在浏览器中打开
                val currentRepository = repository
                if (currentRepository is Resource.Success) {
                    IconButton(
                        onClick = {
                            currentRepository.data?.let { repo ->
                                uriHandler.openUri(repo.htmlUrl)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Open in Browser")
                    }
                }
            }
        )
        
        when (val currentRepository = repository) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                val repo = currentRepository.data ?: return
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 仓库信息卡片
                    item {
                        RepositoryInfoCard(repository = repo)
                    }
                    
                    // Issues部分
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Issues",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // 创建Issue按钮（仅登录用户且是自己的仓库）
                            val currentAuthState = authState
                            if (isLoggedIn && currentAuthState is Resource.Success) {
                                val currentUser = currentAuthState.data
                                if (currentUser?.login == repo.owner.login) {
                                    Button(
                                        onClick = { showCreateIssueDialog = true }
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("创建 Issue")
                                    }
                                }
                            }
                        }
                    }
                    
                    // Issues列表
                    when (issues) {
                        is Resource.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        is Resource.Success -> {
                            val issuesList = issues.data ?: emptyList()
                            if (issuesList.isEmpty()) {
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
                                                text = "暂无 Issues",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(issuesList) { issue ->
                                    IssueItem(
                                        issue = issue,
                                        onClick = {
                                            uriHandler.openUri(issue.htmlUrl)
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
                                            text = issues.message ?: "加载 Issues 失败",
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                repositoryDetailViewModel.loadIssues(owner, repo.toString())
                                            }
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
                            text = currentRepository.message ?: "加载仓库信息失败",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                repositoryDetailViewModel.loadRepository(owner, repo)
                            }
                        ) {
                            Text("重试")
                        }
                    }
                }
            }
            null -> {
                // 初始状态
            }
        }
    }
    
    // 创建Issue对话框
    if (showCreateIssueDialog) {
        AlertDialog(
            onDismissRequest = { showCreateIssueDialog = false },
            title = { Text("创建 Issue") },
            text = {
                Column {
                    OutlinedTextField(
                        value = issueTitle,
                        onValueChange = { issueTitle = it },
                        label = { Text("标题") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = issueBody,
                        onValueChange = { issueBody = it },
                        label = { Text("描述（可选）") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                    
                    // 显示创建状态
                    val currentCreateIssueState = createIssueState
                    when (currentCreateIssueState) {
                        is Resource.Loading -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("创建中...")
                            }
                        }
                        is Resource.Error -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentCreateIssueState.message ?: "创建失败",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        else -> {}
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (issueTitle.isNotEmpty()) {
                            repositoryDetailViewModel.createIssue(
                                owner, repo, issueTitle,
                                if (issueBody.isNotEmpty()) issueBody else null
                            )
                        }
                    },
                    enabled = issueTitle.isNotEmpty() && createIssueState !is Resource.Loading
                ) {
                    Text("创建")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateIssueDialog = false
                        repositoryDetailViewModel.clearCreateIssueState()
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun RepositoryInfoCard(repository: Repository) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 仓库名称和所有者
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = repository.owner.avatarUrl,
                    contentDescription = "Owner Avatar",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = repository.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = repository.owner.login,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 描述
            if (!repository.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = repository.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 统计信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StatChip(
                    icon = Icons.Default.Star,
                    count = repository.stargazersCount,
                    label = "Stars"
                )
                
                StatChip(
                    icon = Icons.Default.Share,
                    count = repository.forksCount,
                    label = "Forks"
                )
                
                StatChip(
                    icon = Icons.Default.Warning,
                    count = repository.openIssuesCount,
                    label = "Issues"
                )
            }
            
            if (!repository.language.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Language",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = repository.language,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String
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
            text = formatNumber(count),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IssueItem(
    issue: Issue,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = issue.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "#${issue.number} • ${formatDate(issue.createdAt)} • ${issue.user.login}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 状态标签
                val stateColor = if (issue.state == "open") {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = stateColor.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = issue.state.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = stateColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (!issue.body.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = issue.body,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> "${number / 1000000}M"
        number >= 1000 -> "${number / 1000}k"
        else -> number.toString()
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString.substring(0, 10) // 简单截取日期部分
    }
}
