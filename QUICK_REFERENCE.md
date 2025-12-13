# 宗门模拟游戏 - 快速参考卡片

> 打印这个卡片，放在你的工作区！

---

## 🎯 四大核心原则

### 1️⃣ 卓越代码质量
- 行长 ≤ 120 字符
- 命名: PascalCase (类) / camelCase (函数) / UPPER_SNAKE_CASE (常量)
- Public API 必须有 KDoc
- 最大环复杂度 ≤ 10

### 2️⃣ 严格测试标准  
- 覆盖率: 核心 ≥90%, 系统 ≥80%
- 框架: JUnit5 + MockK
- 模式: AAA (Arrange-Act-Assert)
- 命名: `test[Func]When[Cond]Expect[Result]`

### 3️⃣ 用户体验一致性
- 统一信息架构
- 命令: `[1] 操作`, `[0] 返回`
- 状态: ✓/✗/⚠ 
- 快捷键: 数字、q(quit)、h(help)

### 4️⃣ 高性能要求
- 帧率: ≥ 60 FPS
- 系统更新: ≤ 5ms
- 内存: ≤ 500MB
- 查询: ≤ 1ms

---

## ⚡ 快速命令

```bash
# 开发者必用
./gradlew preCommit              # ⭐ 提交前必运行!

# 完整检查
./gradlew qualityCheck           # 代码 + 测试 + 覆盖 + 分析

# 单项检查
./gradlew ktlintFormat           # 自动格式化
./gradlew detekt                 # 静态分析
./gradlew :lko-sect:test         # 运行测试
./gradlew jacocoTestReport       # 覆盖率报告
```

---

## 📝 代码范例

### ✓ 正确的方法

```kotlin
/**
 * Validates construction against sect resources.
 * @param resources current sect resources
 * @return Result.success if valid
 */
fun validateConstruction(resources: SectResources): Result<Unit> {
    if (resources.funds < 100) {
        return Result.failure(Exception("Insufficient funds"))
    }
    return Result.success(Unit)
}

// 测试
@Test
fun testValidateWhenInsufficientFundsExpectFailure() {
    // Arrange
    val resources = SectResources(funds = 50)
    // Act
    val result = validateConstruction(resources)
    // Assert
    assertFalse(result.isSuccess)
}
```

### ✗ 错误的方法

```kotlin
// ✗ 无文档、变量名模糊
fun validate(s: SectResources): Boolean {
    return s.f > 100  // 什么是 f?
}

// ✗ catch 中什么都不做
try {
    validate(resources)
} catch (e: Exception) {
    // ✗ 忽略异常!
}
```

---

## 📋 提交规范

### Conventional Commits 格式

```
<type>(<scope>): <subject>
```

**Type**:
- `feat` - 新功能
- `fix` - 修复 bug
- `docs` - 文档
- `style` - 代码风格
- `refactor` - 重构
- `perf` - 性能优化
- `test` - 测试相关
- `chore` - 配置、依赖

**Examples**:
```bash
git commit -m "feat(us1): implement sect construction validation"
git commit -m "fix(ai): correct task recognition algorithm"
git commit -m "docs: update architecture guide"
```

---

## 🧪 测试检查清单

- [ ] 测试命名: `test[Func]When[Cond]Expect[Result]`
- [ ] 结构: Arrange → Act → Assert
- [ ] 无硬编码数据 (使用 builder/fixture)
- [ ] 每个测试只测一个行为
- [ ] 快速运行: < 100ms 单个, < 5s 全部
- [ ] 无 @Ignore 跳过的测试

---

## 🔍 代码审查 Checklist

对他人代码审查时:

- [ ] 命名清晰？ (PascalCase/camelCase)
- [ ] Public API 有文档？ (KDoc)
- [ ] 复杂度 ≤ 10？ (检查 detekt 报告)
- [ ] 有对应测试？ (≥ 75% 覆盖率)
- [ ] 错误处理显式？ (Result<T> 或 try-catch)
- [ ] 无循环依赖？ (通过 ECS Addon)
- [ ] 性能合理？ (无 O(n²) 循环)

---

## 📚 文档导航

| 你需要... | 查看文件 | 用时 |
|---------|--------|------|
| 快速入门 | CONTRIBUTING.md | 15 min |
| 完整规则 | constitution.md | 45 min |
| 代码示例 | docs/CODE_EXAMPLES.md | 20 min |
| 工具配置 | GOVERNANCE.md | 30 min |
| 这个卡片 | QUICK_REFERENCE.md | 3 min |

---

## ⚠️ 常见错误

| ❌ 错误 | ✓ 正确 | 影响 |
|--------|-------|------|
| 无测试 | 写测试 | 🔴 阻止合并 |
| 无文档 | 加 KDoc | ⚠️ 需审查 |
| 复杂度 > 10 | 拆分函数 | ⚠️ 需审查 |
| catch 啥都不做 | 处理异常 | ⚠️ 需审查 |
| 无快捷键 | 支持快捷键 | ⚠️ 需审查 |

---

## 🚨 质量门槛

### 🔴 必须通过 (否则阻止合并)
- [ ] 编译: 0 errors
- [ ] 单元测试: 100% pass
- [ ] 集成测试: 100% pass

### ⚠️ 需要审查 (可协商)
- [ ] 覆盖率 ≥ 75%
- [ ] detekt 警告 < 5 个
- [ ] 性能 ≤ 10% 下降

---

## 💡 提示

1. **提交前运行**: `./gradlew preCommit`
2. **本地 IDE 检查**: 安装 Kotlin + ktlint + detekt 插件
3. **遇到问题**: 查看 GOVERNANCE.md FAQ
4. **代码参考**: 看 CODE_EXAMPLES.md 的 ✓ 示例
5. **团队讨论**: 在 GitHub Discussions 中讨论规则改进

---

## 📞 快速链接

- 📋 **Constitution**: `specs/003-sect-simulation-game/constitution.md`
- 📖 **Contributing**: `CONTRIBUTING.md`  
- 🎯 **Governance**: `GOVERNANCE.md`
- 💡 **Code Examples**: `docs/CODE_EXAMPLES.md`
- 🔧 **CI/CD**: `.github/workflows/ci.yml`

---

**打印并贴在你的显示器上! 📌**

