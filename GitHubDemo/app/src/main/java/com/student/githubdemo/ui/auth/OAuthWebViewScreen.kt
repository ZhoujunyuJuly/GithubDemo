package com.student.githubdemo.ui.auth

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.githubdemo.BuildConfig
import com.student.githubdemo.util.Resource

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OAuthWebViewScreen(
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var isLoading by remember { mutableStateOf(true) }

    val authUrl = remember { authViewModel.getAuthUrl() }
    val redirectUri = remember { BuildConfig.GITHUB_REDIRECT_URI }
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(authState) {
            if (authState is Resource.Success) {
                onSuccess()
            } else if (authState is Resource.Error) {
                onError((authState as Resource.Error).message ?: "auth_failed")
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }

                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            val url = request?.url?.toString() ?: return false
                            if (url.startsWith(redirectUri)) {
                                val uri = Uri.parse(url)
                                val code = uri.getQueryParameter("code")
                                if (!code.isNullOrEmpty()) {
                                    // 停止继续加载并在当前页面内完成交换 token
                                    view?.stopLoading()
                                    isLoading = true
                                    authViewModel.handleOAuthCallback(code)
                                } else {
                                    val err = uri.getQueryParameter("error") ?: "unknown_error"
                                    onError(err)
                                }
                                return true
                            }
                            return false
                        }
                    }
                    loadUrl(authUrl)
                }
            },
            update = { webView ->
                if (webView.url != authUrl) {
                    webView.loadUrl(authUrl)
                }
            }
        )

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}


