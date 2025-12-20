# 重构当前项目，使其符合项目章程（Constitution Alignment）

> 本仓库的规范产物位于 `specs/<id>-<name>/`。
> 核心规则定义在 `.specify/memory/constitution.md`。

## Clarifications

### Session 2025-12-20

- Q: 覆盖率门禁范围与阈值应如何定义？ → A: 仅 `lko-sect` v2，行覆盖率 ≥60%，后续目标 75%（阶段性对齐章程 ≥80% 目标）。

## Problem

当前仓库已包含多个模块（含 `lko-sect`、若干 lib、测试工程与文档）。随着功能迭代，出现了如下风险：

- 代码组织方式与项目章程不一致（ECS-first、Addon + DI 的使用不统一）。
- 质量门禁（静态检查/格式化/测试/覆盖率）在不同模块之间不一致，导致“能编译但不符合章程”的变更进入主干。
- 文档与实际实现存在偏差，降低贡献者上手效率。

本规范定义一组**可执行、可验收**的重构与治理要求，以**最小必要改动**（避免大规模无关重写）实现“章程对齐”。

## Goals / Non-goals

### Goals

1. **章程对齐**：至少 `lko-sect` v2 核心代码与项目章程一致（ECS-first、Addon + DI）。
2. **门禁一致**：detekt / ktlint / tests / coverage 在所有 in-scope 模块上具备统一、可重复、可度量的门禁。
3. **文档对齐**：对外文档与当前实现一致；贡献指南能指导贡献者通过门禁。
4. **边界清晰**：明确哪些目录/模块必须改，哪些不动，避免无关大改动。

### Non-goals

- 不进行“推倒重来”的架构重写（例如整仓迁移到新构建体系/重排全部模块边界）。
- 不引入与章程无关的大规模新功能。
- 不承诺一次性清理所有历史技术债；仅处理与章程对齐与门禁一致强相关的部分。

## Requirements

### Scope boundaries

1. **必须覆盖的代码范围**：`lko-sect` v2 目录下核心代码（以仓库现状为准：核心运行时、ECS 交互层、Addon 扩展点、DI 绑定入口、关键公共 API）。
2. **必须覆盖的工程范围**：对 in-scope 模块启用一致的质量门禁：detekt、ktlint、tests、coverage。
3. **可选覆盖**：与 `lko-sect` v2 强依赖的公共库模块可纳入“代码对齐”范围；其余模块默认仅纳入“门禁一致性”范围。
4. **明确不做**：不对与目标无直接关系的模块做内部架构重构；不做全仓大规模重命名/搬迁。

### R-1 ECS-first（lko-sect v2）

1. v2 的核心业务行为以 ECS 方式表达：
   - System 负责行为
   - Component 负责状态
   - Entity 负责组合
2. v2 核心路径不得新增“全局单例 + 隐式状态”的依赖模式。
3. 对已识别的 v2 核心路径的章程冲突点进行**最小修改**对齐（不要求扩散到全仓）。

### R-2 Addon + DI（lko-sect v2）

1. v2 必须提供清晰的 Addon 组装入口：
   - 支持声明启用/禁用某个 Addon
   - Addon 依赖必须显式可见（可审计、可测试）
2. DI 用于：
   - 管理 Addon 生命周期
   - 注入系统/服务依赖，减少隐藏依赖
3. Addon 不得绕过 DI 直接创建核心服务对象（纯值对象除外）。

### R-3 质量门禁一致性（全 in-scope 模块）

1. 定义并应用一套统一的 quality gates：
   - 格式化/风格检查（ktlint）
   - 静态检查（detekt）
   - 自动化测试（unit tests）
   - 覆盖率校验（coverage）
2. 必须保证“本地运行门禁”的结果与 CI 一致（同一套规则与阈值）。
3. 对于任何需要例外的模块，必须：
   - 显式声明该例外
   - 在文档或本 spec 的 Rollout 中记录原因与回收计划

### R-4 覆盖率目标（Coverage targets）

覆盖率门禁范围与阈值（本 feature 的最小假设 / 章程阶段性对齐计划）：

1. **门禁范围**：本轮只对 `lko-sect` v2（核心代码）执行覆盖率门禁；其他 in-scope 模块本轮不引入覆盖率阈值门禁（仍需通过 detekt/ktlint/tests）。
2. **当前阈值（merge blocker）**：`lko-sect` v2 行覆盖率 **≥ 60%**。
3. **后续提升目标（非本轮 blocker）**：在后续迭代中将 `lko-sect` v2 的行覆盖率提升到 **≥ 75%**，并继续向项目章程的 **≥ 80%** 目标收敛。
4. **与章程一致性的解释**：项目章程的覆盖率目标（≥80%）视为最终目标；本规范将 60% 作为“从历史基线到章程目标”的阶段性门槛，确保先建立可执行门禁，再逐步提升。

覆盖率门禁通用要求：

- 需有明确的排除策略（例如生成代码、平台/适配层、纯资源封装），且排除项在文档中可追溯。
- 本地与 CI 计算方式一致。

### R-5 文档对齐

1. `README.md` / `CONTRIBUTING.md` / `docs/` 必须说明：
   - 项目章程关键原则（ECS-first、Addon + DI、质量门禁）
   - 最小可执行的本地门禁步骤（format → lint → test → coverage）
2. 若存在与实际实现不一致的描述，需更新为当前行为。
3. 文档需面向贡献者与维护者（非特定语言/框架细节）。

### R-6 变更边界与兼容性

1. 禁止无关的大规模重命名/搬迁文件。
2. 若为章程对齐必须调整对外 API，则必须提供：
   - 明确迁移说明
   - 过渡期（例如标记弃用并保持兼容一段时间）

### Acceptance criteria

1. **章程对齐**：`lko-sect` v2 的核心入口/关键路径完成审计；所有识别出的章程冲突点已修复或被记录为明确例外（含理由与后续计划）。
2. **门禁一致**：在 CI 与本地，对 in-scope 模块运行同一套门禁，结果一致。
3. **静态检查**：in-scope 范围内无“新增违规”；如存在历史违规，必须有明确的基线/豁免策略并说明原因。
4. **测试**：in-scope 模块的关键路径具备可重复的自动化测试；测试失败会阻止合并。
5. **覆盖率**：达到选定的覆盖率范围与阈值（见 R-4）。
6. **文档**：按文档流程可在本地完成门禁运行；新贡献者能在 30 分钟内找到 v2 关键入口并完成最小示例（新增一个简单 Addon 或 System）。
7. **变更边界**：无无关大改动（全仓大规模重命名/搬迁/无关架构重写）。

## Design

> 本节描述“要改哪里、改到什么状态”，但避免落入具体实现细节（不绑定特定工具/插件/代码结构）。

### D-1 `lko-sect` v2 对齐策略（最小改动）

- 列出 v2 的“核心入口与关键路径”（例如：engine/world 初始化、system 调度、addon 装配、DI 绑定入口）。
- 对核心路径做章程审计，输出：
  - ECS-first 冲突点清单（行为/状态耦合、隐式全局状态等）
  - Addon+DI 冲突点清单（绕过 DI、隐式依赖、生命周期不清等）
- 按风险与收益排序，逐项小步修复，确保每一步都有对应测试保护。

### D-2 质量门禁统一策略（分层、可例外）

- 定义“默认门禁”并让所有 in-scope 模块继承。
- 对历史存量不达标处，优先采用：
  1) 不引入新的违规
  2) 以可追溯方式逐步收敛（基线/分阶段阈值/明确排除）
- 所有例外必须可追溯且有回收计划。

### D-3 文档对齐策略

- README：聚焦价值/快速开始/门禁命令入口。
- CONTRIBUTING：聚焦贡献流程与合并门禁。
- docs：聚焦架构理念（ECS-first、Addon+DI）与 v2 关键入口示例。

## Testing Plan

1. **静态检查**：对 in-scope 模块运行 detekt 与 ktlint，验证失败策略为“违规即失败”。
2. **单元测试（v2 核心）**：至少覆盖：
   - world/engine 初始化冒烟测试
   - addon 装配与依赖解析测试
   - system 调度核心路径测试
3. **覆盖率验证**：在本地与 CI 生成覆盖率报告并验证阈值达标；验证排除项符合约定。
4. **文档验收**：按 README/CONTRIBUTING 的步骤从零跑通一次（format → lint → test → coverage）。

## Rollout

1. 先在 `lko-sect` v2 上完成门禁一致化与关键测试补齐，保证核心可持续演进。
2. 再将门禁默认应用到其他模块；对确有例外者添加显式声明与回收计划。
3. 文档更新与代码变更同步提交，避免“代码已变、文档滞后”。

---

## Final audit status (this implementation)

- Gates wiring: DONE (via `build-logic` convention plugins + root `preCommit`).
- v2 tests added: DONE (Planning, Addon assembly, Scheduling, Example addon).
- ECS-first refactor slices (US4): DEFERRED (planned next iteration).
- Tracked exceptions: none.

## Verification evidence

If you can run Gradle locally/CI, use:

- `./gradlew preCommit`

If terminal output capture is not available in this IDE environment, attach evidence via report artifacts:

- Kover HTML report: `lko-sect/build/reports/kover/html/index.html`
- Detekt report: `lko-sect/build/reports/detekt/detekt.html` (path may vary by Gradle version)
- KtLint reports: `lko-sect/build/reports/ktlint/` (checkstyle/json)

> Environment note (Windows IDE): in this session, Gradle command output/log capture may be empty even when invoked.
> If you don't see output, run the commands in an external terminal or CI and then attach the generated report paths/files here.

> Additional note: In this IDE session, terminal command output capture appears to be unavailable (even for basic PowerShell output), so automated verification must be executed in an external terminal or CI and attached here.
