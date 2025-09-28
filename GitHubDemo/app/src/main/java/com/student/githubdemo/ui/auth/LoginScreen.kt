package com.student.githubdemo.ui.auth
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.student.githubdemo.R
import com.student.githubdemo.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onStartOAuth: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // 监听登录状态变化
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("登录 GitHub") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // GitHub Logo (如果有的话，这里可以用占位图标)
            Icon(
                painter = painterResource(id = android.R.drawable.ic_dialog_info),
                contentDescription = "GitHub Logo",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "GitHub Demo",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "登录您的 GitHub 账户以访问个人仓库和创建 Issues",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            when (authState) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("正在登录...")
                }
                is Resource.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = (authState as Resource.Error).message ?: "登录失败",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onStartOAuth,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("重试登录")
                    }
                }
                else -> {
                    Button(
                        onClick = onStartOAuth,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("使用 GitHub 登录")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "登录后您可以：\n• 查看个人仓库\n• 创建和管理 Issues\n• 访问私有仓库",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
