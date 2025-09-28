package com.student.githubdemo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.student.githubdemo.ui.auth.LoginScreen
import com.student.githubdemo.ui.auth.OAuthWebViewScreen
import com.student.githubdemo.ui.home.HomeScreen
import com.student.githubdemo.ui.profile.ProfileScreen
import com.student.githubdemo.ui.repository.RepositoryDetailScreen

@Composable
fun GitHubNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onRepositoryClick = { owner, repo ->
                    navController.navigate(Screen.RepositoryDetail.createRoute(owner, repo))
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onStartOAuth = {
                    navController.navigate(Screen.OAuthWebView.route)
                }
            )
        }
        
        composable(Screen.OAuthWebView.route) {
            OAuthWebViewScreen(
                onSuccess = {
                    navController.popBackStack()
                },
                onError = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onRepositoryClick = { owner, repo ->
                    navController.navigate(Screen.RepositoryDetail.createRoute(owner, repo))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.RepositoryDetail.route,
            arguments = Screen.RepositoryDetail.arguments
        ) { backStackEntry ->
            val owner = backStackEntry.arguments?.getString("owner") ?: ""
            val repo = backStackEntry.arguments?.getString("repo") ?: ""
            
            RepositoryDetailScreen(
                owner = owner,
                repo = repo,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
