# 宗门模拟游戏 - 贡献指南

欢迎贡献宗门模拟游戏项目！本指南说明如何遵循项目的代码质量标准、测试要求和工作流程。

## 目录

1. [开发环境设置](#开发环境设置)
2. [项目规则 (宪法)](#项目规则-宪法)
3. [开发工作流程](#开发工作流程)
4. [代码质量检查](#代码质量检查)
5. [测试要求](#测试要求)
6. [提交规范](#提交规范)
7. [常见问题](#常见问题)

---

## 开发环境设置

### 前置条件

- **JDK 17+**: 下载 [Eclipse Temurin](https://adoptium.net/)
- **IDE**: IntelliJ IDEA 2023.2+ (社区版或专业版)
- **Git**: 配置用户信息
- **Kotlin Plugin**: 确保 IDEA 已安装最新 Kotlin 插件

### 项目克隆与构建

```bash
# 克隆项目
git clone https://github.com/<org>/LibGDXKotlinOdyssey.git
cd LibGDXKotlinOdyssey

# 构建项目
./gradlew :lko-sect:build

# 运行单元测试
./gradlew :lko-sect:test

# 运行所有质量检查 (推荐在提交前)
./gradlew preCommit
```

---

## 项目规则 (宪法)

项目的核心原则定义在 [`specs/003-sect-simulation-game/constitution.md`](specs/003-sect-simulation-game/constitution.md) 中。

### 关键原则

1. **卓越代码质量** — 遵循 Kotlin 最佳实践、清晰命名、充分注释
2. **严格测试标准** — ≥80% 覆盖率、单元/集成/性能测试齐全
3. **用户体验一致性** — 统一界面设计、清晰交互流程
4. **高性能要求** — 60 FPS、低延迟、≤500MB 内存占用

详细规则请阅读 [`constitution.md`](specs/003-sect-simulation-game/constitution.md)。

---

## 开发工作流程

### 1. 创建 Feature 分支

```bash
# 从 develop 分支创建新分支
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name
```

命名约定: `feature/<user-story>-<description>` (例: `feature/us1-sect-construction`)

### 2. 开发 & 测试 (TDD)

按照 Test-Driven Development 流程:

1. **编写测试** — 定义行为预期
2. **运行测试** — 验证测试失败
3. **实现功能** — 编写最小化实现
4. **运行测试** — 验证测试通过
5. **重构** — 改进代码质量

### 3. 本地质量检查

在提交前运行完整的质量检查:

```bash
# 自动格式化代码 + 运行所有检查
./gradlew preCommit

# 或者分步检查:
./gradlew ktlintFormat          # 自动修复代码风格
./gradlew detekt                # 静态分析
./gradlew test                  # 单元测试
./gradlew jacocoTestReport      # 覆盖率报告
```

### 4. 提交代码

```bash
# 查看变更
git status
git diff

# 暂存变更 (推荐逐个文件暂存)
git add <file1> <file2>

# 提交 (遵循 Conventional Commits)
git commit -m "feat(us1): implement sect construction validation"

# 推送到远程
git push origin feature/your-feature-name
```

### 5. 创建 Pull Request

在 GitHub 上创建 PR:

- **标题**: 遵循 Conventional Commits (见下文)
- **描述**: 说明改动内容、测试覆盖、性能影响
- **关联 Issue**: 如有对应的 GitHub Issue 需关联
- **检查清单**: 确保通过所有本地检查

### 6. 代码审查 & 合并

- 等待至少 1 个 reviewer 批准
- 确保 CI/CD pipeline 全部通过 (build, test, coverage, style)
- reviewer 批准后可以 squash merge

---

## 代码质量检查

### 代码风格 (ktlint)

遵循官方 Kotlin 编码规范。工具会自动检查和修复:

```bash
./gradlew ktlintCheck       # 检查
./gradlew ktlintFormat      # 自动修复
```

### 静态分析 (Detekt)

Detekt 检查代码中的设计问题、复杂度过高等问题:

```bash
./gradlew detekt
# 报告位置: build/reports/detekt/detekt.html
```

常见告警及解决:

| 告警 | 解决方案 |
|-----|--------|
| `CyclomaticComplexMethod` | 函数复杂度 > 10，拆分成多个函数 |
| `LongMethod` | 函数 > 60 行，提取功能到新函数 |
| `LongParameterList` | 参数 > 6 个，使用数据类封装 |
| `TooManyFunctions` | 类 > 11 个函数，拆分为多个类 |

### 代码覆盖率 (JaCoCo)

生成并检查测试覆盖率:

```bash
./gradlew jacocoTestReport
# 报告位置: build/reports/jacoco/test/html/index.html
```

最小要求:
- 核心业务逻辑: ≥ 90%
- 系统集成: ≥ 80%

---

## 测试要求

### 单元测试

每个新功能必须有对应的单元测试:

```kotlin
// 命名: test[Function]When[Condition]Expect[Result]
@Test
fun testValidateConstructionWhenInsufficientFundsExpectFailure() {
    // Arrange
    val resources = SectResources(funds = 50)
    val request = ConstructionRequest(type = "MAIN_HALL")

    // Act
    val result = SectConstruction.validateConstruction(resources, request)

    // Assert
    assertFalse(result.isSuccess)
}
```

### 集成测试

验证多个系统/模块协作:

```kotlin
// 放在 IntegrationTest 源集或使用 IntegrationTest 前缀
class SectConstructionIntegrationTest {
    @Test
    fun testSectConstructionCompleteFlow() {
        // 测试从命令下达到设施建成的完整流程
    }
}
```

### 性能测试 (可选)

关键路径需要性能基准:

```bash
./gradlew jmh
```

---

## 提交规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

- **feat**: 新功能
- **fix**: 修复 bug
- **docs**: 文档更新
- **style**: 代码风格修改 (格式、缩进等)
- **refactor**: 代码重构 (无功能改变)
- **perf**: 性能优化
- **test**: 测试相关
- **chore**: 构建配置、依赖更新等

### Scope 范围

- 指与改动相关的模块/故事 (例: `us1`, `sect`, `ai`)
- 可选，但推荐使用

### Subject 主题

- 命令式语气 (不用 "Added"，改用 "Add")
- 不以句点结尾
- 长度 ≤ 50 字符

### Body 正文

- 详细说明改动原因和内容 (可选)
- 每行 ≤ 72 字符

### Footer 页脚

- 关联 Issue (例: `Closes #123`, `Refs #456`)
- Breaking Change 声明

### 示例

```
feat(us1): implement sect construction validation

Add resource validation before construction is approved. 
If resources are insufficient, a gather task is automatically generated.

- Validate funds and materials
- Generate gather tasks when shortage detected
- Return Result<Unit> for error handling

Closes #45
```

---

## 常见问题

### Q: 我在编码时应该如何组织项目结构?

A: 遵循 ECS Addon 模式。每个功能模块包含:
- `*Components.kt` — 数据组件定义
- `*System.kt` — 系统实现
- `*Addon.kt` — 模块注册
- `*Test.kt` — 测试

### Q: 我的代码审查被拒了，怎么处理?

A: 
1. 阅读 reviewer 的评论和建议
2. 修复问题后在同一分支上提交新 commit
3. 回复 reviewer 确认修复
4. 等待 re-review

### Q: 如何处理代码中的 TODO/FIXME?

A: 所有 TODO 必须追踪:
- 格式: `// TODO(name): description (Issue #123)`
- 提交前移除或关联到 GitHub Issue

### Q: 测试覆盖率不够怎么办?

A: 
1. 检查 coverage report 找到未覆盖代码
2. 针对这些代码编写单元或集成测试
3. 运行 `./gradlew jacocoTestReport` 重新生成报告

### Q: 能跳过某些检查吗?

A: **不能**。所有检查都是必需项:
- ktlint: 自动修复 (`./gradlew ktlintFormat`)
- detekt: 修复代码设计问题或标记异常 (`@Suppress`)
- test: 编写测试用例
- coverage: 增加测试

---

## 获取帮助

- **Issue**: 在 GitHub Issues 中提问或报告问题
- **Discussion**: 在 GitHub Discussions 中讨论设计
- **Slack/Discord**: 加入项目开发频道 (如有)
- **文档**: 查看 [`constitution.md`](specs/003-sect-simulation-game/constitution.md) 和 [`plan.md`](specs/003-sect-simulation-game/plan.md)

---

感谢你的贡献！🎉

