# Changelog

## Unreleased

- Added the GPL-3.0-or-later baseline for Mixture, with README, NOTICE, and third party notices.
- Added the architecture note, the OpenRouter model API note, the design tokens note, and the fork note.
- Recorded the upstream nanoMuse commit d808bb2 as the skeleton source.
- Added the mixture-termux module with real code: workspace paths, validated TermuxCommand, TermuxResult, TermuxBridge, and TermuxLauncher, plus unit tests.
- Added the mixture-hands module with real code: screen nodes, screen state, Hands actions, approval, and the Hands operator, plus unit tests.
- Added the mixture-shizuku module with real code: Shizuku state, validated commands, read only detection, approval, and the adapter, plus unit tests.
- Added the mixture-ui module with real code: design tokens, chat state, approval cards, and the chat reducer, plus unit tests.
- Added the mixture-model module with real code: OpenRouter config, model request, security check, fake model client, task log, and memory store, plus unit tests.
- Added Hands Tap by node and Input wiring, the adaptive icon, and the Gradle wrapper files.
- Added the android-app shell with real code: MainActivity, MixtureAccessibilityService, TermuxIntentSender, ShizukuBinder, OpenRouterHttpClient, KeystoreStore, Manifest, and native layout.
- Rewrote the main README and the android-app README in Chinese. Reviewed the frontend against the open-claude-ui tokens and fixed the gaps: surface 3, user bubble tint, border, danger, dark mode, radii, shadows, press scale, and approval status display.

## 0.0.0

- Empty repository with an MIT license and a one line README. Replaced by the GPL baseline in the Unreleased section above.
