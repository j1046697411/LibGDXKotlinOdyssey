# API 合同: 福利服务 (WelfareService)

**功能分支**: `001-sect-system-enhancement`
**创建时间**: 2025-12-13
**模块**: `lko-sect/ecs/welfare/`

## 概述

WelfareService 提供宗门福利规则管理、周期性福利发放和领取记录功能。

---

## 服务接口

```kotlin
package cn.jzl.sect.ecs.welfare

class WelfareService(world: World) : EntityRelationContext(world) {

    // ==================== 福利规则管理 ====================
    
    /**
     * 创建福利规则
     * @param sect 宗门实体
     * @param named 福利名称
     * @param config 福利配置
     * @param block 额外配置
     * @return 福利规则实体
     */
    @ECSDsl
    fun createWelfareRule(
        sect: Entity,
        named: Named,
        config: WelfareConfig,
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity
    
    /**
     * 更新福利规则配置
     * @param rule 福利规则实体
     * @param config 新配置
     */
    fun updateWelfareConfig(rule: Entity, config: WelfareConfig)
    
    /**
     * 删除福利规则
     * @param rule 福利规则实体
     */
    fun deleteWelfareRule(rule: Entity)

    // ==================== 福利领取 ====================
    
    /**
     * 检查成员是否可领取福利
     * @param rule 福利规则实体
     * @param member 成员实体
     * @return 可领取返回 null，否则返回不可领取原因
     */
    fun canClaim(rule: Entity, member: Entity): WelfareClaimError?
    
    /**
     * 成员领取福利
     * @param rule 福利规则实体
     * @param member 成员实体
     * @return 实际领取的奖励
     * @throws IllegalStateException 如果福利冷却中
     * @throws IllegalStateException 如果成员角色无对应福利
     */
    fun claim(rule: Entity, member: Entity): WelfareReward
    
    /**
     * 获取成员下次可领取时间
     * @param rule 福利规则实体
     * @param member 成员实体
     * @return 下次可领取的时间戳，若从未领取返回 0
     */
    fun getNextClaimTime(rule: Entity, member: Entity): Long

    // ==================== 查询 ====================
    
    /**
     * 获取宗门所有福利规则
     * @param sect 宗门实体
     * @return 福利规则实体序列
     */
    fun getWelfareRulesBySect(sect: Entity): Sequence<Entity>
    
    /**
     * 获取成员可领取的福利列表
     * @param sect 宗门实体
     * @param member 成员实体
     * @return 可领取的福利规则实体序列
     */
    fun getClaimableWelfares(sect: Entity, member: Entity): Sequence<Entity>
    
    /**
     * 获取成员某福利的领取状态
     * @param rule 福利规则实体
     * @param member 成员实体
     * @return 领取状态，若从未领取返回 null
     */
    fun getClaimStatus(rule: Entity, member: Entity): WelfareStatus?
    
    /**
     * 获取成员在某福利上的累计领取次数
     * @param rule 福利规则实体
     * @param member 成员实体
     * @return 累计领取次数
     */
    fun getClaimCount(rule: Entity, member: Entity): Int
}
```

---

## 数据类型

```kotlin
data class WelfareConfig(
    val period: Duration,                              // 发放周期
    val rewardsByRole: Map<MemberRole, WelfareReward>  // 按角色的奖励
)

data class WelfareReward(
    val money: Int = 0,                        // 灵石奖励
    val items: Map<Entity, Int> = emptyMap(),  // 物品奖励 (物品预制体 -> 数量)
    val contribution: Int = 0                  // 贡献度奖励
)

data class WelfareStatus(
    val ruleEntity: Entity,     // 福利规则实体
    val lastClaimTime: Long,    // 上次领取时间戳
    val claimCount: Int = 0     // 累计领取次数
)

sealed class WelfareClaimError {
    object RuleNotFound : WelfareClaimError()
    data class Cooldown(val remainingTime: Duration) : WelfareClaimError()
    object NoRewardForRole : WelfareClaimError()
    object NotSectMember : WelfareClaimError()
}
```

---

## 事件发射

| 事件 | 触发时机 |
|------|----------|
| `OnWelfareClaimed` | `claim` 成功后 |

---

## 默认福利配置

```kotlin
val DEFAULT_DAILY_WELFARE = WelfareConfig(
    period = 24.hours,
    rewardsByRole = mapOf(
        MemberRole.LEADER to WelfareReward(money = 100, contribution = 50),
        MemberRole.ELDER to WelfareReward(money = 80, contribution = 40),
        MemberRole.INNER_DISCIPLE to WelfareReward(money = 50, contribution = 20),
        MemberRole.OUTER_DISCIPLE to WelfareReward(money = 30, contribution = 10)
    )
)
```

---

## 使用示例

```kotlin
// 创建每日福利规则
val dailyWelfare = welfareService.createWelfareRule(
    sect = sectEntity,
    named = Named("每日俸禄"),
    config = WelfareConfig(
        period = 24.hours,
        rewardsByRole = mapOf(
            MemberRole.LEADER to WelfareReward(money = 100, contribution = 50),
            MemberRole.ELDER to WelfareReward(money = 80, contribution = 40),
            MemberRole.INNER_DISCIPLE to WelfareReward(money = 50, contribution = 20),
            MemberRole.OUTER_DISCIPLE to WelfareReward(money = 30, contribution = 10)
        )
    )
) {}

// 检查是否可领取
val error = welfareService.canClaim(dailyWelfare, memberEntity)
when (error) {
    null -> {
        // 领取福利
        val reward = welfareService.claim(dailyWelfare, memberEntity)
        println("领取成功: 灵石+${reward.money}, 贡献度+${reward.contribution}")
    }
    is WelfareClaimError.Cooldown -> {
        println("福利冷却中，剩余时间: ${error.remainingTime}")
    }
    else -> {
        println("无法领取: $error")
    }
}

// 查询可领取的福利
val claimable = welfareService.getClaimableWelfares(sectEntity, memberEntity)
claimable.forEach { rule ->
    println("可领取: ${rule.getComponent<Named>()}")
}
```

