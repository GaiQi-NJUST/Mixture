# mixture-ui

UI state for Mixture. This module holds only pure logic: design tokens, chat state, approval cards, and the reducer. No Android classes and no Compose code are used here. The Android app renders this state with native layout.

## Files

- DesignTokens: colors, radii, motion, and interaction constants from open-claude-ui, MIT.
- ChatState: messages, composer, thinking, approval cards, task status, and app state.
- ChatReducer: pure functions that return a new state for each action.

## Rules

- Tokens match docs/design-tokens-open-claude-ui.md. Clay main #D97757, hover #CC5A36. Light surface 0 #F5F5F3. Text primary #151515. Bubble radius 18. Composer radius 20. Panel radius 10. Dialog radius 16.
- Thinking stays forced open while generating, with a mutual exclusion lock. The reducer sets thinkingForcedOpen to true on send and to false on receive or stop.
- Approval card has three states in the UI sense: pending, running, success or error. Each card covers one action. Approve moves pending to running. Finish moves running to success or error. Reject moves pending to error.
- Composer has a CJK double interlock guard: it cannot send while composing. The reducer blocks send when isComposing is true.
- Stop clears thinking and hands running, and sets the task to stopped.

## Tests

Run the unit tests with Gradle from the module directory:

```sh
./gradlew :mixture-ui:test
```

The tests use only the reducer. They do not need a phone, a screen, or a network.
