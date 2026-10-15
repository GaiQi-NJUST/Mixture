# Model client, PR6

This note describes the real code in mixture-model. It implements Phase 6 of the plan, the model client, memory, task log, and security regression, for the pure logic part. The Android HTTP client and Keystore code come in a later PR.

## Sources borrowed

- nanoMuse docs/own-key.md and nanomuse/llm/providers.json: the own key model, the provider catalogue, the four jobs, and the key handling rules. Mixture keeps the same model and the same defaults.
- nanoMuse AGENTS.md: tests use a mock LLM. Mixture uses FakeModelClient for all tests, never a real key or a network.
- open-claude-ui: the error card pattern. When a model fails, the error card offers the one time Cloud option for that one turn only, and changes no row.

## What the code does

- ModelConfig holds the OpenRouter defaults, the capability set, the model name check, and the missing capability message.
- ModelRequest holds the model, the messages, max tokens, and temperature. It validates the model name, the messages, and the secret check.
- SecurityCheck detects and redacts key, api key, password, token, and card number patterns.
- ModelClient is the interface. FakeModelClient records requests and returns a fixed response, or fails when configured.
- TaskLog is append only. It redacts before storing, and it can list entries for one task and render the log as text.
- MemoryStore holds preferences. It rejects secret keys and secret values.

## What is not in this PR

- No HTTP client. The Android code that calls OpenRouter with the key from Keystore comes in a later PR.
- No Keystore code. The secure storage code comes with the Android app code.
- No Android UI. The Settings, Models screen comes with the app UI PR.

## Verification

The unit tests cover defaults, model name validation, fake client text and usage, key in content rejection, empty messages rejection, secret detection and redaction, task log redaction, memory put get remove, memory secret rejection, and the missing video message. They run on the JVM with no phone and no network.

Verified in this workspace: kotlinc 2.0.21 with JDK 17, JUnit 4.13.2. Main sources compile with exit 0. Test sources compile with exit 0. JUnit run reports OK. The Gradle build in CI and in Android Studio remains the canonical check.
