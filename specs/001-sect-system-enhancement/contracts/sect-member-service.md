# API 合同: 宗门成员服务 (SectMemberService)

**功能分支**: `001-sect-system-enhancement`
**创建时间**: 2025-12-13
**模块**: `lko-sect/ecs/sect/`

## 概述

SectMemberService 提供增强的成员管理功能，包括权限检查、晋升机制和配额管理。此服务扩展现有 SectService 的成员管理能力。

---

## 服务接口

```kotlin
package cn.jzl.sect.ecs.sect

class SectMemberService(world: World) : EntityRelationContext(world) {

    // ==================== 权限检查 ====================
    
    /**
     * 检查成员是否有指定权限
     * @param sect 宗门实体
     * @param member 成员实体
     * @param permission 权限类型
     * @return 是否有权限
     */
    fun hasPermission(sect: Entity, member: Entity, permission: Permission): Boolean
    
    /**
     * 要求成员有指定权限，否则抛出异常
     * @param sect 宗门实体
     * @param member 成员实体
     * @param permission 权限类型
     * @throws IllegalStateException 如果没有权限
     */
    fun requirePermission(sect: Entity, member: Entity, permission: Permission)

    // ==================== 晋升管理 ====================
    
    /**
     * 检查成员是否满足晋升条件
     * @param sect 宗门实体
     * @param member 成员实体
     * @param targetRole 目标角色
     * @return 满足返回 null，否则返回不满足的原因
     */
    fun canPromote(sect: Entity, member: Entity, targetRole: MemberRole): PromotionError?
    
    /**
     * 晋升成员
     * @param sect 宗门实体
     * @param member 成员实体
     * @param targetRole 目标角色
     * @param approver 审批人实体 (宗主或长老)
     * @throws IllegalStateException 如果条件不满足
     * @throws IllegalStateException 如果审批人权限不足
     */
    fun promote(sect: Entity, member: Entity, targetRole: MemberRole, approver: Entity)
    
    /**
     * 降级成员
     * @param sect 宗门实体
     * @param member 成员实体
     * @param targetRole 目标角色
     * @param operator 操作人实体
     * @throws IllegalStateException 如果操作人权限不足
     */
    fun demote(sect: Entity, member: Entity, targetRole: MemberRole, operator: Entity)

    // ==================== 配额管理 ====================
    
    /**
     * 获取宗门当前成员配额使用情况
     * @param sect 宗门实体
     * @return 各角色的当前数量和上限
     */
    fun getMemberQuotaUsage(sect: Entity): Map<MemberRole, Pair<Int, Int>>
    
    /**
     * 检查是否可以添加指定角色的成员
     * @param sect 宗门实体
     * @param role 成员角色
     * @return 可以添加返回 true
     */
    fun canAddMember(sect: Entity, role: MemberRole): Boolean
    
    /**
     * 获取宗门成员配额配置
     * @param sect 宗门实体
     * @return 配额配置
     */
    fun getMemberQuota(sect: Entity): MemberQuota
    
    /**
     * 更新宗门成员配额配置
     * @param sect 宗门实体
     * @param quota 新配额配置
     */
    fun updateMemberQuota(sect: Entity, quota: MemberQuota)

    // ==================== 晋升条件配置 ====================
    
    /**
     * 获取晋升条件配置
     * @param targetRole 目标角色
     * @return 晋升条件
     */
    fun getPromotionRequirement(targetRole: MemberRole): PromotionRequirement
    
    /**
     * 设置考核任务
     * @param sect 宗门实体
     * @param targetRole 目标角色
     * @param tasks 考核任务实体集合
     */
    fun setPromotionTasks(sect: Entity, targetRole: MemberRole, tasks: Set<Entity>)

    // ==================== 查询 ====================
    
    /**
     * 获取指定角色的所有成员
     * @param sect 宗门实体
     * @param role 成员角色
     * @return 成员实体序列
     */
    fun getMembersByRole(sect: Entity, role: MemberRole): Sequence<Entity>
    
    /**
     * 获取成员角色
     * @param sect 宗门实体
     * @param member 成员实体
     * @return 成员角色，若非成员返回 null
     */
    fun getMemberRole(sect: Entity, member: Entity): MemberRole?
    
    /**
     * 获取成员贡献度
     * @param sect 宗门实体
     * @param member 成员实体
     * @return 贡献度值
     */
    fun getContribution(sect: Entity, member: Entity): Int
    
    /**
     * 获取可晋升的成员列表
     * @param sect 宗门实体
     * @param targetRole 目标角色
     * @return 满足晋升条件的成员实体序列
     */
    fun getPromotableMem(sect: Entity, targetRole: MemberRole): Sequence<Entity>
}
```

---

## 数据类型

```kotlin
/**
 * 成员角色（扩展）
 */
enum class MemberRole {
    LEADER,          // 宗主：全权限
    ELDER,           // 长老：管理权限
    INNER_DISCIPLE,  // 内门弟子：任务发布权限
    OUTER_DISCIPLE   // 外门弟子：仅参与权限
}

/**
 * 权限类型
 */
sealed class Permission {
    /** 管理成员（添加/移除/晋升） */
    object ManageMembers : Permission()
    /** 管理建筑（创建/升级） */
    object ManageBuildings : Permission()
    /** 发布任务 */
    object PublishTasks : Permission()
    /** 领取任务 */
    object AcceptTasks : Permission()
    /** 学习功法 */
    object LearnTechniques : Permission()
    /** 领取福利 */
    object ClaimWelfare : Permission()
    /** 管理宗门配置 */
    object ManageSect : Permission()
    /** 管理福利规则 */
    object ManageWelfare : Permission()
}

/**
 * 成员配额
 */
data class MemberQuota(
    val maxElders: Int = 5,
    val maxInnerDisciples: Int = 20,
    val maxOuterDisciples: Int = 50,
    val additionalPerLevel: Int = 10  // 每升一级增加的外门配额
)

/**
 * 晋升条件
 */
data class PromotionRequirement(
    val targetRole: MemberRole,
    val minContribution: Int,
    val minLevel: Long,
    val requiredTasks: Set<Entity> = emptySet(),  // 考核任务
    val requiresApproval: Boolean = true           // 是否需要审批
)

/**
 * 晋升错误
 */
sealed class PromotionError {
    data class InsufficientContribution(val required: Int, val actual: Int) : PromotionError()
    data class LevelTooLow(val required: Long, val actual: Long) : PromotionError()
    data class IncompleteTasks(val missing: Set<Entity>) : PromotionError()
    object QuotaExceeded : PromotionError()
    object AlreadyAtRole : PromotionError()
    object CannotPromoteToLeader : PromotionError()
}
```

---

## 权限矩阵

| 角色 | ManageMembers | ManageBuildings | PublishTasks | AcceptTasks | LearnTechniques | ClaimWelfare | ManageSect | ManageWelfare |
|------|--------------|-----------------|--------------|-------------|-----------------|--------------|------------|---------------|
| LEADER | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| ELDER | ✓* | ✓ | ✓ | ✓ | ✓ | ✓ | ✗ | ✗ |
| INNER_DISCIPLE | ✗ | ✗ | ✓ | ✓ | ✓ | ✓ | ✗ | ✗ |
| OUTER_DISCIPLE | ✗ | ✗ | ✗ | ✓ | ✓ | ✓ | ✗ | ✗ |

*注：长老可审批晋升，但不能移除长老或更改宗主*

---

## 默认晋升条件

```kotlin
val DEFAULT_PROMOTION_REQUIREMENTS = mapOf(
    MemberRole.INNER_DISCIPLE to PromotionRequirement(
        targetRole = MemberRole.INNER_DISCIPLE,
        minContribution = 500,
        minLevel = 10,
        requiresApproval = true
    ),
    MemberRole.ELDER to PromotionRequirement(
        targetRole = MemberRole.ELDER,
        minContribution = 2000,
        minLevel = 30,
        requiresApproval = true
    )
)
```

---

## 事件发射

| 事件 | 触发时机 |
|------|----------|
| `OnMemberPromoted` | `promote` 成功后 |

---

## 使用示例

```kotlin
// 检查权限
if (sectMemberService.hasPermission(sect, member, Permission.PublishTasks)) {
    // 创建任务
}

// 检查晋升条件
val error = sectMemberService.canPromote(sect, disciple, MemberRole.INNER_DISCIPLE)
when (error) {
    null -> {
        // 执行晋升
        sectMemberService.promote(sect, disciple, MemberRole.INNER_DISCIPLE, elder)
        println("晋升成功！")
    }
    is PromotionError.InsufficientContribution -> {
        println("贡献度不足: 需要${error.required}, 当前${error.actual}")
    }
    is PromotionError.IncompleteTasks -> {
        println("考核任务未完成: ${error.missing.size}个任务")
    }
    else -> println("晋升失败: $error")
}

// 查询配额使用情况
val usage = sectMemberService.getMemberQuotaUsage(sect)
usage.forEach { (role, quota) ->
    println("$role: ${quota.first}/${quota.second}")
}
```

