# Quickstart — Constitution Alignment (lko-sect v2)

This quickstart is about *running the repo’s quality gates* and validating `lko-sect` v2 alignment.

## Local quality gates (canonical)

> The constitution defines merge blockers. These commands should match CI.

1) Run the full pre-commit gate:

- `./gradlew preCommit`

2) Run module-scoped gates for SECT v2 (when iterating):

- `./gradlew :lko-sect:check`
- `./gradlew :lko-sect:test`
- `./gradlew :lko-sect:ktlintCheck :lko-sect:detekt`
- Coverage (Kover; merge blocker only for `cn.jzl.sect.v2.*`, LINE >= 60%):
  - `./gradlew :lko-sect:koverHtmlReport`
  - `./gradlew :lko-sect:koverVerify`

## Minimal documentation acceptance check

- Follow `README.md` / `CONTRIBUTING.md` and ensure the above commands are discoverable.

## Minimal extension acceptance check (v2)

- Add a tiny Addon that registers a System via DI.
- Confirm it can be enabled/disabled explicitly.
- Confirm tests cover addon assembly + system scheduling.
