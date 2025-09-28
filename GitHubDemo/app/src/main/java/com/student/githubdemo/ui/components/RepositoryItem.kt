package com.student.githubdemo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.student.githubdemo.data.model.Repository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryItem(
    repository: Repository,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = repository.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 描述
            if (!repository.description.isNullOrEmpty()) {
                Text(
                    text = repository.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // 统计信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 编程语言
                if (!repository.language.isNullOrEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                colors = CardDefaults.cardColors(
                                    containerColor = getLanguageColor(repository.language)
                                )
                            ) {}
                        }
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = repository.language,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // 星数
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Stars",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = formatNumber(repository.stargazersCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Fork数
                Text(
                    text = "Forks: ${formatNumber(repository.forksCount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun getLanguageColor(language: String) = when (language.lowercase()) {
    "kotlin" -> MaterialTheme.colorScheme.primary
    "java" -> MaterialTheme.colorScheme.secondary
    "python" -> MaterialTheme.colorScheme.tertiary
    "javascript" -> MaterialTheme.colorScheme.error
    "typescript" -> MaterialTheme.colorScheme.errorContainer
    else -> MaterialTheme.colorScheme.outline
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> "${number / 1000000}M"
        number >= 1000 -> "${number / 1000}k"
        else -> number.toString()
    }
}
