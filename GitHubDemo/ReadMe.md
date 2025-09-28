

## 主要功能

- **仓库搜索**: 搜索 GitHub 上的公开仓库，支持按语言筛选

- **热门仓库**: 展示 GitHub 上的热门项目

- **用户认证**: 通过 GitHub OAuth 进行安全登录

- **用户资料**: 查看用户详细信息和个人仓库

- **仓库详情**: 查看仓库详细信息和 Issues

- **Issue 管理**: 创建和查看仓库 Issues

  

### 项目要求

- **Kotlin**: 1.9.0+
- **Gradle**: 8.2+
- **JDK**: 17
- **Android SDK**: 
  - 最小版本: API 29 (Android 10)
  - 目标版本: API 35 (Android 15)
  - 编译版本: API 35



## 代码结构

```
app/src/main/java/com/student/githubdemo/
├── data/                           
│   ├── api/                  
│   │   ├── GitHubApi.kt           # GitHub API 接口
│   │   └── OAuthApi.kt            # OAuth API 接口
│   ├── local/                      # 本地数据存储
│   │   └── PreferencesManager.kt  
│   ├── model/                     
│   │   ├── GitHubUser.kt          # 用户模型
│   │   ├── Repository.kt          # 仓库模型
│   │   ├── Issue.kt               # Issue 模型
│   │   ├── OAuthToken.kt          # OAuth Token 模型
│   │   └── SearchResponse.kt      # 搜索响应模型
│   └── repository/                
│       ├── GitHubRepository.kt    # GitHub 数据仓库
│       └── AuthRepository.kt      # 认证数据仓库
├── di/                            
│   └── NetworkModule.kt           # 网络模块配置
├── navigation/                    
│   ├── GitHubNavigation.kt       # 导航图定义
│   └── Screen.kt                 # 屏幕路由定义
├── ui/                           #
│   ├── auth/                    
│   │   ├── AuthViewModel.kt      # 认证 ViewModel
│   │   ├── LoginScreen.kt        # 登录界面
│   │   ├── OAuthCallbackActivity.kt # OAuth 回调处理
│   │   └── OAuthWebViewScreen.kt # OAuth WebView 界面
│   ├── components/               
│   │   ├── LoadingScreen.kt      # 加载界面组件
│   │   └── RepositoryItem.kt     # 仓库列表项组件
│   ├── home/                     
│   │   ├── HomeScreen.kt         # 主页界面
│   │   └── HomeViewModel.kt      # 主页 ViewModel
│   ├── profile/                 
│   │   ├── ProfileScreen.kt      # 用户资料界面
│   │   └── ProfileViewModel.kt   # 用户资料 ViewModel
│   ├── repository/              
│   │   ├── RepositoryDetailScreen.kt    # 仓库详情界面
│   │   └── RepositoryDetailViewModel.kt # 仓库详情 ViewModel
│   └── theme/                   
│       ├── Color.kt             
│       ├── Theme.kt              
│       └── Type.kt             
├── util/                        
│   ├── ConnectivityObserver.kt  
│   ├── ErrorReporter.kt          
│   ├── NetworkUtils.kt          
│   ├── PerformanceTracker.kt     
│   └── Resource.kt              
├── GitHubDemoApplication.kt    
└── MainActivity.kt              
```



### 使用接口

- `GET /search/repositories` - 搜索仓库
- `GET /user` - 获取当前用户信息
- `GET /user/repos` - 获取用户仓库
- `GET /repos/{owner}/{repo}` - 获取仓库详情
- `GET /repos/{owner}/{repo}/issues` - 获取仓库 Issues
- `POST /repos/{owner}/{repo}/issues` - 创建 Issue



