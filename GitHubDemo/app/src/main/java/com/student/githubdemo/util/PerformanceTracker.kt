package com.student.githubdemo.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

object PerformanceTracker {
    
    const val TAG = "PerformanceTracker"
    
    /**
     * 测量代码块执行时间
     */
    inline fun <T> measureTime(
        operation: String,
        block: () -> T
    ): T {
        var result: T
        val timeMillis = measureTimeMillis {
            result = block()
        }
        Log.d(TAG, "$operation took ${timeMillis}ms")
        return result
    }
    
    /**
     * 测量挂起函数执行时间
     */
    suspend inline fun <T> measureSuspendTime(
        operation: String,
        crossinline block: suspend () -> T
    ): T {
        var result: T
        val timeMillis = measureTimeMillis {
            result = block()
        }
        Log.d(TAG, "$operation took ${timeMillis}ms")
        return result
    }
    
    /**
     * 记录内存使用情况
     */
    fun logMemoryUsage(tag: String) {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory()
        
        Log.d(TAG, """
            Memory Usage [$tag]:
            Used: ${formatBytes(usedMemory)}
            Free: ${formatBytes(freeMemory)}
            Total: ${formatBytes(totalMemory)}
            Max: ${formatBytes(maxMemory)}
            Usage: ${(usedMemory * 100 / maxMemory)}%
        """.trimIndent())
    }
    
    /**
     * 异步记录性能指标
     */
    fun logPerformanceAsync(
        operation: String,
        duration: Long,
        success: Boolean,
        additionalData: Map<String, Any> = emptyMap()
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val logData = buildString {
                    append("Performance Log: $operation\n")
                    append("Duration: ${duration}ms\n")
                    append("Success: $success\n")
                    additionalData.forEach { (key, value) ->
                        append("$key: $value\n")
                    }
                }
                Log.i(TAG, logData)
            }
        }
    }
    
    /**
     * 网络请求性能监控
     */
    fun logNetworkRequest(
        url: String,
        method: String,
        responseCode: Int,
        duration: Long,
        responseSize: Long = 0
    ) {
        val status = when (responseCode) {
            in 200..299 -> "SUCCESS"
            in 400..499 -> "CLIENT_ERROR"
            in 500..599 -> "SERVER_ERROR"
            else -> "UNKNOWN"
        }
        
        Log.d(TAG, """
            Network Request:
            URL: $url
            Method: $method
            Status: $status ($responseCode)
            Duration: ${duration}ms
            Size: ${formatBytes(responseSize)}
        """.trimIndent())
    }
    
    /**
     * UI 渲染性能监控
     */
    fun logUIRender(screenName: String, renderTime: Long) {
        val performance = when {
            renderTime < 16 -> "EXCELLENT" // 60fps
            renderTime < 33 -> "GOOD"      // 30fps
            renderTime < 50 -> "FAIR"      // 20fps
            else -> "POOR"
        }
        
        Log.d(TAG, "UI Render [$screenName]: ${renderTime}ms ($performance)")
    }
    
    /**
     * 启动时间监控
     */
    fun logAppStartup(
        coldStart: Boolean,
        startupTime: Long,
        firstDrawTime: Long
    ) {
        val startupType = if (coldStart) "Cold Start" else "Warm Start"
        Log.i(TAG, """
            App Startup:
            Type: $startupType
            Startup Time: ${startupTime}ms
            First Draw: ${firstDrawTime}ms
            Total: ${startupTime + firstDrawTime}ms
        """.trimIndent())
    }
    
    /**
     * 格式化字节大小
     */
    private fun formatBytes(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return "%.2f %s".format(size, units[unitIndex])
    }
    
    /**
     * 数据库操作性能监控
     */
    fun logDatabaseOperation(
        operation: String,
        table: String,
        duration: Long,
        recordCount: Int = 0
    ) {
        Log.d(TAG, """
            Database Operation:
            Operation: $operation
            Table: $table
            Duration: ${duration}ms
            Records: $recordCount
        """.trimIndent())
    }
    
    /**
     * 图片加载性能监控
     */
    fun logImageLoad(
        url: String,
        cacheHit: Boolean,
        loadTime: Long,
        imageSize: Long = 0
    ) {
        val cacheStatus = if (cacheHit) "HIT" else "MISS"
        Log.d(TAG, """
            Image Load:
            URL: $url
            Cache: $cacheStatus
            Duration: ${loadTime}ms
            Size: ${formatBytes(imageSize)}
        """.trimIndent())
    }
}

/**
 * 性能监控扩展函数
 */
inline fun <T> T.trackPerformance(
    operation: String,
    block: (T) -> Unit
): T {
    PerformanceTracker.measureTime(operation) {
        block(this)
    }
    return this
}

/**
 * 挂起函数性能监控扩展
 */
suspend inline fun <T> T.trackSuspendPerformance(
    operation: String,
    crossinline block: suspend (T) -> Unit
): T {
    PerformanceTracker.measureSuspendTime(operation) {
        block(this)
    }
    return this
}
