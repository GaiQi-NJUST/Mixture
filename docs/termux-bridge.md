# Termux bridge, PR2

This note describes the real code in mixture-termux. It implements Phase 2 of the plan, the Termux connection and the fixed launcher, for the pure logic part. The Android intent sender comes in a later PR.

## What the code does

- TermuxWorkspace validates the project name and builds the project dir, the config file, the log file, and the session file. A project name must match lower case letters, digits, underscore, and hyphen, start with a letter or digit, and stay under 64 chars. A path that escapes the workspace root is rejected.
- TermuxCommand validates the executable, the arguments, the work dir, and the task id. It builds the RUN_COMMAND extras with the Termux keys. The executable must be in the allowlist. Arguments must not contain shell metacharacters. The work dir must not contain .. .
- TermuxResult holds stdout, stderr, exit code, original lengths, and internal error fields. It reports success only when there is no internal error and the exit code is zero. It reports truncation when the returned text is shorter than the original length.
- TermuxBridge is the interface for sending and stopping. FakeTermuxBridge records sent commands and stopped ids for tests.
- TermuxLauncher checks that the work dir equals the project dir, checks that Termux is available, sends through the bridge, and tracks running tasks. Stop removes the task from the running set.

## RUN_COMMAND contract

- Action: com.termux.RUN_COMMAND.
- Service: com.termux.app.RunCommandService in package com.termux.
- Mandatory extra: com.termux.RUN_COMMAND_PATH, absolute path of the command.
- Arguments: com.termux.RUN_COMMAND_ARGUMENTS, String array.
- Work dir: com.termux.RUN_COMMAND_WORKDIR, defaults to the Termux home dir in Termux. Mixture always sets it to the project dir.
- Background: com.termux.RUN_COMMAND_BACKGROUND, boolean. Mixture uses true for tasks that return results.
- Session action: com.termux.RUN_COMMAND_SESSION_ACTION, String, default 0.
- Label: com.termux.RUN_COMMAND_LABEL, String, task id when no label is given.
- Result: for background commands, the result bundle holds stdout, stderr, and exit code separately. For foreground commands, stdout holds the session transcript and stderr is null. The internal error fields hold err and errmsg. The caller checks the original lengths for truncation.

## Setup that the app must still guide

- The app must request com.termux.permission.RUN_COMMAND in AndroidManifest.xml and the user must grant it in Settings.
- The user must set allow-external-apps to true in ~/.termux/termux.properties in Termux.
- On Android 11 and later, the app must handle package visibility, or the RUN_COMMAND intent will not work.
- On Android 10 and later, background activity start limits affect foreground sessions. The app must explain the Termux notification tap when needed.
- Battery optimizations may kill Termux. The app must guide the user to exempt Termux where needed.

## What is not in this PR

- No Android intent sender. The Android app code that builds the Intent and handles the pending intent result comes in a later PR.
- No foreground notification. The notification for long tasks comes with the Android app code.
- No Termux install wizard UI. The wizard comes with the Android app code.
- No command execution on this machine. The tests use a fake bridge.

## Verification

The unit tests cover workspace paths, command validation, extras keys, launcher start and stop, and result success and truncation. They run on the JVM with no phone and no network.

Verified in this workspace: kotlinc 2.0.21 with JDK 17, JUnit 4.13.2. Main sources compile with exit 0. Test sources compile with exit 0. JUnit run reports OK, 15 tests. The same rules were also checked with a Python mirror in the working notes. The Gradle build in CI and in Android Studio remains the canonical check.
