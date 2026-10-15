# UI, PR5

This note describes the real code in mixture-ui. It implements Phase 5 of the plan, the app UI state, for the pure logic part. The Android native layout that renders this state comes in a later PR.

## Sources borrowed

- open-claude-ui, MIT, code-examples/cds-theme.css: colors, radii, borders, shadows, motion, and interaction physics. Mixture ports the tokens to native Android resources first. It does not put React components directly in a WebView as the first step.
- open-claude-ui approval card: three states, pending, running, success or error. Mixture keeps the same states and the same per action rule.
- nanoMuse: the task status and Stop pattern. The app shows a resident status during a task and offers Stop at any time.

## What the code does

- DesignTokens holds the colors, radii, motion, and interaction constants from the open-claude-ui doc.
- ChatState holds messages, composer, thinking, approval cards, task status, current model, and capability flags.
- ChatReducer holds pure functions: set composer text, set composing, send message, receive message, set thinking, add approval, approve, finish approval, reject approval, stop task, finish task, set model, set capabilities, set hands running.

## What is not in this PR

- No Android layout, no Compose, no XML. The native layout that renders this state comes in a later PR.
- No WebView. If React is reused in a WebView later, it opens only a small set of strictly typed bridge interfaces. The web page does not get arbitrary command execution or file access.
- No model call. The model client comes in the next PR.

## Verification

The unit tests cover tokens, send and receive, composer composing guard, approval card flow, add approval, approve, stop, set model, and finish task. They run on the JVM with no phone and no network.

Verified in this workspace: kotlinc 2.0.21 with JDK 17, JUnit 4.13.2. Main sources compile with exit 0. Test sources compile with exit 0. JUnit run reports OK. The Gradle build in CI and in Android Studio remains the canonical check.
