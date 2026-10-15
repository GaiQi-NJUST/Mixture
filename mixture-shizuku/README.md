# mixture-shizuku

Shizuku adapter for Mixture. This module holds only pure logic. No Android classes and no Shizuku SDK classes are used here. The Android app supplies the real Shizuku binder and the real permission check.

## Files

- ShizukuState: availability and permission state, plus the degradation message.
- ShizukuCommand: validated command, with read only detection.
- ShizukuAdapter: state, permission, read only, approval, send, and running tracking.

## Rules

- Shizuku is not root. It only runs what the user authorized.
- Read only commands run without extra approval: pm list, pm dump, pm path, dumpsys, settings get, cmd package list, am stack list.
- Write commands need approval each time: settings put and other non read only commands. A past approval never covers the next command.
- Blocked subcommands: install, uninstall, reboot, su, sudo, rm, push, pull.
- Arguments must not contain shell metacharacters.
- When Shizuku is not ready or permission is denied, Termux and Hands still work. Shizuku features are off with a clear message.

## Tests

Run the unit tests with Gradle from the module directory:

```sh
./gradlew :mixture-shizuku:test
```

The tests use a fake bridge and a fake approver. They do not need Shizuku, a phone, or a network.
