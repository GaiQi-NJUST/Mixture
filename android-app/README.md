# android-app

Android app shell for Mixture. This is the first runnable shell. It wires the pure logic modules from PR2 to PR6 into an Activity, an AccessibilityService, a Termux intent sender, a Shizuku binder, an OpenRouter HTTP client, and a Keystore store.

## Files

- AndroidManifest.xml: MainActivity, MixtureAccessibilityService, permissions, and package queries for Termux and Shizuku.
- MainActivity.kt: native Views, chat state, approval cards, Stop, model call.
- MixtureAccessibilityService.kt: builds ScreenState from the tree, runs Hands actions through gestures and global actions.
- TermuxIntentSender.kt: builds the RUN_COMMAND intent from TermuxCommand.
- ShizukuBinder.kt: package check and state. The real Shizuku binder binding comes in a later PR.
- OpenRouterHttpClient.kt: HttpURLConnection and org.json, with the key from KeystoreStore.
- KeystoreStore.kt: AndroidKeyStore AES key, ciphertext in SharedPreferences.
- res/layout/activity_main.xml: native layout, no Compose, no WebView.
- res/values/colors.xml: open-claude-ui tokens, MIT.
- res/xml/accessibility_service_config.xml: service config.

## Build in Android Studio

Open the repo root in Android Studio, sync Gradle, then Run on a phone with Android 8 or later. The project needs JDK 17, Android SDK 34, and AGP 8.5.2. The Gradle wrapper is not in this PR. Android Studio generates it, or copy it from the nanoMuse skeleton.

## Permissions to grant on the phone

- Accessibility: Settings, Accessibility, Mixture, on. The app never reads a FLAG_SECURE screen and never fills a password field.
- Termux: install Termux, set allow-external-apps to true in ~/.termux/termux.properties, grant Run commands in Termux environment to Mixture in Settings.
- Shizuku: optional. Install Shizuku, start it, grant permission. When Shizuku is off, Termux and Hands still work.

## Key

Add the OpenRouter key in Settings. It goes to KeystoreStore. It is never pasted in chat, never logged, and never committed.

## Limits in this PR

- Tap by node and Input are not yet wired in the service executor. They need a node id to AccessibilityNodeInfo map. TapAt, Swipe, Back, and Home work.
- ShizukuBinder only checks the package. The real binder binding comes in a later PR.
- The Manifest has no app icon in this PR, because binary PNGs are not committed here. The verified APK in the working notes used solid clay color icons generated in the VM. Replace them with a designed icon in a later PR.
- No Gradle wrapper in this PR. Android Studio generates it. The APK in the working notes was built with aapt2, d8, zipalign, and apksigner, without Gradle.
