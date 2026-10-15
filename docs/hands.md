# Hands, PR3

This note describes the real code in mixture-hands. It implements Phase 3 of the plan, the Hands operator, for the pure logic part. The Android AccessibilityService comes in a later PR.

## Sources borrowed

- nanoMuse docs/gui.md and docs/every-device.md: the ladder from a raw screenshot to a structured model, then the operator loop: grounding, action, confirmation, with re-observation after each action. Mixture keeps the same loop but keeps the module small and testable.
- open-claude-ui: the approval card pattern. Sensitive actions map to an approval card with Approve and Reject, and the card state is per action, not per task.
- Termux bridge PR2: the same rule that each action is validated before it is sent, and that history approval never covers the next action.

## What the code does

- ScreenNode holds id, text, content description, bounds, clickable, editable, password, and package name. It reports the display text, visibility, and whether it is a password field.
- ScreenState holds the nodes, the secure flag, the package name, the screenshot flag, and the timestamp. It finds nodes by id, by text, and by clickable text. It lists visible nodes.
- HandsAction holds one action. The sensitive wrapper holds an inner action plus a category: PAY, TRANSFER, SEND, DELETE, PUBLISH, CONFIRM.
- HandsApproval holds the approval request, the result, and the store. FakeApprovalStore records requests and returns the configured decision.
- HandsOperator holds the current state, the stopped flag, and the step history. Observe sets the state and returns SecureScreen when the screen is secure. Act checks stopped, step limit, observation, blocked cases, sensitive approval, then runs the executor once. Stop blocks later actions.

## Blocked cases

- No observation yet.
- Secure screen.
- Tap or input on a missing node.
- Input into a password field. The user types it personally.
- Input into a node that is not editable.
- Sensitive action without approval.

## What is not in this PR

- No AccessibilityService. The Android service that reads the tree and performs the actions comes in a later PR.
- No screenshot code. The screenshot path comes with the Android app code.
- No model call. The model that picks the next action comes with the model client PR.
- No UI. The approval card UI comes with the app UI PR.

## Verification

The unit tests cover node bounds, password detection, state lookup, one action per turn, coordinate fallback, plain input, password block, sensitive approval, separate approval per action, secure screen, stop, missing node, and step limit. They run on the JVM with no phone and no network.

Verified in this workspace: kotlinc 2.0.21 with JDK 17, JUnit 4.13.2. Main sources compile with exit 0. Test sources compile with exit 0. JUnit run reports OK. The Gradle build in CI and in Android Studio remains the canonical check.
