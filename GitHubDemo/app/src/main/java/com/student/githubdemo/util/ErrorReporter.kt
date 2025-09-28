package com.student.githubdemo.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

object ErrorReporter {
    
    private const val TAG = "ErrorReporter"
    
    /**
     * 报告异常
     */
    fun reportException(
        context: Context,
        throwable: Throwable,
        tag: String = "UnhandledException",
        additionalInfo: Map<String, String> = emptyMap()
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val errorReport = buildErrorReport(context, throwable, tag, additionalInfo)
                Log.e(TAG, errorReport)
                
                // 在生产环境中，这里可以发送到崩溃报告服务
                // 例如：Firebase Crashlytics, Bugsnag 等
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to report exception", e)
            }
        }
    }
    
    /**
     * 报告网络错误
     */
    fun reportNetworkError(
        url: String,
        method: String,
        responseCode: Int,
        error: Throwable?,
        duration: Long = 0
    ) {
        val errorInfo = mapOf(
            "url" to url,
            "method" to method,
            "responseCode" to responseCode.toString(),
            "duration" to "${duration}ms"
        )
        
        Log.e(TAG, """
            Network Error:
            URL: $url
            Method: $method
            Response Code: $responseCode
            Duration: ${duration}ms
            Error: ${error?.message ?: "Unknown"}
        """.trimIndent())
        
        error?.let { 
            Log.e(TAG, "Network error stack trace:", it)
        }
    }
    
    /**
     * 报告UI错误
     */
    fun reportUIError(
        screenName: String,
        action: String,
        error: Throwable,
        userInfo: Map<String, String> = emptyMap()
    ) {
        val errorInfo = mutableMapOf(
            "screen" to screenName,
            "action" to action,
            "errorType" to error.javaClass.simpleName
        )
        errorInfo.putAll(userInfo)
        
        Log.e(TAG, """
            UI Error:
            Screen: $screenName
            Action: $action
            Error: ${error.message}
        """.trimIndent(), error)
    }
    
    /**
     * 报告数据错误
     */
    fun reportDataError(
        operation: String,
        dataSource: String,
        error: Throwable,
        metadata: Map<String, Any> = emptyMap()
    ) {
        Log.e(TAG, """
            Data Error:
            Operation: $operation
            Data Source: $dataSource
            Error: ${error.message}
            Metadata: $metadata
        """.trimIndent(), error)
    }
    
    /**
     * 构建错误报告
     */
    private fun buildErrorReport(
        context: Context,
        throwable: Throwable,
        tag: String,
        additionalInfo: Map<String, String>
    ): String {
        return buildString {
            appendLine("=== ERROR REPORT ===")
            appendLine("Tag: $tag")
            appendLine("Timestamp: ${getCurrentTimestamp()}")
            appendLine()
            
            // 应用信息
            appendLine("=== APP INFO ===")
            appendLine(getAppInfo(context))
            appendLine()
            
            // 设备信息
            appendLine("=== DEVICE INFO ===")
            appendLine(getDeviceInfo())
            appendLine()
            
            // 异常信息
            appendLine("=== EXCEPTION ===")
            appendLine("Exception Type: ${throwable.javaClass.name}")
            appendLine("Message: ${throwable.message}")
            appendLine("Stack Trace:")
            appendLine(getStackTrace(throwable))
            appendLine()
            
            // 附加信息
            if (additionalInfo.isNotEmpty()) {
                appendLine("=== ADDITIONAL INFO ===")
                additionalInfo.forEach { (key, value) ->
                    appendLine("$key: $value")
                }
                appendLine()
            }
            
            // 内存信息
            appendLine("=== MEMORY INFO ===")
            appendLine(getMemoryInfo())
            
            appendLine("=== END REPORT ===")
        }
    }
    
    /**
     * 获取应用信息
     */
    private fun getAppInfo(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            buildString {
                appendLine("Package Name: ${packageInfo.packageName}")
                appendLine("Version Name: ${packageInfo.versionName}")
                appendLine("Version Code: ${packageInfo.longVersionCode}")
                appendLine("First Install Time: ${Date(packageInfo.firstInstallTime)}")
                appendLine("Last Update Time: ${Date(packageInfo.lastUpdateTime)}")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            "Unable to get app info: ${e.message}"
        }
    }
    
    /**
     * 获取设备信息
     */
    private fun getDeviceInfo(): String {
        return buildString {
            appendLine("Device: ${Build.DEVICE}")
            appendLine("Model: ${Build.MODEL}")
            appendLine("Manufacturer: ${Build.MANUFACTURER}")
            appendLine("Brand: ${Build.BRAND}")
            appendLine("Product: ${Build.PRODUCT}")
            appendLine("Board: ${Build.BOARD}")
            appendLine("Hardware: ${Build.HARDWARE}")
            appendLine("Android Version: ${Build.VERSION.RELEASE}")
            appendLine("API Level: ${Build.VERSION.SDK_INT}")
            appendLine("Build ID: ${Build.ID}")
            appendLine("Build Type: ${Build.TYPE}")
            appendLine("Build Tags: ${Build.TAGS}")
            appendLine("Build Time: ${Date(Build.TIME)}")
            appendLine("Fingerprint: ${Build.FINGERPRINT}")
        }
    }
    
    /**
     * 获取内存信息
     */
    private fun getMemoryInfo(): String {
        val runtime = Runtime.getRuntime()
        return buildString {
            appendLine("Max Memory: ${formatBytes(runtime.maxMemory())}")
            appendLine("Total Memory: ${formatBytes(runtime.totalMemory())}")
            appendLine("Free Memory: ${formatBytes(runtime.freeMemory())}")
            appendLine("Used Memory: ${formatBytes(runtime.totalMemory() - runtime.freeMemory())}")
        }
    }
    
    /**
     * 获取堆栈跟踪
     */
    private fun getStackTrace(throwable: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        return stringWriter.toString()
    }
    
    /**
     * 获取当前时间戳
     */
    private fun getCurrentTimestamp(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return formatter.format(Date())
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
     * 报告应用崩溃
     */
    fun setupCrashHandler(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                reportException(
                    context = context,
                    throwable = throwable,
                    tag = "AppCrash",
                    additionalInfo = mapOf(
                        "thread" to thread.name,
                        "threadId" to thread.id.toString()
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to report crash", e)
            } finally {
                // 调用默认的异常处理器
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }
    
    /**
     * 记录用户操作
     */
    fun logUserAction(
        screen: String,
        action: String,
        parameters: Map<String, String> = emptyMap()
    ) {
        Log.i(TAG, """
            User Action:
            Screen: $screen
            Action: $action
            Parameters: $parameters
            Timestamp: ${getCurrentTimestamp()}
        """.trimIndent())
    }
    
    /**
     * 记录性能问题
     */
    fun reportPerformanceIssue(
        operation: String,
        duration: Long,
        threshold: Long,
        additionalData: Map<String, Any> = emptyMap()
    ) {
        if (duration > threshold) {
            Log.w(TAG, """
                Performance Issue:
                Operation: $operation
                Duration: ${duration}ms
                Threshold: ${threshold}ms
                Exceeded by: ${duration - threshold}ms
                Additional Data: $additionalData
            """.trimIndent())
        }
    }
}
