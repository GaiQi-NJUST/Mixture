# Android app shell, PR7

This note describes the real code in android-app. It implements Phase 7 of the plan, the Android app shell, for the first runnable version. It wires PR2 to PR6 into an Activity and a Service.

## What the code does

- MainActivity renders the mixture-ui AppState with native Views. Send calls ChatReducer, then calls the model on a worker thread, then renders the reply. Stop calls ChatReducer.stopTask. Approval cards render as text plus Approve and Reject buttons. The model key comes from KeystoreStore. When no key is present, the app states that the key must be added in Settings, and does not call the model.
- MixtureAccessibilityService builds a ScreenState from the accessibility tree, observes it through HandsOperator, and runs Hands actions. TapAt and Swipe use dispatchGesture. Back and Home use global actions. Tap by node and Input are not yet wired. They need a node id to AccessibilityNodeInfo map, which comes in a later PR.
- TermuxIntentSender implements TermuxBridge. It validates the command, builds the RUN_COMMAND intent from the extras, and starts the Termux service. Stop returns true as a best effort. The launcher tracks the running set.
- ShizukuBinder checks the Shizuku package and reports state. When Shizuku is not installed, it reports NOT_INSTALLED. When installed but the binder is not bound, it reports NOT_RUNNING. The real binder binding comes in a later PR.
- OpenRouterHttpClient implements ModelClient with HttpURLConnection and org.json. It builds the chat completions body, sends with Authorization Bearer, and parses id, content, finish reason, and usage.
- KeystoreStore uses AndroidKeyStore. It generates an AES key, encrypts the OpenRouter key with AES GCM, and stores the ciphertext and IV in SharedPreferences. The plain key is never written to a file or a log.

## True device regression, checklist

Run these on a real arm64 phone, not an emulator, before calling the app done:

- Install the APK from Android Studio. Open the app. Send a message with no key. The app states that the key must be added, and does not call the model.
- Add the OpenRouter key in Settings. Send a message. A reply returns with provider, model, and usage.
- Grant Accessibility to Mixture. Open another app. Ask Mixture to tap a visible button by coordinates. The tap runs, then the app re-observes and confirms.
- Open a password field. Ask Mixture to fill it. The app blocks it and asks the user to type it personally.
- Open a FLAG_SECURE screen, for example a banking app that sets the flag. The service does not read it. The app reports that the screen cannot be read and hands back to the user.
- Install Termux, set allow-external-apps to true, grant Run commands to Mixture. Ask Mixture to run git status in a project. The command runs in Termux and returns stdout, stderr, and exit code.
- Stop a running task. The task stops, the status shows stopped, and no later action runs.
- Remove Shizuku or deny its permission. Termux and Hands still work. Shizuku features show as off with a clear message.

## What is not in this PR

- No Gradle wrapper. Android Studio generates it.
- No app icon in this PR. The Manifest has no icon attribute, because binary PNGs are not committed here. The verified APK in the working notes used solid clay color icons generated in the VM. Replace them with a designed icon in a later PR.
- No Tap by node or Input wiring in the service executor.
- No real Shizuku binder binding.
- No Compose, no WebView, no React. The app uses native Views only.

## Verification

Verified in this workspace: kotlinc 2.0.21 with JDK 17, against android.jar and the PR2 to PR6 module jars. The app Kotlin files compile with exit 0, with two warnings: deprecated getPackageInfo and one unchecked cast. The resources compile with aapt2 compile and link, exit 0. The classes compile to dex with d8, exit 0. The APK is zipaligned, verification successful. The APK is signed with a debug key generated in this VM, and apksigner verify reports v1, v2, and v3 true. The APK is 746K. The APK is a debug build for testing only, not a Play Store release. The full Gradle build in Android Studio and the true device regression remain the canonical check.
