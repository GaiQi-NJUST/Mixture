# mixture-model

Model client, task log, and memory for Mixture. This module holds only pure logic. No Android classes, no HTTP code, and no key storage are used here. The Android app supplies the real HTTP client and the key from Secure Vault or Keystore.

## Files

- ModelConfig: OpenRouter defaults from docs/model-api-openrouter.md, plus capability checks.
- ModelRequest: request and response models, with validation.
- SecurityCheck: secret detection and redaction.
- ModelClient: interface plus a fake client for tests.
- TaskLog: append only log with redaction before storing.
- MemoryStore: simple preferences store that rejects secrets.

## Rules

- Base URL is https://openrouter.ai/api/v1, protocol openai. Defaults: chat deepseek/deepseek-v4.1-flash, hands qwen/qwen3.8-27b, image openai/gpt-image-2, video none.
- A provider without a capability does not offer that function. The UI states which provider is missing, in one sentence. It does not fail silently.
- Keys go through Secure Vault or Android Keystore. They are not pasted in chat, not written to logs, not committed to the repo, and not written in plain text in config.
- When a model fails, nothing falls back on its own. The error card offers the one time Cloud option for that one turn only, and changes no row.
- Task text and screenshots that may contain personal information go to the selected provider when a network model is used. The architecture keeps the model pluggable, so a local model can replace it later.

## Tests

Run the unit tests with Gradle from the module directory:

```sh
./gradlew :mixture-model:test
```

The tests use a fake client. They do not need a key, a network, or a phone.
