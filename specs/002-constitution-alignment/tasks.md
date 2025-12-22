# Tasks — 002 Constitution Alignment (lko-sect v2)

> Source: `specs/002-constitution-alignment/plan.md`, `specs/002-constitution-alignment/spec.md`
> 
> Goal: Align `lko-sect` v2 with constitution principles (ECS-first, Addon + DI), unify quality gates (detekt/ktlint/tests/coverage), and align docs.
>
> Task sizing: each task is designed to be doable in ~0.5–2h.

---

## Implementation strategy (MVP-first, incremental delivery)

- **MVP scope**: Make quality gates deterministic and enforceable via one canonical command (`./gradlew preCommit`). Then add the minimal `lko-sect` v2 tests required to protect refactor slices.
- **Incremental slices** (protect with tests before refactor):
  1) scheduling loop (ECS-first)
  2) world/engine init (no implicit global state)
  3) addon assembly (explicit, auditable)
  4) DI wiring (addons/systems created via DI)
- **Coverage gate**: only `lko-sect` v2, line coverage ≥ 60% (merge blocker for this feature).
- **No scope creep**: no broad renames/moves unless directly needed.

---

## Phase 1 — Setup (project initialization / discovery)

**Goal**: Make the repo’s “one command” gate and module scope explicit, so later tasks can reference real Gradle task names.

- [X] T001 运行并记录 Gradle quality gate tasks 清单到 `specs/002-constitution-alignment/spec.md`（新增 “Audit notes” 小节）：确认 `preCommit`、`ktlintCheck`、`detekt`、`test`、覆盖率相关任务（如 `koverVerify`/`jacocoTestCoverageVerification`）在本仓库的真实 task 名称与所属 module；若不存在则记录为缺口

- [X] T002 识别并记录 `lko-sect` v2 的“核心入口与关键路径”列表到 `specs/002-constitution-alignment/spec.md`（engine/world init、system scheduling、addon assembly、DI binder/entrypoint），附上对应代码文件路径（绝对不要重命名/搬迁）

- [X] T003 [P] 建立 exceptions/基线追踪文档：在 `specs/002-constitution-alignment/spec.md` 增加 “Tracked exceptions (timeboxed)” 表格模板（location / rationale / remediation plan / target date）

- [X] T004 校验 `preCommit` 是否为唯一推荐入口：若不存在则在根 `build.gradle.kts` 或 build logic 中新增/修正聚合任务 `preCommit`（最少包含 ktlint + detekt + test + coverage verify）

- [X] T005 统一 in-scope modules 的 ktlint 配置：由 build-logic 约定插件统一启用 `ktlintCheck/ktlintFormat`

- [X] T006 统一 in-scope modules 的 detekt 配置：由 build-logic 约定插件统一使用 `detekt.yml`，并保持 `maxIssues=0`

- [X] T009 [US1] 确保 `preCommit` 明确依赖 `ktlint*`、`detekt`、`test`、`coverage verify` 并可重复运行：已在 root-quality-conventions 中显式 dependsOn ":lko-sect:*" 任务

- [X] T013 [US2] 确认覆盖率插件与真实任务名：采用 Kover，任务为 `:lko-sect:koverHtmlReport` / `:lko-sect:koverVerify`

- [X] T014 [US2] 为 `lko-sect` 启用覆盖率报告：已由 `cn.jzl.sect-module-conventions` 应用 `org.jetbrains.kotlinx.kover`

- [X] T015 [US2] 配置 coverage verify（仅 v2 范围）并设置 line ≥ 60%：见 `kover { reports { filters { includes { classes("cn.jzl.sect.v2.*") } } verify { ... } } }`

- [X] T016 [US2] 将 coverage verify 纳入 `preCommit`：root `preCommit` dependsOn `:lko-sect:koverVerify`

- [X] T018 [US3] 为 world/engine 初始化添加冒烟测试：以最小 world + addon 安装为基础新增测试（AddonAssemblyTest）

- [X] T019 (P, US3) 为 system scheduling 核心路径新增 unit test：验证系统被注册、按预期顺序执行、ECS 状态变化可观察（避免隐式全局状态）
  - Verification:
    - `./gradlew :lko-sect:test --tests *Scheduling*`

---

## Phase 2 — Foundations (blocking prerequisites)

**Goal**: Ensure quality gates are consistent and deterministic (local = CI) before deeper refactors.

- [X] T007 建立“可追踪例外”机制：本轮无例外（Tracked exceptions 表为空）。
  - Verification:
    - `./gradlew preCommit`

- [X] T008 明确 CI 与本地的一致入口：已在 README/CONTRIBUTING/Spec 的 Quality gate contract 中统一为 `./gradlew preCommit`。
  - Verification:
    - `./gradlew preCommit`

---

## Phase 3 — US1 (P1): 质量门禁一致化（detekt/ktlint/tests）

**Story goal**: 本地运行一次命令即可得到与 CI 一致的格式化/静态检查/测试结果；任何新增违规都会被阻止。

- [X] T010 [US1] 为 KMP 模块提供稳定 `:lko-sect:test` 入口（alias to desktopTest）：已由 `cn.jzl.sect-module-conventions` 提供。
  - Verification:
    - `./gradlew :lko-sect:test`

- [X] T011 [US1] 固定 detekt/ktlint 报告格式（CI 友好）：已在 `cn.jzl.kotlin-quality-conventions` 配置（ktlint checkstyle/json；detekt html/xml）。
  - Verification:
    - `./gradlew :lko-sect:detekt`
    - `./gradlew :lko-sect:ktlintCheck`

- [X] T012 [US1] 在 spec 中补充 Quality gate contract：已完成（见 `spec.md` 的 Audit notes）。

**Checkpoint (US1)**
- `./gradlew preCommit` 是唯一入口（本环境终端输出捕获受限时，按 Spec 记录命令在本地/CI 验证）。

---

## Phase 4 — US2 (P1): lko-sect v2 覆盖率门禁（≥ 60% line coverage)

**Story goal**: 覆盖率只对 `lko-sect` v2 生效且是 merge blocker；本地与 CI 计算一致，并提供 HTML 报告。

- [X] T013–T016 [US2] 覆盖率门禁接通（Kover + v2 only + ≥60% + 纳入 preCommit）：已完成（见 Spec 的 Coverage contract 与 build-logic wiring）。

**Checkpoint (US2)**
- `./gradlew :lko-sect:koverVerify` 作为 merge blocker 已被 root `preCommit` 依赖。

---

## Phase 5 — US3 (P1): lko-sect v2 最小必要测试补齐（保护重构切片 + 推动覆盖率)

**Story goal**: 为 v2 核心路径建立可重复的 unit tests：world/engine init、system scheduling、addon assembly + dependency resolution。

- [X] T017 [US3] 测试入口与约定：Kotlin `commonTest` 下以 `kotlin.test` 编写；运行入口为 `:lko-sect:test`（alias to desktopTest）。

- [X] T018 [US3] world/engine init 冒烟：`AddonAssemblyTest`

- [X] T019 [US3] scheduling 核心路径：`SchedulingTest`

- [X] T020 [US3] addon assembly/dep resolution：`AddonAssemblyTest`

- [ ] T021 [US3] 根据覆盖率报告补齐关键路径缺口测试，直到 `:lko-sect:koverVerify` 稳定通过。
  - Progress: Added coverage-oriented tests (`AttributesTest`, `ItemServiceTest`, `InventoryServiceTest`, `HealthServiceTest`, `LevelingServiceTest`, `EffectsValueTypesTest`, `EquipmentServiceTest`, `AStarPlannerTest`) to increase v2 exercised surface.
  - Next: Run `./gradlew :lko-sect:koverVerify` and add more tests if it still fails.
