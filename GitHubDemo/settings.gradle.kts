pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven ( "https://maven.aliyun.com/repository/public" )
        maven ( "https://maven.aliyun.com/repositories/jcenter" )
        maven ( "https://maven.aliyun.com/repositories/google" )
        maven ( "https://maven.aliyun.com/repository/central" )
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ( "https://maven.aliyun.com/repository/public" )
        maven ( "https://maven.aliyun.com/repositories/jcenter" )
        maven ( "https://maven.aliyun.com/repositories/google" )
        maven ( "https://maven.aliyun.com/repository/central" )
    }
}

rootProject.name = "GitHubDemo"
include(":app")
 