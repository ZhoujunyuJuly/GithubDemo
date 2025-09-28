package com.student.githubdemo.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.student.githubdemo.ui.theme.GitHubDemoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysCorrectTitle() {
        composeTestRule.setContent {
            GitHubDemoTheme {
                HomeScreen(
                    onRepositoryClick = { _, _ -> },
                    onLoginClick = { },
                    onProfileClick = { }
                )
            }
        }

        // 验证标题显示
        composeTestRule
            .onNodeWithText("GitHub Demo")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysSearchField() {
        composeTestRule.setContent {
            GitHubDemoTheme {
                HomeScreen(
                    onRepositoryClick = { _, _ -> },
                    onLoginClick = { },
                    onProfileClick = { }
                )
            }
        }

        // 验证搜索框显示
        composeTestRule
            .onNodeWithText("搜索仓库")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysLoginButton() {
        composeTestRule.setContent {
            GitHubDemoTheme {
                HomeScreen(
                    onRepositoryClick = { _, _ -> },
                    onLoginClick = { },
                    onProfileClick = { }
                )
            }
        }

        // 验证登录按钮显示
        composeTestRule
            .onNodeWithText("登录")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysHotRepositoriesTitle() {
        composeTestRule.setContent {
            GitHubDemoTheme {
                HomeScreen(
                    onRepositoryClick = { _, _ -> },
                    onLoginClick = { },
                    onProfileClick = { }
                )
            }
        }

        // 验证热门仓库标题显示
        composeTestRule
            .onNodeWithText("热门仓库")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_searchFieldIsClickable() {
        composeTestRule.setContent {
            GitHubDemoTheme {
                HomeScreen(
                    onRepositoryClick = { _, _ -> },
                    onLoginClick = { },
                    onProfileClick = { }
                )
            }
        }

        // 验证搜索框可以点击
        composeTestRule
            .onNodeWithText("搜索仓库")
            .assertIsDisplayed()
            .performClick()
    }
}
