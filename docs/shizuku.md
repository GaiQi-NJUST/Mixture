# Shizuku, PR4

This note describes the real code in mixture-shizuku. It implements Phase 4 of the plan, the Shizuku adapter, for the pure logic part. The Android Shizuku binder comes in a later PR.

## What the code does

- ShizukuState holds the availability and permission state: NOT_INSTALLED, NOT_RUNNING, PERMISSION_DENIED, READY, UNKNOWN. The degradation message always states that Termux and Hands still work when Shizuku is off.
- ShizukuCommand holds the executable, the arguments, the task id, and the label. It validates the executable, the arguments, and the task id. It reports read only for the safe list. It blocks install, uninstall, reboot, su, sudo, rm, push, and pull as subcommands.
- ShizukuAdapter checks state, permission, read only, and approval, then sends through the bridge and tracks running tasks. FakeShizukuBridge and FakeShizukuApprover are for tests.

## Read only list

- pm list, pm dump, pm path.
- dumpsys, any args.
- settings get.
- cmd package list.
- am stack list.

Everything else needs approval each time.

## What is not in this PR

- No Shizuku SDK binding. The Android code that binds to Shizuku and checks the real permission comes in a later PR.
- No adb shell execution on this machine. The tests use a fake bridge.
- No UI. The Shizuku status row in the app comes with the app UI PR.

## Verification

The unit tests cover read only detection, blocked subcommands, ready read only run, not running block, permission denied block, write approval, write after approval, forbidden executable, shell metachar, and the degradation message. They run on the JVM with no phone and no network.

Verified in this workspace: kotlinc 2.0.21 with JDK 17, JUnit 4.13.2. Main sources compile with exit 0. Test sources compile with exit 0. JUnit run reports OK. The Gradle build in CI and in Android Studio remains the canonical check.
