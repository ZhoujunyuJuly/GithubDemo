package com.student.githubdemo.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.student.githubdemo.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OAuthCallbackActivity : ComponentActivity() {
    
    private val authViewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val uri = intent.data
        if (uri != null && uri.scheme == "com.student.githubdemo") {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                authViewModel.handleOAuthCallback(code)
            }
        }
        
        // 观察认证状态
        lifecycleScope.launch {
            authViewModel.authState.collect { authState ->
                if (authState != null) {
                    // 认证完成，返回主界面
                    val intent = Intent(this@OAuthCallbackActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
