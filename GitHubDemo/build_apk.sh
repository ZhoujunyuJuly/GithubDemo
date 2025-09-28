#!/bin/bash

# GitHub Demo App 构建脚本
# 此脚本用于自动构建和生成APK文件

echo "========================================="
echo "GitHub Demo App 构建脚本"
echo "========================================="

# 检查是否存在gradlew
if [ ! -f "./gradlew" ]; then
    echo "❌ 错误: 未找到gradlew文件，请确保在项目根目录下运行此脚本"
    exit 1
fi

# 清理项目
echo "🧹 清理项目..."
./gradlew clean

if [ $? -ne 0 ]; then
    echo "❌ 清理失败"
    exit 1
fi

echo "✅ 清理完成"

# 构建Debug APK
echo "🔨 构建Debug APK..."
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    echo "❌ Debug构建失败"
    exit 1
fi

echo "✅ Debug APK构建完成"

# 构建Release APK
echo "🔨 构建Release APK..."
./gradlew assembleRelease

if [ $? -ne 0 ]; then
    echo "❌ Release构建失败"
    exit 1
fi

echo "✅ Release APK构建完成"

# 显示APK位置
echo ""
echo "========================================="
echo "构建完成！APK文件位置："
echo "========================================="

DEBUG_APK="app/build/outputs/apk/debug/app-debug.apk"
RELEASE_APK="app/build/outputs/apk/release/app-release.apk"

if [ -f "$DEBUG_APK" ]; then
    echo "📱 Debug APK: $DEBUG_APK"
    echo "   文件大小: $(du -h "$DEBUG_APK" | cut -f1)"
fi

if [ -f "$RELEASE_APK" ]; then
    echo "📱 Release APK: $RELEASE_APK"
    echo "   文件大小: $(du -h "$RELEASE_APK" | cut -f1)"
fi

echo ""
echo "🎉 构建成功完成！"
echo ""
echo "安装说明："
echo "1. 将APK文件传输到Android设备"
echo "2. 在设备上启用'未知来源'应用安装"
echo "3. 点击APK文件进行安装"
echo ""
echo "注意：Release APK已启用代码混淆和资源压缩以减小文件大小"