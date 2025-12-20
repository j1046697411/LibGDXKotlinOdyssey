# 📚 文档中心 - 宗门模拟游戏项目

> 项目治理、质量标准和开发指南的统一入口

---

## 🎯 我想要...

### 👨‍💻 我是新开发者，需要快速入门

**推荐阅读路径** (1 小时):

1. **5 min** - [`QUICK_REFERENCE.md`](QUICK_REFERENCE.md) ← **从这里开始!**
   - 四大核心原则速览
   - 快速命令
   - 常见错误

2. **15 min** - [`CONTRIBUTING.md`](CONTRIBUTING.md)
   - 开发环境设置
   - 工作流程 (6 步)
   - 常见问题 FAQ

3. **20 min** - [`docs/CODE_EXAMPLES.md`](docs/CODE_EXAMPLES.md)
   - ✓ 正确的代码
   - ✗ 错误的代码
   - 学会最佳实践

4. **20 min** - [`.specify/memory/constitution.md`](.specify/memory/constitution.md) (核心部分)
   - 详细规则
   - 检查清单

✅ 完成! 你现在可以开始编码了。

---

### 🔍 我想了解完整的项目规则

**推荐阅读** (1.5 小时):

1. [`.specify/memory/constitution.md`](.specify/memory/constitution.md) (45 min)
   - 核心原则的完整定义
   - 详细规则和检查清单
   - 技术栈一致性

2. [`GOVERNANCE.md`](GOVERNANCE.md) (45 min)
   - 工具链集成
   - 质量指标
   - 工作流程
   - 参考资源

3. [`docs/CODE_EXAMPLES.md`](docs/CODE_EXAMPLES.md) (20 min)
   - 应用示例

✅ 现在你理解了完整的项目规则。

---

### 👀 我是代码审查者，需要检查标准

**快速检查清单**:

1. **参考**: [`QUICK_REFERENCE.md`](QUICK_REFERENCE.md) - 代码审查 Checklist
2. **规则**: [`.specify/memory/constitution.md`](.specify/memory/constitution.md) - 核心原则
3. **示例**: [`CODE_EXAMPLES.md`](docs/CODE_EXAMPLES.md) - 对比示例 (✓ vs ✗)
4. **工具**: [`GOVERNANCE.md`](GOVERNANCE.md) - 工具链 & 阈值

---

### 🚀 我想配置开发环境

**步骤**:

1. 阅读: [`CONTRIBUTING.md`](CONTRIBUTING.md) - "开发环境设置" 部分
2. 运行: `./gradlew preCommit` 验证
3. 检查: IDE 是否安装了 Kotlin + ktlint + detekt 插件
4. 参考: [`GOVERNANCE.md`](GOVERNANCE.md) - IDE 配置部分

---

### 🏗️ 我是架构师，需要了解项目结构

**推荐阅读** (1 小时):

1. [`.specify/memory/constitution.md`](.specify/memory/constitution.md)
   - 核心原则
   - 工作流程与检查点

2. `specs/` 下对应 feature 的 `plan.md`
   - 项目结构
   - 技术决策

3. [`GOVERNANCE.md`](GOVERNANCE.md)
   - 模块化规范
   - 工具链

---

### 📊 我是项目经理，需要了解质量指标

**关键文档**:

1. [`GOVERNANCE.md`](GOVERNANCE.md) - "质量指标" 部分
   - 编译、测试、代码质量、性能 4 个维度
   - 工具及其失败后果

2. `.specify/memory/constitution.md`
   - 测试纪律 - 覆盖率目标
   - 性能预算 - 性能目标

3. [`QUICK_REFERENCE.md`](QUICK_REFERENCE.md)
   - 质量门槛总结

---

### 🔧 我想配置 CI/CD

**文件**:

1. [`.github/workflows/ci.yml`](.github/workflows/ci.yml) (100+ 行)
   - GitHub Actions 配置
   - 自动运行的检查

2. [`quality.gradle.kts`](quality.gradle.kts) (150+ 行)
   - Gradle 集成
   - `preCommit` & `qualityCheck` 任务

3. [`detekt.yml`](detekt.yml) (450+ 行)
   - Detekt 规则配置
   - 启用/禁用规则

---

### 🐛 我遇到问题

**查找解决方案**:

| 问题类型 | 查看文件 |
|---------|--------|
| 编码风格问题 | `QUICK_REFERENCE.md` "常见错误" + `CODE_EXAMPLES.md` |
| 测试问题 | `constitution.md` 1.2 + `CODE_EXAMPLES.md` 4 |
| 工作流程问题 | `CONTRIBUTING.md` 常见问题 |
| 工具问题 | `GOVERNANCE.md` 常见问题 |
| 性能问题 | `constitution.md` 1.4 + `CODE_EXAMPLES.md` 5 |

---

## 📑 完整文档导航

### 📋 治理与原则

| 文件 | 行数 | 用途 | 用时 |
|------|------|------|------|
| **constitution.md** | 650+ | 项目宪法 - 4 大原则 + 规则 | 45 min |
| **plan.md** | 139 | 实施计划 - 技术栈 + 结构 | 20 min |
| **GOVERNANCE.md** | 500+ | 完整指南 - 工具 + 工作流 | 45 min |

### 📖 指南与示例

| 文件 | 行数 | 用途 | 用时 |
|------|------|------|------|
| **CONTRIBUTING.md** | 350+ | 贡献指南 - 新人快速入门 | 20 min |
| **CODE_EXAMPLES.md** | 400+ | 代码示例 - ✓ vs ✗ 对比 | 20 min |
| **QUICK_REFERENCE.md** | 150+ | 快速参考 - 可打印卡片 | 5 min |

### 🔧 工具配置

| 文件 | 行数 | 用途 |
|------|------|------|
| **.github/workflows/ci.yml** | 100+ | GitHub Actions CI/CD |
| **quality.gradle.kts** | 150+ | Gradle 质量集成 |
| **detekt.yml** | 450+ | Detekt 分析规则 |

### 📊 实施与总结

| 文件 | 位置 | 用途 |
|------|------|------|
| **CONSTITUTION_IMPLEMENTATION.md** | specs/003-sect-simulation-game/ | 实施总结 + 检查清单 |
| **INDEX.md** | 仓库根目录 (本文件) | 文档导航中心 |

---

## 🎯 文档关系图

```
📋 constitution.md (宪法 - 核心)
├─ 1. 卓越代码质量
│  └─→ CODE_EXAMPLES.md (示例)
├─ 2. 严格测试标准
│  └─→ CODE_EXAMPLES.md (示例)
├─ 3. UX 一致性
│  └─→ CODE_EXAMPLES.md (示例)
└─ 4. 高性能要求
   └─→ CODE_EXAMPLES.md (示例)

📖 CONTRIBUTING.md (新人指南)
├─→ constitution.md (详细规则)
└─→ GOVERNANCE.md (工具链)

🔧 GOVERNANCE.md (完整指南)
├─→ constitution.md (规则参考)
├─→ .github/workflows/ci.yml (CI/CD)
├─→ quality.gradle.kts (构建)
└─→ detekt.yml (分析)

🎓 QUICK_REFERENCE.md (快速参考)
└─→ 所有文件的概览

📊 CONSTITUTION_IMPLEMENTATION.md (实施总结)
└─→ 所有新文件的清单 + 后续步骤
```

---

## ⏱️ 阅读时间估计

| 角色 | 推荐路径 | 总用时 |
|------|--------|-------|
| 👨‍💻 新开发者 | QUICK_REFERENCE → CONTRIBUTING → CODE_EXAMPLES → constitution | 1 h |
| 👀 Code Reviewer | QUICK_REFERENCE → constitution 1.1-1.2 → CODE_EXAMPLES | 45 min |
| 🏗️ 架构师 | constitution → plan → GOVERNANCE 5 | 1 h |
| 👨‍💼 项目经理 | GOVERNANCE 2-3 → constitution 1.2/1.4 | 30 min |
| 🚀 DevOps | ci.yml → quality.gradle.kts → detekt.yml | 30 min |
| 🎓 学习完整体系 | 按上述顺序全读 | 2.5 h |

---

## ✅ 实施检查清单

### 第一周 (团队启动)

- [ ] 所有开发者阅读 `QUICK_REFERENCE.md`
- [ ] 所有开发者阅读 `CONTRIBUTING.md`
- [ ] 所有开发者运行 `./gradlew preCommit`
- [ ] 项目经理阅读 `GOVERNANCE.md` 质量指标部分
- [ ] DevOps 部署 CI/CD 和质量检查

### 第二周 (知识巩固)

- [ ] 所有开发者阅读 `constitution.md` 完整版
- [ ] Code Review 团队阅读 `CODE_EXAMPLES.md`
- [ ] 团队讨论: 规则是否合理，是否需要调整
- [ ] 第一个 PR 提交并通过新工作流程

### 第三周 (文化建立)

- [ ] 所有 PR 遵循新规则
- [ ] CI/CD 正常运行，没有卡顿
- [ ] 收集开发者反馈，记录问题
- [ ] 制定改进计划 (如需)

---

## 🔄 定期回顾

### 月度检查

1. 查看质量指标趋势
2. 回顾测试覆盖率
3. 分析 detekt 警告类型
4. 讨论是否需要调整规则

### 季度检查

1. 性能基准回顾
2. 团队反馈收集
3. 规则更新
4. 工具升级

---

## 🆘 获取帮助

### 问题类型 & 查询方式

| 问题 | 查找方式 |
|------|--------|
| "如何编写代码?" | QUICK_REFERENCE + CODE_EXAMPLES |
| "规则是什么?" | constitution.md |
| "怎样提交 PR?" | CONTRIBUTING.md 工作流程 |
| "工具怎样配置?" | GOVERNANCE.md 工具链 |
| "为什么规则这样定?" | constitution.md 核心原则说明 |
| "有代码示例吗?" | CODE_EXAMPLES.md |

### 联系方式

- 💬 **讨论**: GitHub Discussions
- 🐛 **问题**: GitHub Issues
- 👥 **团队**: 项目 Slack/Discord 频道
- 📧 **反馈**: 向项目维护者提交意见

---

## 📚 相关资源

### 外部参考

- **Kotlin 编码规范**: https://kotlinlang.org/docs/coding-conventions.html
- **Conventional Commits**: https://www.conventionalcommits.org/
- **Clean Code**: "Clean Code" 书籍 by Robert C. Martin
- **Refactoring**: "Refactoring" 书籍 by Martin Fowler
- **Performance**: JVM Performance 最佳实践

### 项目内资源

- 📁 ECS 框架: `lko-libs/lko-ecs4/`
- 📁 DI 框架: `lko-libs/lko-di/`
- 📁 项目规范: `specs/003-sect-simulation-game/`
- 📁 代码: `lko-sect/src/`

---

## 🎉 你准备好了吗?

### 检查清单

- [ ] 你已经阅读了 `QUICK_REFERENCE.md`
- [ ] 你已经阅读了 `CONTRIBUTING.md`
- [ ] 你已经运行了 `./gradlew preCommit`
- [ ] 你已经配置了 IDE
- [ ] 你已经创建了 feature 分支

✅ 完成? **现在你可以开始编码了!** 🚀

---

**最后更新**: 2025-12-13  
**项目**: 宗门模拟游戏  
**维护者**: 项目团队

