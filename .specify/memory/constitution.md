<!--
SYNC IMPACT REPORT
- Source of truth: This file defines non-negotiable project rules.
- Referenced by: GOVENANCE.md, CONTRIBUTING.md, specs/* artifacts.
- If you change MUST rules here, update any conflicting guidance in docs/ and build quality gates.
-->

# Project Constitution

- **Project**: LibGDXKotlinOdyssey (宗门模拟游戏)
- **Version**: 0.2.0
- **Last updated**: 2025-12-20
- **Ratified**: TODO(RATIFICATION_DATE)

## I. Scope

This constitution applies to all modules in this mono-repo, including (but not limited to):
- `lko-sect`
- `lko-libs/*`
- `lko-gdx-libs/*`
- `lko-graph-libs/*`
- `tests/*`

## II. Architecture Principles (non-negotiable)

1. **ECS-first**
   - Domain behavior MUST be expressed through ECS concepts first (components/systems/relations) when reasonable.
   - OO wrappers are allowed, but MUST not hide domain state transitions from ECS.

2. **Reuse before reinvent**
   - New features MUST reuse existing services (inventory/leveling/money/countdown, etc.) when suitable.
   - If you introduce a parallel concept, you MUST justify it in the spec and link alternatives considered.

3. **Consistency via Addon + DI**
   - Modules SHOULD expose an Addon-style entry point and use dependency injection consistently.
   - Public APIs MUST be stable and documented.

## III. Quality Gates (merge blockers)

These are merge blockers unless explicitly waived by maintainers with a written rationale.

1. **Build correctness**
   - Compilation errors MUST be 0.

2. **Static analysis**
   - `detekt` MUST pass.
   - Repo configuration expects `maxIssues: 0` (no outstanding issues).

3. **Formatting**
   - `ktlintCheck` MUST pass.

4. **Tests**
   - All tests MUST pass (pass rate = 100%).

## IV. Testing & Coverage

1. **Test discipline**
   - New behavior MUST ship with tests.
   - Prefer AAA structure and clear naming.

2. **Coverage baseline**
   - Target coverage is **≥ 80%** for in-scope packages.
   - If coverage is below the threshold, contributions MUST either:
     - raise coverage, or
     - document why the change is out-of-scope / not meaningfully testable.

> Note: The executable enforcement lives in Gradle quality configuration (see `quality.gradle.kts`).

## V. Performance Budget

- Critical game loop paths SHOULD be budgeted for **60 FPS**.
- If a change affects hot paths, you MUST provide either:
  - a micro benchmark, or
  - a reasoned performance note with complexity + allocations.

## VI. Documentation Rules

- Public APIs MUST have KDoc.
- Any user-facing behavior change MUST update the relevant documentation under `docs/`.

## VII. Compatibility & Versioning

- This repo follows SemVer for public artifacts.
- Breaking API changes MUST bump MAJOR and MUST be called out in release notes.

## VIII. Governance & Change Process

- Changes to this constitution MUST be reviewed and approved by maintainers.
- Any change that weakens a MUST rule requires:
  - explicit motivation,
  - impact analysis,
  - and a plan for maintaining quality/consistency.

