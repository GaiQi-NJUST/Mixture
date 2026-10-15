# Hands complete, PR8

This note describes the changes in PR8. It completes the Hands executor, adds the adaptive icon, and adds the Gradle wrapper files.

## What changed

- MixtureAccessibilityService now keeps a node id to AccessibilityNodeInfo map from the latest buildState. Tap by node looks up the node and performs ACTION_CLICK. When the node is not clickable, it clicks the parent. Input looks up the node, checks that it is editable and not a password field, and performs ACTION_SET_TEXT with the text in a Bundle. TapAt, Swipe, Back, and Home are unchanged.
- The service still never reads a FLAG_SECURE screen. A null root is the signal. It still never fills a password field. HandsOperator blocks password input, and the executor checks isPassword again.
- The app icon is now an adaptive icon: a white M on the clay background, from the open-claude-ui Clay token. The foreground is a vector drawable. The background is the clay color. No PNG is committed. The Manifest references @mipmap/ic_launcher and @mipmap/ic_launcher_round. The minSdk is 26, so the adaptive icon covers all supported devices.
- Gradle wrapper: gradlew and gradle/wrapper/gradle-wrapper.properties are added, from the nanoMuse skeleton, Gradle 8.11.1. The gradle-wrapper.jar is not committed. Android Studio generates it, or run `gradle wrapper` once with a local Gradle install. The README states this limit plainly.

## What is not in this PR

- No real Shizuku binder binding. That comes in a later PR.
- No designed icon beyond the simple M mark. Replace it with a designed icon in a later PR.
- No Gradle wrapper jar. See above.

## Verification

The Kotlin files were compiled in this workspace with kotlinc 2.0.21 and JDK 17, against android.jar and the PR2 to PR6 module jars. The compile passed with exit 0. The resources compile with aapt2 compile and link, exit 0, including the adaptive icon. The classes compile to dex with d8, exit 0. The APK is zipaligned and signed with a debug key generated in this VM, and apksigner verify reports v1, v2, and v3 true. The full Gradle build in Android Studio and the true device regression remain the canonical check.
