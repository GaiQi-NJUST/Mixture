# Mixture

Mixture 是跑在一台安卓手机上的个人 Agent。它基于 nanoMuse，许可证为 GPL-3.0-or-later。

## 它能做什么

手机本地运行 Agent，对话、命令、文件和手机操作都在手机上完成，不需要云电脑、云手机或中继服务器。

- Termux：跑命令和管理项目工作区。
- 无障碍：读屏幕语义、截图、点按、滑动和输入。
- Shizuku：只在用户授权范围内用系统 API 和 ADB shell，不当作 root。
- 模型可插拔。当前用 OpenRouter + 你自己的 key，见 docs/model-api-openrouter.md。
- 界面跟随 docs/design-tokens-open-claude-ui.md 里的令牌。

## 现在到哪一步

- PR1：许可证、架构、模型 API、设计令牌基线，已合并。
- PR2：Termux 工作区与 RUN_COMMAND 桥接。
- PR3：Hands 无障碍操作器。
- PR4：Shizuku 适配器。
- PR5：界面状态与审批卡。
- PR6：模型客户端、任务日志、记忆。
- PR7：安卓 App 壳。
- PR8：Hands 按节点点按与输入接线、自适应图标、Gradle wrapper 文件。
- PR9：README 改中文，设计令牌审查修复。

PR2 到 PR9 是一条链子，请按顺序合并。每个 PR 都有可编译的代码和单元测试。

## 安装调试版

调试版安装包只用于测试，不是正式发布版。安装前先卸掉旧版。

1. 打开手机设置，允许安装未知应用。
2. 安装调试版 APK。
3. 打开 App。第一次没有钥匙时，它会提示去设置里加 OpenRouter 的钥匙，不会在聊天里让你贴。
4. 按 android-app/README.md 里的步骤开权限：无障碍、Termux、Shizuku（可选）。

## 安全规则

- 登录、验证码、CAPTCHA、密码、PIN、卡号由你亲自输入，Agent 不填。
- 支付、转账、发送、删除、发布每次都要你单独点同意，上一次的同意不算下一次的。
- 遇到 FLAG_SECURE 屏幕不绕过，只报告读不到，交还给你。
- Shizuku 不可用时，Termux 和无障碍继续工作，Shizuku 功能关掉并说清楚。
- 钥匙放 Android Keystore，不进聊天、日志、仓库或明文配置。

## 许可证

GPL-3.0-or-later。见 LICENSE、NOTICE、THIRD_PARTY_NOTICES.md。

## 来源

上游 nanoMuse，commit d808bb2。见 docs/fork-notes.md。
界面令牌来源 open-claude-ui，MIT。见 docs/design-tokens-open-claude-ui.md。
