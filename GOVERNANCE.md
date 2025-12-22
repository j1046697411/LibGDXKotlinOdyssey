# 宗门模拟游戏 - 项目治理与质量标准

## 概述

本项目遵循严格的代码质量、测试标准、用户体验一致性和性能要求。本文件为治理和质量相关文档的入口。

## 📋 核心文档

### 项目宪法 (Constitution)
**位置**: `.specify/memory/constitution.md`

定义项目的核心原则:
1. **ECS-first 架构**
2. **服务复用优先**
3. **框架一致性 (Addon + DI)**
4. **质量门禁 (ktlint/detekt, tests=100%)**
5. **测试覆盖率目标 (≥80%)**
6. **性能预算 (60 FPS)**

👉 **所有开发者必读**

### 项目计划 (Plan)
**位置**: 参见 `specs/` 下对应 feature 的 `plan.md`

### 任务清单 (Tasks)
**位置**: `specs/` 下对应 feature 的 `tasks.md`

---

## 🚀 快速开始

### 开发环境

```bash
# 克隆项目
git clone https://github.com/<org>/LibGDXKotlinOdyssey.git
cd LibGDXKotlinOdyssey

# 构建并运行质量检查
./gradlew preCommit

# 运行所有测试
./gradlew :lko-sect:test
```

### 检查清单：开始开发前

- [ ] 阅读 `.specify/memory/constitution.md`
- [ ] 阅读 [`CONTRIBUTING.md`](CONTRIBUTING.md)
- [ ] 配置 IDE (Kotlin 插件、代码风格)
- [ ] 运行 `./gradlew preCommit` 验证环境
- [ ] 创建 feature 分支

---

## 📊 质量指标

### 编译

| 检查 | 阈值 | 状态 |
|------|------|------|
| 编译错误 | 0 | 🔴 阻止合并 |
| 编译警告 | ≤ 5 | ⚠️ 需要审查 |

### 测试

| 检查 | 阈值 | 状态 |
|------|------|------|
| 单元测试通过率 | 100% | 🔴 阻止合并 |
| 集成测试通过率 | 100% | 🔴 阻止合并 |
| 代码覆盖率 | ≥ 75% | ⚠️ 需要审查 |

### 代码质量

| 检查 | 工具 | 状态 |
|------|------|------|
| 代码风格 | ktlint | ⚠️ 自动修复 |
| 静态分析 | detekt | ⚠️ 需要审查 |
| 复杂度 | detekt | ⚠️ 需要重构 |

### 性能

| 指标 | 目标 | 监测 |
|------|------|------|
| 帧率 | ≥ 60 FPS | 基准测试 |
| 单系统更新 | ≤ 5ms | 性能分析 |
| 内存占用 | ≤ 500MB | 堆快照 |

---

## 🛠️ 工具链集成

### 构建工具

```bash
# 完整构建
./gradlew :lko-sect:build

# 代码格式化
./gradlew ktlintFormat

# 静态分析
./gradlew detekt

# 单元测试
./gradlew :lko-sect:test

# 覆盖率报告
./gradlew jacocoTestReport

# 性能基准 (如配置)
./gradlew jmh
```

### IDE 集成

**IntelliJ IDEA**:
- Plugins: Kotlin, Detekt, ktlint
- Code Style: 启用 Kotlin formatting
- Inspections: 启用 Kotlin 和自定义规则

**GitLab/GitHub CI/CD**:
- 自动运行: build, test, coverage, lint, detekt
- 阻止合并: 如果任何检查失败

---

## 📖 开发指南

### 代码风格

遵循 [Kotlin 官方编码规范](https://kotlinlang.org/docs/coding-conventions.html):
- 最大行长: 120 字符
- 缩进: 4 空格
- 命名: PascalCase (类), camelCase (函数), UPPER_SNAKE_CASE (常量)

### 命名规范

| 元素 | 规范 | 示例 |
|------|------|------|
| 类 | PascalCase | `SectSystem`, `FacilityBuilder` |
| 函数 | camelCase | `validateConstruction`, `updateResources` |
| 常量 | UPPER_SNAKE_CASE | `DEFAULT_MAX_DISCIPLES`, `MIN_CONSTRUCTION_COST` |
| 私有成员 | 前缀 `_` | `_internalCache`, `_logger` |
| 布尔值 | is/has/can | `isActive`, `hasDisciples`, `canPerform` |

### 注释规范

所有 public API 必须有 KDoc:

```text
/**
 * Brief description (1 line).
 *
 * Detailed description of what this function does,
 * including any important behavior or constraints.
 *
 * @param param1 description
 * @param param2 description
 * @return description
 * @throws ExceptionType when this condition occurs
 *
 * @see RelatedClass
 * @since 1.0.0
 */
fun publicFunction(param1: String, param2: Int): Boolean { ... }
```

### 模块化规范

每个功能模块遵循 ECS Addon 模式:

```
cn/jzl/sect/ecs/{module}/
├── {Module}Components.kt    # 数据组件定义
├── {Module}System.kt        # 业务逻辑系统
├── {Module}Addon.kt         # Addon 注册
└── {Module}Test.kt          # 单元测试
```

---

## 🧪 测试标准

### 测试命名

遵循 "test[Function]When[Condition]Expect[Result]" 模式:

```text
@Test
fun testValidateConstructionWhenInsufficientFundsExpectFailure() { ... }

@Test
fun testAssignTasksWhenMultipleDisciplesIdleExpectRoundRobin() { ... }
```

### 测试结构 (AAA 模式)

```kotlin
@Test
fun testSomethingWhenConditionExpectResult() {
    // Arrange - 准备测试数据和环境
    val resource = createTestResource()
    
    // Act - 执行被测试的代码
    val result = resource.process()
    
    // Assert - 验证结果
    assertEquals(expected, result)
}
```

### 覆盖率目标

| 类型 | 目标 |
|------|------|
| 核心业务逻辑 | ≥ 90% |
| 系统集成 | ≥ 80% |
| UI 组件 | ≥ 60% |

---

## 📈 性能要求

### 性能目标

| 指标 | 目标 | 说明 |
|------|------|------|
| 帧率 | ≥ 60 FPS | 主循环 |
| 系统更新 | ≤ 5ms | 单个系统 per frame |
| 查询响应 | ≤ 1ms | 常见 ECS 查询 |
| 内存占用 | ≤ 500MB | 启动后稳定 |
| GC 暂停 | ≤ 10ms | 最大暂停时间 |
| GC 频率 | ≤ 1/s | 每秒 GC 次数 |

### 性能优化指南

1. **ECS 优化**
   - 使用 ArchetypeService 按组件分组
   - 系统仅查询必需的组件
   - 批量操作优于逐个

2. **资源管理**
   - 对象池化频繁创建/销毁的对象
   - 懒初始化非关键资源
   - 及时释放完成使用的资源

3. **算法选择**
   - 任务分配: O(n log n) 排序，避免 O(n²)
   - 查询: O(1) 或 O(log n)，避免 O(n) 扫描
   - 路径计算: 预计算或异步执行

---

## 📝 工作流程

### 开发步骤

1. **分支创建** - 从 develop 创建 feature 分支
2. **TDD 开发** - 先写测试，后写实现
3. **本地检查** - `./gradlew preCommit`
4. **提交代码** - 遵循 Conventional Commits
5. **创建 PR** - 在 GitHub 提交 Pull Request
6. **代码审查** - 至少 1 个 reviewer 批准
7. **CI/CD 验证** - 自动运行所有检查
8. **合并** - reviewer 批准且 CI 通过后合并

### 提交规范

使用 [Conventional Commits](https://www.conventionalcommits.org/):

```
feat(us1): implement sect construction validation
fix(ai): correct task recognition algorithm
docs: update architecture guide
style: format code with ktlint
refactor: extract construction logic
perf: optimize task assignment
test: add integration test for sect flow
chore: update gradle dependencies
```

---

## 🚨 常见问题

### Q: 我的 PR 被拒了，怎么办?
A: 
1. 阅读 reviewer 反馈
2. 在同一分支修复问题
3. 提交新 commit
4. 等待 re-review

### Q: 代码覆盖率不够?
A:
1. 检查 `build/reports/jacoco/test/html/index.html`
2. 为未覆盖的代码编写测试
3. 重新生成报告

### Q: detekt 告警太多?
A:
1. 查看 `build/reports/detekt/detekt.html`
2. 要么修复代码设计，要么标记 `@Suppress`
3. 添加注释解释为什么需要压制

### Q: 性能基准失败?
A:
1. 检查 `build/reports/jmh/` (如有)
2. 分析瓶颈 (CPU profiler, 堆快照)
3. 优化算法或数据结构
4. 重新基准测试

---

## 📚 参考资源

- **Kotlin 编码规范**: https://kotlinlang.org/docs/coding-conventions.html
- **ECS 框架**: `lko-libs/lko-ecs4/` (项目内)
- **CI/CD 配置**: `.github/workflows/ci.yml`
- **项目计划**: 参见 `specs/` 下对应 feature 的 `plan.md`
- **代码示例**: `docs/CODE_EXAMPLES.md`

---

## 📞 获取帮助

- **Issue**: GitHub Issues (报告 bug 或提功能需求)
- **Discussion**: GitHub Discussions (设计讨论)
- **文档**: 查看项目 docs 和 specs 文件夹
- **团队**: 联系项目维护者

---

## ✅ 检查清单：第一次贡献

- [ ] 阅读 `.specify/memory/constitution.md`
- [ ] 阅读 [`CONTRIBUTING.md`](CONTRIBUTING.md)
- [ ] 阅读 [`CODE_EXAMPLES.md`](docs/CODE_EXAMPLES.md)
- [ ] 本地运行 `./gradlew preCommit`
- [ ] 创建 feature 分支
- [ ] 遵循 TDD 开发
- [ ] 提交 PR 前再次运行质量检查
- [ ] 提交 PR 并请求审查

---

**感谢你的贡献！让我们一起构建高质量的宗门模拟游戏。** 🎮
