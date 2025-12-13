# 快速开始指南: 宗门系统完善

**功能分支**: `001-sect-system-enhancement`
**创建时间**: 2025-12-13

## 概述

本指南帮助开发者快速理解和使用宗门系统的新功能。

---

## 前置条件

确保已安装以下 Addon：

```kotlin
val world = World {
    install(coreAddon)
    install(levelingAddon)
    install(moneyAddon)
    install(characterAddon)
    install(itemAddon)
    install(inventoryAddon)
    install(countdownAddon)
    install(sectAddon)           // 包含所有宗门相关 Addon
}
```

---

## 快速示例

### 1. 创建宗门和成员

```kotlin
// 获取服务
val sectService by world.di.instance<SectService>()
val characterService by world.di.instance<CharacterService>()

// 创建角色
val leader = characterService.createCharacter(Named("张三")) {
    it.addComponent(Money(1000))
}

val disciple1 = characterService.createCharacter(Named("李四")) {
    it.addComponent(Money(100))
}

// 创建宗门（张三为宗主）
val sect = sectService.createSect(Named("青云宗"), leader) {
    it.addComponent(MemberQuota(maxElders = 5, maxInnerDisciples = 20))
}

// 添加成员
sectService.addMember(sect, disciple1, MemberRole.OUTER_DISCIPLE)
```

### 2. 管理宗门资源

```kotlin
val sectResourceService by world.di.instance<SectResourceService>()
val itemService by world.di.instance<ItemService>()

// 获取资源预制体
val lingshi = itemService["灵石"]!!
val lingcao = itemService["低级灵草"]!!

// 存入资源
sectResourceService.deposit(sect, lingshi, 10000)
sectResourceService.deposit(sect, lingcao, 500)

// 查询资源
println("灵石: ${sectResourceService.getResourceAmount(sect, lingshi)}")  // 10000
println("灵草: ${sectResourceService.getResourceAmount(sect, lingcao)}")  // 500

// 取出资源
sectResourceService.withdraw(sect, lingshi, 1000)
```

### 3. 创建和完成任务

```kotlin
val taskService by world.di.instance<TaskService>()

// 创建采集任务
val task = taskService.createTask(
    sect = sect,
    named = Named("采集灵草任务"),
    type = TaskType.GATHERING,
    requirement = TaskRequirement(
        requiredItems = mapOf(lingcao to 10)
    ),
    rewardConfig = TaskRewardConfig(
        baseContribution = 50,
        baseExperience = 100,
        items = mapOf(lingshi to 30)
    ),
    limit = TaskLimit(
        maxAcceptors = 3,
        timeLimit = 1.hours,
        minLevel = 1
    )
) {}

// 弟子领取任务
taskService.acceptTask(task, disciple1)

// 弟子提交物品（假设弟子已收集到灵草）
inventoryService.addItem(disciple1, lingcao, 10)  // 模拟收集
taskService.submitItems(task, disciple1, mapOf(lingcao to 10))

// 完成任务
val reward = taskService.completeTask(task, disciple1)
println("获得贡献度: ${reward.contribution}")
println("获得经验: ${reward.experience}")
```

### 4. 建筑升级

```kotlin
val buildingService by world.di.instance<BuildingService>()

// 创建炼丹房
val alchemyHall = buildingService.createAlchemyHall(sect, Named("青云炼丹房")) {}

// 查看升级消耗
val cost = buildingService.calculateUpgradeCost(alchemyHall)
println("升级需要: $cost")

// 检查是否可升级
val error = buildingService.canUpgrade(alchemyHall)
if (error == null) {
    buildingService.upgrade(alchemyHall)
    println("升级成功！新等级: ${buildingService.getBuildingLevel(alchemyHall)}")
}
```

### 5. 功法学习

```kotlin
val techniqueService by world.di.instance<TechniqueService>()

// 创建功法
val swordTechnique = techniqueService.createTechnique(
    named = Named("基础剑法"),
    description = Description("入门剑法"),
    grade = TechniqueGrade.COMMON,
    requirement = TechniqueRequirement(
        contribution = 50,
        minLevel = 1
    ),
    effect = TechniqueEffect(
        attributeModifiers = mapOf(Named("Attack") to 10L)
    )
) {}

// 添加到藏经阁
val library = buildingService.createLibrary(sect, Named("藏经阁")) {}
techniqueService.addToLibrary(swordTechnique, library)

// 弟子学习功法
val learnError = techniqueService.canLearn(swordTechnique, disciple1, sect)
if (learnError == null) {
    techniqueService.learn(swordTechnique, disciple1, sect)
    println("学习成功！")
}

// 查看已学功法的属性加成
val modifiers = techniqueService.getTotalAttributeModifiers(disciple1)
println("攻击力加成: ${modifiers[Named("Attack")]}")
```

### 6. 福利领取

```kotlin
val welfareService by world.di.instance<WelfareService>()

// 创建每日福利规则
val dailyWelfare = welfareService.createWelfareRule(
    sect = sect,
    named = Named("每日俸禄"),
    config = WelfareConfig(
        period = 24.hours,
        rewardsByRole = mapOf(
            MemberRole.LEADER to WelfareReward(money = 100),
            MemberRole.ELDER to WelfareReward(money = 80),
            MemberRole.INNER_DISCIPLE to WelfareReward(money = 50),
            MemberRole.OUTER_DISCIPLE to WelfareReward(money = 30)
        )
    )
) {}

// 领取福利
val claimError = welfareService.canClaim(dailyWelfare, disciple1)
if (claimError == null) {
    val reward = welfareService.claim(dailyWelfare, disciple1)
    println("领取福利: 灵石+${reward.money}")
}
```

### 7. 成员晋升

```kotlin
val sectMemberService by world.di.instance<SectMemberService>()

// 检查晋升条件
val promoteError = sectMemberService.canPromote(sect, disciple1, MemberRole.INNER_DISCIPLE)
when (promoteError) {
    null -> {
        sectMemberService.promote(sect, disciple1, MemberRole.INNER_DISCIPLE, leader)
        println("晋升成功！")
    }
    is PromotionError.InsufficientContribution -> {
        println("贡献度不足: 需要${promoteError.required}, 当前${promoteError.actual}")
    }
    else -> println("晋升失败: $promoteError")
}

// 查看权限
val canPublish = sectMemberService.hasPermission(sect, disciple1, Permission.PublishTasks)
println("可以发布任务: $canPublish")
```

---

## 事件监听示例

```kotlin
// 监听任务完成事件
world.observe<OnTaskCompleted>().exec { event ->
    println("任务完成: ${event.task}, 弟子: ${event.disciple}")
    println("奖励: 贡献度+${event.reward.contribution}")
}

// 监听功法学习事件
world.observe<OnTechniqueLearned>().exec { event ->
    println("功法学习: ${event.technique}, 弟子: ${event.disciple}")
}

// 监听宗门升级事件
world.observe<OnSectLevelUp>().exec { event ->
    println("宗门升级: ${event.sect} -> ${event.newLevel}级")
    println("解锁建筑: ${event.unlocks.newBuildingTypes}")
}

// 监听成员晋升事件
world.observe<OnMemberPromoted>().exec { event ->
    println("成员晋升: ${event.member} ${event.oldRole} -> ${event.newRole}")
}
```

---

## 常见问题

### Q: 如何自定义任务奖励计算公式？

```kotlin
val customRewardFormula = RewardBonusFormula { completionTime, discipleLevel, quality ->
    // 完成时间越短，奖励越高
    val timeBonus = if (completionTime < 30.minutes) 1.5f else 1.0f
    // 等级越高，奖励越高
    val levelBonus = 1.0f + (discipleLevel * 0.01f)
    // 质量越高，奖励越高
    val qualityBonus = 1.0f + (quality * 0.5f)
    timeBonus * levelBonus * qualityBonus
}

val task = taskService.createTask(
    // ...
    rewardConfig = TaskRewardConfig(
        baseContribution = 50,
        bonusFormula = customRewardFormula
    )
) {}
```

### Q: 如何限制某些功法只能特定角色学习？

```kotlin
val advancedTechnique = techniqueService.createTechnique(
    // ...
    requirement = TechniqueRequirement(
        contribution = 500,
        minLevel = 20,
        allowedRoles = setOf(MemberRole.LEADER, MemberRole.ELDER, MemberRole.INNER_DISCIPLE)
        // 外门弟子无法学习
    )
) {}
```

### Q: 如何实现自定义的建筑效果？

```kotlin
// 监听建筑升级事件，应用效果
world.observe<OnBuildingUpgraded>().involving<AlchemyHall>().exec { event ->
    val efficiency = buildingService.getBuildingEfficiency(entity)
    // 更新炼丹成功率等属性
}
```

---

## 下一步

- 查看 [data-model.md](./data-model.md) 了解完整的数据模型
- 查看 [contracts/](./contracts/) 目录了解各服务的详细 API
- 查看 [research.md](./research.md) 了解设计决策的背景

