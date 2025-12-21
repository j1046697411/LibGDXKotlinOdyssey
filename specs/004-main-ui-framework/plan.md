# 实施计划: 主界面框架设计（1920×1080优化版）

**分支**: `004-main-ui-framework` | **日期**: 2025-12-21 | **规范**: [link](./spec.md)
**输入**: 来自 `/specs/004-main-ui-framework/spec.md` 的功能规范

**注意**: 此模板由 `/speckit.plan` 命令填充. 执行工作流程请参见 `.specify/templates/commands/plan.md`.

## 摘要

实现一个符合1920×1080分辨率优化的主界面框架，使用Compose技术栈。包括顶部导航栏、左侧菜单栏、中央内容区、右侧信息区和底部状态栏五个主要区域，实现清晰的视觉层次和友好的交互设计。

## 技术背景

**语言/版本**: Kotlin 1.9.20
**主要依赖**: Compose Multiplatform 1.6.0, LibGDX 1.12.1
**存储**: N/A
**测试**: JUnit 5, MockK
**目标平台**: Desktop (Windows, macOS, Linux)
**项目类型**: 单一项目，使用Kotlin Multiplatform架构
**性能目标**: 60 fps，长列表滚动流畅
**约束条件**: 1920×1080分辨率优化，针对160字符×60行分辨率优化布局
**规模/范围**: 1个主界面，包含5个主要区域，支持响应式设计

## 章程检查

*门控: 必须在阶段 0 研究前通过. 阶段 1 设计后重新检查. *

### 核心原则检查

✅ 多模块架构优先: 使用模块化设计，将主界面拆分为多个组件
✅ 质量门控强制执行: 确保代码通过ktlint、detekt等质量检查
✅ 测试优先开发: 为每个组件编写单元测试
✅ 代码覆盖率要求: 核心组件覆盖率达到60%以上
✅ 技术栈一致性: 使用统一的Kotlin Multiplatform和Compose技术栈

## 项目结构

### 文档(此功能)

```
specs/004-main-ui-framework/
├── plan.md              # 此文件 (/speckit.plan 命令输出)
├── research.md          # 阶段 0 输出 (/speckit.plan 命令)
├── data-model.md        # 阶段 1 输出 (/speckit.plan 命令)
├── quickstart.md        # 阶段 1 输出 (/speckit.plan 命令)
├── contracts/           # 阶段 1 输出 (/speckit.plan 命令)
└── tasks.md             # 阶段 2 输出 (/speckit.tasks 命令 - 非 /speckit.plan 创建)
```

### 源代码(仓库根目录)

```
lko-sect/
src/
├── commonMain/
│   ├── kotlin/
│   │   └── cn/
│   │       └── jzl/
│   │           └── sect/
│   │               ├── App.kt
│   │               ├── ui/
│   │               │   ├── MainUI.kt                # 主界面入口
│   │               │   ├── components/
│   │               │   │   ├── TopNavigationBar.kt   # 顶部导航栏组件
│   │               │   │   ├── LeftMenuBar.kt       # 左侧菜单栏组件
│   │               │   │   ├── CentralContentArea.kt # 中央内容区组件
│   │               │   │   ├── RightInfoArea.kt     # 右侧信息区组件
│   │               │   │   └── BottomStatusBar.kt   # 底部状态栏组件
│   │               │   └── screens/
│   │               │       └── TaskHallScreen.kt    # 任务大厅屏幕
│   │               └── data/
│   │                   ├── models/
│   │                   │   ├── MenuItem.kt          # 菜单项数据模型
│   │                   │   ├── Task.kt              # 任务数据模型
│   │                   │   └── Character.kt         # 角色数据模型
│   │                   └── mock/                    # 模拟数据
│   │                       └── MockData.kt
│   └── resources/
├── commonTest/
│   └── kotlin/
│       └── cn/
│           └── jzl/
│               └── sect/
│                   └── ui/
│                       └── components/
│                           ├── TopNavigationBarTest.kt
│                           ├── LeftMenuBarTest.kt
│                           ├── CentralContentAreaTest.kt
│                           ├── RightInfoAreaTest.kt
│                           └── BottomStatusBarTest.kt
└── desktopMain/
    └── kotlin/
        └── cn/
            └── jzl/
                └── sect/
                    ├── App.desktop.kt
                    └── Main.kt
```

**结构决策**: 采用单一项目结构，使用Kotlin Multiplatform架构，将UI组件拆分为独立模块，便于维护和测试。使用Compose实现所有UI组件，暂时不与流程逻辑对接，使用模拟数据填充界面。

## 复杂度跟踪

*仅在章程检查有必须证明的违规时填写*

| 违规 | 为什么需要 | 拒绝更简单替代方案的原因 |
|-----------|------------|-------------------------------------|
| 无 | 无 | 无 |