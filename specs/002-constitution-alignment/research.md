# Research — Constitution Alignment (lko-sect v2)

> Context: `specs/002-constitution-alignment/spec.md`
> Goal: Resolve implementation uncertainties for ECS-first + Addon+DI alignment, and define repo quality-gate execution strategy.

## Decision 1: Treat `lko-sect` (v2 packages) as the only coverage-gated scope for this feature

- **Decision**: Enforce coverage threshold only on `lko-sect` v2 core code, with a merge-blocking line coverage threshold of **≥ 60%**.
- **Rationale**: Matches spec clarifications (phase goal) while keeping change surface minimal; other modules still run format/lint/tests.
- **Alternatives considered**:
  - Enforce coverage across all modules now → too risky, large refactor/test effort.
  - No coverage gate at all → fails constitution direction and doesn’t create measurable improvement.

## Decision 2: Prefer Gradle-level enforcement to keep local/CI behavior identical

- **Decision**: Implement/adjust gates as Gradle tasks (or composite tasks) and make CI call the same tasks.
- **Rationale**: Constitution explicitly requires local = CI behavior. Gradle is the repo build system.
- **Alternatives considered**:
  - Separate CI-only scripts → risk of drift.
  - IDE-only checks → not reproducible.

## Decision 3: Use “baseline/exception tracking” instead of allowing new violations

- **Decision**: For detekt/ktlint, do not allow new issues; where legacy issues exist, document and explicitly scope exceptions.
- **Rationale**: Constitution prefers `maxIssues: 0` (merge blocker). If repo isn’t green today, plan must include a controlled path to green.
- **Alternatives considered**:
  - Increase maxIssues → weakens gate.
  - Blanket suppressions → hides true quality status.

## Decision 4: ECS-first alignment should be done by small, test-protected refactor slices

- **Decision**: Identify v2 “core entrypoints” and align them iteratively: (1) system scheduling loop, (2) world/engine init, (3) addon assembly, (4) DI wiring.
- **Rationale**: Reduces risk vs. broad redesign; matches spec “minimal change” and “each step has tests”.
- **Alternatives considered**:
  - Large architecture rewrite → non-goal.

## Decision 5: Documentation must expose one canonical set of commands and one canonical v2 extension example

- **Decision**: Create/refresh docs so that contributors have:
  - one “pre-commit” command,
  - module-scoped commands for `lko-sect` v2,
  - a minimal “add an Addon + System” example.
- **Rationale**: Spec acceptance requires onboarding within 30 min and doc/impl alignment.
- **Alternatives considered**:
  - Multiple doc entrypoints → confusion.

## Open items resolved

- Coverage scope & threshold: resolved by spec clarification (60% on `lko-sect` v2 line coverage).
- Gate execution: standardized via Gradle tasks.


