# Mixture

Mixture is a personal agent that runs on one Android phone. It is based on nanoMuse, under GPL-3.0-or-later.

This branch is PR1, the baseline. It contains the license, the architecture note, the model API note, the design tokens note, and the fork note. It does not yet contain the Android app code. The Android code comes in later PRs, one module at a time, for review.

## What Mixture will do

The plan is in docs/architecture.md. In short: the phone runs the agent locally, with Termux for commands and the workspace, Accessibility for reading the screen and gestures, and Shizuku for system API and ADB shell inside the authorized scope. The model is pluggable. This PR uses OpenRouter with your own key, as described in docs/model-api-openrouter.md. The UI follows the tokens in docs/design-tokens-open-claude-ui.md.

## License

GPL-3.0-or-later. See LICENSE, NOTICE, and THIRD_PARTY_NOTICES.md.

## Source

Upstream nanoMuse, commit d808bb2. See docs/fork-notes.md for the import path and the attribution.
