package com.student.githubdemo

import android.app.Application
import com.student.githubdemo.util.ErrorReporter
import com.student.githubdemo.util.PerformanceTracker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GitHubDemoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 设置崩溃处理器
        ErrorReporter.setupCrashHandler(this)
        
        // 记录应用启动时间
        PerformanceTracker.logAppStartup(
            coldStart = true,
            startupTime = System.currentTimeMillis(),
            firstDrawTime = 0
        )
        
        // 记录内存使用情况
        PerformanceTracker.logMemoryUsage("Application.onCreate")
    }
}
