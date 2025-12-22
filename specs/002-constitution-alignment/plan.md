# Plan

> Follow `.specify/memory/constitution.md`.
> Feature spec: `specs/002-constitution-alignment/spec.md`.

## Technical Context

- **Primary language**: Kotlin (Gradle Kotlin DSL)
- **Build system**: Gradle (multi-module)
- **Target scope (code alignment)**: `lko-sect` v2 core paths (engine/world init, system scheduling, addon assembly, DI wiring)
- **Target scope (quality gates)**:
  - All *in-scope* modules: detekt + ktlint + tests (per spec R-3)
  - Coverage gate: **only** `lko-sect` v2, line coverage **≥ 60%** (per spec R-4)
- **Unknowns**: None declared by spec; any repo-specific gaps discovered during audit become tracked exceptions with rationale + cleanup plan.

## Constitution Checks (must-pass)

From `.specify/memory/constitution.md`:

- Architecture principles:
  - ECS-first (behavior via systems/components first)
  - Consistency via Addon + DI
- Quality gates (merge blockers):
  - Build correctness: compile errors = 0
  - Static analysis: detekt must pass, maxIssues: 0
  - Formatting: ktlintCheck must pass
  - Tests: all tests must pass
- Testing & coverage:
  - Target coverage ≥ 80% (constitution target); **this feature uses a phased threshold** for `lko-sect` v2: ≥ 60% now, ≥ 75% later, converge to ≥ 80%
- Docs:
  - Public APIs have KDoc
  - User-facing behavior changes update `docs/`

## Milestones

### M0 — Baseline audit (stop-the-bleeding)

**Outcome**: A written audit of `lko-sect` v2 “core entrypoints” and current gate status; no surprise scope creep.

- Identify v2 core entrypoints (at least):
  - world/engine initialization
  - system scheduling loop
  - addon assembly/registry
  - DI module/binder entrypoint
- Record constitution conflicts (ECS-first / Addon+DI) as:
  - Fix-now items (small, low risk)
  - Exception items with explicit rationale + follow-up plan (if truly unavoidable)
- Establish current status of quality gates (which tasks exist, which fail, why).

**Exit criteria**:
- Audit notes exist (in PR description or appended to `specs/002-constitution-alignment/spec.md` Rollout if needed).
- We can run a single canonical local command that approximates CI (`./gradlew preCommit`) and see deterministic results.

### M1 — Quality gates consistency (repo-wide in-scope)

**Outcome**: detekt/ktlint/tests are consistently enforced for in-scope modules; local = CI.

- Ensure a single entrypoint Gradle task exists and is documented:
  - `preCommit` should run (at minimum): `ktlintCheck`, `detekt`, `test` (and any required compilation checks).
- Make module boundaries explicit:
  - For modules not yet clean, do **not** relax global rules silently.
  - If a module cannot pass immediately, document a temporary exception (scoped + timeboxed) rather than weakening gates.

**Exit criteria**:
- `./gradlew preCommit` passes on a clean checkout (or failures are explicitly tracked as exceptions with rationale).
- CI runs the same task(s).

### M2 — SECT v2 ECS-first alignment (minimal refactor slices)

**Outcome**: v2 behavior expressed via systems/components; no new global hidden state.

- Refactor only the audited conflict points by small slices:
  1) system scheduling loop: make state transitions explicit in ECS
  2) initialization: avoid singleton/implicit state; pass dependencies explicitly
  3) component/system responsibilities clarified
- Add tests per slice (see “Verification steps”).

**Exit criteria**:
- Identified ECS-first conflicts are fixed or tracked as explicit exceptions.
- Tests cover core scheduling + init flow.

### M3 — SECT v2 Addon + DI alignment

**Outcome**: Addon assembly is explicit, DI owns lifecycle and wiring.

- Provide / align a clear Addon entrypoint for v2:
  - explicit enable/disable
  - explicit dependencies (auditable)
- Enforce: Addons don’t construct core services outside DI (except pure value objects).

**Exit criteria**:
- Addon assembly + dependency resolution tested.
- Example path exists for “add an Addon + System” without touching unrelated modules.

### M4 — Coverage gate for SECT v2 (≥ 60% line coverage)

**Outcome**: Coverage is measurable and merge-blocking for `lko-sect` v2 only.

- Configure coverage reporting + verification (prefer Gradle-integrated tooling).
- Define exclusions (generated code, platform adapters, resources wrappers) and document them.
- Add/extend tests to reach ≥ 60% line coverage.

**Exit criteria**:
- Coverage verification task fails below threshold and passes at/above threshold.
- Local and CI compute coverage the same way.

### M5 — Documentation alignment

**Outcome**: README/CONTRIBUTING/docs match reality and provide one canonical workflow.

- README:
  - value + quickstart
  - canonical gate command(s)
- CONTRIBUTING:
  - required quality gates (format → lint → test → coverage)
  - how to run module-scoped iterations
- docs:
  - ECS-first and Addon+DI explanation for v2
  - minimal example (addon + system)

**Exit criteria**:
- A new contributor can find v2 entrypoints and run the gates in ≤ 30 minutes.

## Risks

1. **Hidden scope expansion**: “ECS-first” refactor could creep outside v2.
   - Mitigation: lock scope to audited entrypoints; no drive-by renames/moves.
2. **Gate incompatibility across modules**: detekt/ktlint rules may not be uniformly satisfied.
   - Mitigation: don’t relax global thresholds; use scoped, explicit exceptions with a rollback plan.
3. **Coverage tooling mismatch (local vs CI)**:
   - Mitigation: enforce coverage via Gradle verification task; ensure CI calls the same task.
4. **Test flakiness/performance**:
   - Mitigation: keep tests deterministic; avoid timing-based assertions; run in `check` lifecycle.
5. **Public API breakage during alignment**:
   - Mitigation: prefer compatibility shims + deprecations; document migration if unavoidable.

## Quality Gates

Canonical entrypoint:

- `./gradlew preCommit`

Expected composition (verify in build logic):

- Formatting: `ktlintCheck`
- Static analysis: `detekt`
- Tests: `test`
- Coverage (this feature adds/enables for `lko-sect` v2): `:lko-sect:koverVerify` (or equivalent)

> Note: Exact coverage task names depend on the coverage plugin configured in this repo. If not present, add it in build logic and keep the public entrypoint stable via `preCommit`.

## Verification Steps (must be runnable)

### Local verification (developer)

Run in order while iterating:

- Full gate:
  - `./gradlew preCommit`

- SECT v2 focused checks (faster iteration):
  - `./gradlew :lko-sect:ktlintCheck :lko-sect:detekt`
  - `./gradlew :lko-sect:test`
  - `./gradlew :lko-sect:check`

- Coverage (merge blocker for v2):
  - `./gradlew :lko-sect:koverHtmlReport`
  - `./gradlew :lko-sect:koverVerify`

### CI verification

- CI must run the same Gradle entrypoint(s) (`preCommit` and any module-scoped verify tasks if needed).
- Artifact expectations (optional but helpful): publish detekt/ktlint reports + coverage HTML/XML.

## Minimal-change Strategy

- No broad package renames, no moving directories unless directly required.
- Keep public APIs stable; if refactor requires change:
  - add deprecated wrappers and migration notes
  - keep binary/source compatibility when possible
- Convert “implicit global state” to explicit dependencies only on the audited v2 paths.
- Prefer additive wiring (new Addon/DI module) over invasive rewrites.

## Implementation Notes

- Track exceptions explicitly (where constitution rules can’t be met immediately):
  - location, rationale, remediation plan, and target date/feature.
- Ensure every refactor slice is protected by tests before proceeding to the next slice.
- After design is complete, re-check constitution alignment:
  - ECS-first intact
  - Addon+DI intact
  - detekt/ktlint/tests pass
  - v2 coverage ≥ 60%
