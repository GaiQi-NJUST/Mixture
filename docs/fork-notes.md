# Fork note, skeleton source

Upstream: https://github.com/nano-muse/nanoMuse, commit d808bb2, GPL-3.0-or-later.

This PR is the baseline. It does not yet copy the full upstream tree. It records the upstream commit as the skeleton source. Later PRs import the Android modules in slices, one module at a time, for review.

## Why no API fork

Calling fork_repository through the Muse GitHub App returned 403, Resource not accessible by integration, in a test on 2026-10-11. The API fork path does not work here. Do not retry the same call.

## How the code reached Mixture

The Mixture repository was created as a new empty repository. This PR adds the GPL baseline and the design notes to it. The upstream tree stays at its own remote. Do not push Mixture changes to the upstream remote.

## License duties on release

- Keep GPL-3.0-or-later, LICENSE, NOTICE, and THIRD_PARTY_NOTICES.md.
- Android is based on OpenMinis 1.13, GPL-3.0. Edits inside upstream files carry a // nanoMuse: comment in the upstream tree. Mixture edits carry a // Mixture: comment. New Mixture code lives in new files.
- When open-claude-ui MIT components are merged, keep their MIT notice. A release based on nanoMuse still provides the corresponding source under GPL.
- Do not copy Claude proprietary marks or unlicensed material. open-claude-ui states it does not distribute proprietary binary assets.

## Rebranding

Rebranding goes through scripts/rebrand.py in the upstream tree, plus the icon generators. Fix the script, not the miss. Do not rename the Kotlin package, the Gradle namespace, or the sandbox paths by hand. This PR does not rename packages. It only adds Mixture docs and the Mixture NOTICE.
