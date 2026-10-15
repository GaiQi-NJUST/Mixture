# android-app

Mixture 的安卓 App 壳。它把 PR2 到 PR6 的纯逻辑模块接进一个 Activity、一个无障碍服务、一个 Termux 发送端、一个 Shizuku 检查、一个 OpenRouter 客户端和一个 Keystore 保管。

## 文件

- AndroidManifest.xml：MainActivity、无障碍服务、权限、Termux 和 Shizuku 的包查询。
- MainActivity.kt：原生 Views，聊天状态、审批卡、停止、模型调用。消息按气泡渲染，用户气泡浅蓝底，助手气泡透明。
- MixtureAccessibilityService.kt：从无障碍树建 ScreenState，跑 Hands 动作。按节点点按、输入、按坐标点按、滑动、返回、主页都已接线。
- TermuxIntentSender.kt：从 TermuxCommand 建 RUN_COMMAND intent。
- ShizukuBinder.kt：包检查和状态。真实的 Shizuku 绑定在后续 PR。
- OpenRouterHttpClient.kt：HttpURLConnection + org.json，钥匙从 KeystoreStore 来。
- KeystoreStore.kt：AndroidKeyStore AES 钥匙，密文放 SharedPreferences。
- res/layout/activity_main.xml：原生布局，不用 Compose，不用 WebView。
- res/values/colors.xml：open-claude-ui 令牌，MIT。res/values-night/colors.xml：深色模式。
- res/values/dimens.xml：圆角阶梯。
- res/drawable/：气泡、输入框、按钮、审批卡背景。
- res/xml/accessibility_service_config.xml：服务配置。

## 在 Android Studio 里编译

在 Android Studio 打开仓库根目录，同步 Gradle，然后在 Android 8 或更高版本的手机上点 Run。需要 JDK 17、Android SDK 34、AGP 8.5.2。Gradle wrapper 文件已在仓库里，gradle-wrapper.jar 由 Android Studio 生成，或本地跑一次 `gradle wrapper`。

## 在手机上开权限

- 无障碍：设置，无障碍，Mixture，开。App 不读 FLAG_SECURE 屏幕，不填密码框。
- Termux：装 Termux，把 ~/.termux/termux.properties 里的 allow-external-apps 设为 true，在设置里把“在 Termux 里跑命令”授权给 Mixture。
- Shizuku：可选。装 Shizuku，启动它，授权。Shizuku 关掉时，Termux 和无障碍继续工作。

## 钥匙

在设置里加 OpenRouter 的钥匙。它进 KeystoreStore，不在聊天里贴，不进日志，不进仓库。

## 限制

- ShizukuBinder 只做包检查。真实绑定在后续 PR。
- 图标是简单的白色 M 标记。换成设计图标在后续 PR。
- 深色模式已按令牌配好颜色，仍需在真机上看一遍。
