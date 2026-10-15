# mixture-hands

Hands for Mixture: the accessibility operator. This module holds only pure logic. No Android classes are used here. The Android app supplies the real accessibility tree and the real executor.

## Files

- ScreenNode: one node from the accessibility tree, with bounds and flags.
- ScreenState: one observation, with find helpers and the secure flag.
- HandsAction: one action per turn: tap, tap at, swipe, input, back, home, open app, wait, and the sensitive wrapper.
- HandsApproval: approval for sensitive actions. Each approval covers one action only.
- HandsOperator: observe, one action, re-observe, confirm. Stop at any time.

## Rules

- Node first, coordinate fallback. Tap by node id when a node exists. TapAt is only for cases where no node matches.
- One action per turn. After each action the caller must re-observe and confirm before the next action.
- Sensitive actions need separate approval each time: pay, transfer, send, delete, publish, confirm. A past approval never covers the next action.
- Password fields are blocked. The user types passwords, PINs, card numbers, login codes, and CAPTCHAs personally. The agent never fills them.
- FLAG_SECURE screens are not bypassed. The operator returns SecureScreen and stops. It reports that the screen cannot be read and hands back to the user.
- Step limit defaults to 30. Stop blocks later actions.

## Tests

Run the unit tests with Gradle from the module directory:

```sh
./gradlew :mixture-hands:test
```

The tests use a fake executor and a fake approval store. They do not need a phone, accessibility, or a network.
