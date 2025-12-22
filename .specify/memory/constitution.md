# LibGDXKotlinOdyssey 项目章程

## 核心原则

### I. 多模块架构优先
每个功能都从独立的模块开始; 模块必须是自包含的、可独立测试的、有文档的; 需要明确的目的 - 不允许仅用于组织的模块

### II. 质量门控强制执行
所有代码必须通过质量门控检查，包括ktlint、detekt、测试和覆盖率检查; 质量门控在本地和CI环境保持一致

### III. 测试优先开发
所有功能必须有相应的测试; 严格执行测试驱动开发流程; 测试必须覆盖核心业务逻辑

### IV. 代码覆盖率要求
cn.jzl.sect.v2.* 包的代码覆盖率必须达到60%以上; 其他模块也应保持合理的覆盖率

### V. 技术栈一致性
统一使用Kotlin Multiplatform、Compose Multiplatform等技术栈; 遵循模块约定插件(cn.jzl.sect-module-conventions)的规范

## 附加约束

### 技术栈要求
- Kotlin Multiplatform作为主要开发语言
- Compose Multiplatform用于UI开发
- ECS(实体组件系统)架构用于核心功能
- JUnit Platform用于测试

### 构建配置
- 使用Gradle Kotlin DSL进行构建配置
- 质量门控通过根构建脚本配置
- 不直接应用quality.gradle.kts脚本

## 开发工作流程

### 本地开发
- 使用 `./gradlew preCommit` 进行全项目质量检查
- 对于SECT v2模块，使用快速迭代命令: `./gradlew :lko-sect:check`, `./gradlew :lko-sect:ktlintCheck :lko-sect:detekt`, `./gradlew :lko-sect:test`, `./gradlew :lko-sect:koverHtmlReport :lko-sect:koverVerify`

### CI/CD
- 所有PR必须通过CI环境的质量门控
- 代码合并前必须经过代码审查

## 治理

### 章程优先
章程优先于所有其他实践; 任何与章程冲突的实践都必须进行调整

### 修正流程
章程的修正需要文档化、批准和迁移计划; 修正后必须更新版本号

### 版本控制
遵循语义化版本控制规则:
- MAJOR(主版本): 向后不兼容的治理/原则删除或重新定义
- MINOR(次版本): 新原则/部分添加或实质性扩展指导
- PATCH(补丁版本): 澄清、措辞、拼写错误修复、非语义优化

**版本**: 1.0.0 | **批准日期**: 2025-12-21 | **最后修正**: 2025-12-21
