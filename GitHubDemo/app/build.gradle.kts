plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

android {
    namespace = "com.student.githubdemo"
    compileSdk = 35
//    com.student.githubdemo://oauth/callback
//    Ov23li9mExqrIGRdAwOC
//    a8ae400b4376d308da1255341b388a39c6fc3580
    defaultConfig {
        applicationId = "com.student.githubdemo"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // GitHub OAuth配置
        buildConfigField("String", "GITHUB_CLIENT_ID", "\"Ov23li9mExqrIGRdAwOC\"")
        buildConfigField("String", "GITHUB_CLIENT_SECRET", "\"a8ae400b4376d308da1255341b388a39c6fc3580\"")
        buildConfigField("String", "GITHUB_REDIRECT_URI", "\"com.student.githubdemo://oauth/callback\"")
    }
    

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug") // 使用debug签名用于演示
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

hilt {
    enableAggregatingTask = false
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    
    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Image loading
    implementation(libs.coil.compose)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Accompanist
    implementation(libs.accompanist.systemuicontroller)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation("androidx.compose.ui:ui-test-manifest")
    
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// 自定义任务：生成所有APK
tasks.register("buildAllApks") {
    group = "build"
    description = "Build both debug and release APKs"
    dependsOn("assembleDebug", "assembleRelease")
    
    doLast {
        println("✅ All APKs built successfully!")
        println("Debug APK: app/build/outputs/apk/debug/app-debug.apk")
        println("Release APK: app/build/outputs/apk/release/app-release.apk")
    }
}

// 自定义任务：运行所有测试
tasks.register("runAllTests") {
    group = "verification"
    description = "Run all unit and instrumentation tests"
    dependsOn("test", "connectedAndroidTest")
    
    doLast {
        println("✅ All tests completed!")
    }
}