# Architecture

Mixture runs on one Android phone. It does not deploy a cloud desktop, a cloud phone, or a relay server. It does not require buying a cloud computer. The conversation agent, the working directory, the command processes, the files, and the phone operations run on the phone.

## Three capability lines

- Termux: Linux commands and the project workspace.
- Android Accessibility: reading interface semantics, screenshots, and gestures.
- Shizuku: system API and ADB shell capability inside the user authorized scope. It is not treated as root. Not every shell command is assumed available.

## Flow

Android App (Mixture conversation, memory, and agent core) passes a permission and risk gate, then reaches the Termux workspace bridge, the Accessibility hands, and the Shizuku adapter, then the phone files, commands, and other apps. Task status, approvals, logs, and stop signals return to the conversation interface on the phone. Files, screenshots, and sessions are not synced to the cloud by default.

## Workspace and commands

Each project has its own directory under the Termux private directory, for example ~/workspace/<project>. It holds project config, task logs, Git status, and a resumable session summary. The Android App calls a fixed workspace launcher through the Termux official RUN_COMMAND interface, with separate command arguments, working directory, and limited environment variables. User input is not concatenated into one arbitrary shell string. Termux returns standard output, error, exit code, and task ID by callback. A long task shows a foreground notification with running status, and supports viewing logs, stopping, and retrying.

On first connection, the app guides the user to install or open Termux, authorize RUN_COMMAND, and explicitly turn on allow-external-apps in Termux. When Termux is missing, permission is denied, or Android kills the process, the app shows a readable repair hint. It does not bypass authorization. Project directory isolation and a command allowlist lower the chance of a mistaken operation. Multiple projects still run under the same Termux app user. That is not container or virtual machine level isolation. Code of unknown origin must show its commands and file changes and get approval before it runs.

## Phone hands

The agent first tries to read Android Accessibility nodes and accessible button text. It uses screenshot coordinates only when node location fails. Each operation follows one cycle: observe, state what it will do, do one action, read the screen again to confirm the result. During a task, a persistent status bar and a Stop button stay visible. Login, verification codes, CAPTCHA, passwords, PINs, and card numbers are typed by the user. Payment, transfer, sending, deleting, and publishing ask for confirmation each time. A past approval does not auto approve the next one. When the screen has FLAG_SECURE or a protected interface, the agent does not try to bypass it. It reports that the screen cannot be read and returns control to the user.

AccessibilityService must be turned on by the user in system settings. Shizuku is paired and started by the user, preferrably through wireless debugging on Android 11 and later. After a restart, it needs to start again under the system limits. On older Android, starting may need a physical computer. When Shizuku is not started or lacks capability, Accessibility operations and plain Termux functions remain. No privilege escalation is required.

## Interface and authorization

The app keeps the nanoMuse Android shape: conversation, agent status, memory, and task flow. open-claude-ui is a React UI component library, not a ready Android native interface. The plan ports its design tokens, input box, message layout, thinking state, motion, and approval card to native phone layout, then adapts for narrow screens. If React components are reused directly, they run in a WebView with only a small set of strictly typed bridge interfaces. The web page does not get arbitrary command execution or file access. When MIT code is used, its copyright and license stay. No Claude proprietary marks or unlicensed material are copied.

## Safety, privacy, and recovery

Four risk tiers: read only, plain write, run command, sensitive operation. Read only queries run with low friction. File changes or builds show the target path and the command. Sending, paying, and deleting ask for confirmation each time. Each task keeps a local step log, command exit codes, a changed file list, and needed screen state. The user can stop, revert a Git change, or rerun. Passwords and keys go in Android Keystore or a Termux environment the user sets. They are not written to logs. The app does not auto resume cross app operations after boot. On lock screen, permission revoke, or service stop, it pauses. After recovery, it confirms the state again.

## Stages

1. Code and license audit, confirm the reusable nanoMuse Android modules, the local agent core, and the open-claude-ui components, then set the build baseline.
2. Termux connection wizard and fixed launcher. Verify workspace creation, Git status, output return, timeout, and stop.
3. Accessibility hands: screen observation, node first location, tap, swipe, input, action review, and user takeover.
4. Shizuku optional capability and permission status detection.
5. Phone UI, risk cards, task logs, and local memory.
6. Compatibility, permission, restart, background limit, and security regression on a real Android phone, then a debuggable install package, source, build notes, and GPL and third party license files.

## Acceptance

The phone can create a project and run an authorized task in Termux. It returns results and error codes and can stop a long task. Each Accessibility step is visible on screen and can be interrupted at any time. Sensitive submissions ask for confirmation each time. Turning off Termux, Accessibility, or Shizuku degrades functions clearly. Working files and logs stay on the phone by default. The Play Store rules for AccessibilityService use and disclosure need a separate review. Android defines AccessibilityService as a service to help people with disabilities use the device, so the release channel and the function statement need separate review.
