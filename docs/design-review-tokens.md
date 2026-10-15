# 设计令牌审查，PR9

审查对象：open-claude-ui 的令牌（MIT，code-examples/cds-theme.css）与 Mixture 前端的实际落地（colors.xml、themes.xml、activity_main.xml、MainActivity.kt、DesignTokens.kt）。

审查结论：颜色主值对得上，圆角、气泡、深色、边框、阴影、动效在 PR8 之前没有落地。PR9 把这些补上。

## 审查结果

### 已对上

- 陶土主色 #D97757、悬停 #CC5A36、极浅底 #FBF3EF。
- 浅色表面 0 #F5F5F3、1 #FCFCFB、2 #FFFFFF。
- 文字主 #151515、次 #525252、弱 #8A8A8A。
- 圆角数值在 DesignTokens.kt 里有：气泡 18、输入框 20、面板 10、按钮 6/8/10、对话框 16。
- 审批卡三态在 ChatState 里有：等待、运行、成功或失败。
- 思考强制打开在 ChatReducer 里有：发送时强制开，收到或停止时关。
- 输入法组合锁在 ChatReducer 里有：组合中不能发送。

### PR8 之前没落地，已在 PR9 补上

- 浅色表面 3 #FFFFFF：输入框和浮动面板的高层表面。PR8 的输入框用了表面 2。PR9 加了 light_surface_3，输入框改用它。
- 用户气泡浅蓝底 #F0F4F8：PR8 的消息是纯文本，没有气泡。PR9 加了 bubble_bg_user，浅蓝底，18dp 圆角。助手气泡透明，无边框。
- 边框色：标准边框是黑色 10% 叠白底，约 #E6E6E6。强边框是黑色 20% 叠白底，约 #CCCCCC。PR9 加了 border、border_strong，输入框和审批卡用上。
- 危险色：浅色 #EF4444，深色 #F87171。PR9 加了 fill_danger，审批卡拒绝和失败用它。
- 深色模式：深色表面 0 #0B0B0A、1 #151515、2 #1A1A19、3 #20201E、浮层 #222220，深色文字主 #F5F5F5、次 #A3A3A3、弱 #666666，深色陶土悬停 #E08264，深色危险 #F87171。PR8 没有深色资源。PR9 加了 values-night/colors.xml 和 values-night/themes.xml。
- 圆角落地：PR8 的布局全是直角。PR9 加了 dimens.xml 和五个背景 drawable：用户气泡 18dp、助手气泡透明、输入框 20dp、按钮 8dp、审批卡 10dp。
- 阴影落地：PR8 没有阴影。PR9 给输入框和审批卡加了 elevation：输入框 4dp，审批卡 2dp。
- 动效落地：PR8 没有动效。PR9 给发送和停止按钮加了按压 0.98 缩放，给消息容器开了 animateLayoutChanges，给消息文本加了 breakStrategy balanced。
- 审批卡状态显示：PR8 的审批卡只有标题和按钮，没有状态。PR9 加了状态文本：等待、运行、成功、失败，失败用危险色。

### 仍未落地

- 分段控件（SegmentedControl）：令牌有轨道 999px 全圆角。App 里还没有模型切换器 UI，暂不做。
- 骨架屏扫光、陶土呼吸灯、消息气泡微弹进入：令牌里有动画定义。App 里只做了按压缩放和布局变化动画，完整动画在后续 PR。
- 深色模式真机检查：颜色已配好，仍需在真机上看一遍浅色和深色。

## 令牌来源

颜色、圆角、边框、阴影、动效数值来自 open-claude-ui 的 cds-theme.css，MIT。Mixture 只用了令牌数值和交互规则，没有复制 Claude 的专有标识或未授权素材。MIT 声明见 THIRD_PARTY_NOTICES.md。
