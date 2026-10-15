# mixture-termux

Termux workspace and RUN_COMMAND bridge for Mixture. This module holds only pure logic. No Android classes are used here. The Android app sends the intent through a TermuxBridge implementation.

## Files

- TermuxWorkspace: project name validation and workspace paths.
- TermuxCommand: validated command and RUN_COMMAND extras.
- TermuxResult: result from Termux, with truncation detection.
- TermuxBridge: interface plus a fake for tests.
- TermuxLauncher: start, stop, and running task tracking.

## RUN_COMMAND extras

The extras follow the Termux RUN_COMMAND wiki. The command path is absolute under /data/data/com.termux/files/usr/bin. Arguments are a String array. No user input is concatenated into one shell string. The work dir must equal the project dir. Background commands return separate stdout and stderr. Foreground commands return a session transcript.

## Allowlist

Allowed executables: git, ls, pwd, cat, echo, sh, python3, node. Everything else is rejected. Arguments must not contain shell metacharacters. The total command length must stay under 131072 bytes. The intent size limit is about 500KB. The caller must keep all extras under that limit.

## Tests

Run the unit tests with Gradle from the module directory:

```sh
./gradlew :mixture-termux:test
```

The tests use a fake bridge. They do not need Termux, a phone, or a network.
