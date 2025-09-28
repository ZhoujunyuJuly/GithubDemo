package com.student.githubdemo.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Home : Screen("home")
    
    object Login : Screen("login")
    
    object OAuthWebView : Screen("oauth_webview")
    
    object Profile : Screen("profile")
    
    object RepositoryDetail : Screen(
        route = "repository_detail/{owner}/{repo}",
        arguments = listOf(
            navArgument("owner") { type = NavType.StringType },
            navArgument("repo") { type = NavType.StringType }
        )
    ) {
        fun createRoute(owner: String, repo: String): String {
            return "repository_detail/$owner/$repo"
        }
    }
}
