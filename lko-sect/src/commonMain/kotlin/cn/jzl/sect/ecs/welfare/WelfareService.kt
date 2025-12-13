package cn.jzl.sect.ecs.welfare

import cn.jzl.di.instance
import cn.jzl.ecs.*
import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.*
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import kotlin.time.Duration.Companion.milliseconds

/**
 * 福利服务
 * 提供福利规则管理和领取功能
 */
class WelfareService(world: World) : EntityRelationContext(world) {

    private val sectService by world.di.instance<SectService>()
    private val levelingService by world.di.instance<LevelingService>()
    private val inventoryService by world.di.instance<InventoryService>()

    /**
     * 创建福利规则
     */
    @ECSDsl
    fun createWelfareRule(
        sect: Entity,
        named: Named,
        config: WelfareConfig,
        reward: WelfareReward,
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return world.entity {
            it.addTag<WelfareRule>()
            it.addComponent(named)
            it.addComponent(WelfareConfigComponent(config))
            it.addComponent(WelfareRewardComponent(reward))
            it.addRelation<OwnedBy>(sect)
            block(it)
        }
    }

    /**
     * 检查是否可以领取福利
     */
    fun canClaim(welfare: Entity, member: Entity, sect: Entity): WelfareClaimError? {
        require(welfare.hasTag<WelfareRule>()) { "实体${welfare.id}不是福利规则" }
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }

        val memberData = sectService.getMemberData(sect, member)
            ?: return WelfareClaimError.NotSectMember

        val config = welfare.getComponent<WelfareConfigComponent?>()?.config
            ?: return WelfareClaimError.WelfareNotFound

        if (memberData.role !in config.allowedRoles) {
            return WelfareClaimError.RoleNotAllowed(memberData.role)
        }

        val reward = welfare.getComponent<WelfareRewardComponent?>()?.reward
        if (reward?.getRewardFor(memberData.role) == null) {
            return WelfareClaimError.NoRewardForRole(memberData.role)
        }

        val status = welfare.getRelation<WelfareStatus?>(member)
        if (status != null) {
            val elapsed = (System.currentTimeMillis() - status.lastClaimTime).milliseconds
            if (elapsed < config.cooldown) {
                val remaining = config.cooldown - elapsed
                return WelfareClaimError.OnCooldown(remaining)
            }
        }

        return null
    }

    /**
     * 领取福利
     */
    fun claim(welfare: Entity, member: Entity, sect: Entity): WelfareClaimError? {
        val error = canClaim(welfare, member, sect)
        if (error != null) return error

        val memberData = sectService.getMemberData(sect, member)!!
        val reward = welfare.getComponent<WelfareRewardComponent?>()!!.reward
        val roleReward = reward.getRewardFor(memberData.role)!!

        if (roleReward.contribution > 0) {
            sectService.addContribution(sect, member, roleReward.contribution)
        }

        if (roleReward.experience > 0) {
            levelingService.addExperience(member, roleReward.experience)
        }

        roleReward.items.forEach { (itemPrefab, amount) ->
            inventoryService.addItem(member, itemPrefab, amount)
        }

        val existingStatus = welfare.getRelation<WelfareStatus?>(member)
        val newStatus = WelfareStatus(
            lastClaimTime = System.currentTimeMillis(),
            totalClaimed = (existingStatus?.totalClaimed ?: 0) + 1
        )
        world.entity(welfare) {
            it.addRelation(member, newStatus)
        }

        world.emit(welfare, OnWelfareClaimed(welfare, member, roleReward))
        return null
    }

    /**
     * 获取宗门的所有福利规则
     */
    fun getWelfaresBySect(sect: Entity): Sequence<Entity> {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return world.query { WelfareQueryContext(this) }.entities.filter { welfare ->
            welfare.getRelationUp<OwnedBy>() == sect
        }
    }

    /**
     * 获取成员可领取的福利
     */
    fun getAvailableWelfares(member: Entity, sect: Entity): Sequence<Entity> {
        return getWelfaresBySect(sect).filter { welfare ->
            canClaim(welfare, member, sect) == null
        }
    }

    /**
     * 获取成员的福利领取记录
     */
    fun getClaimStatus(welfare: Entity, member: Entity): WelfareStatus? {
        return welfare.getRelation<WelfareStatus?>(member)
    }

    /**
     * 获取成员距离下次可领取的剩余时间
     */
    fun getRemainingCooldown(welfare: Entity, member: Entity): kotlin.time.Duration? {
        val status = welfare.getRelation<WelfareStatus?>(member) ?: return null
        val config = welfare.getComponent<WelfareConfigComponent?>()?.config ?: return null

        val elapsed = (System.currentTimeMillis() - status.lastClaimTime).milliseconds
        return if (elapsed < config.cooldown) {
            config.cooldown - elapsed
        } else {
            null
        }
    }

    @PublishedApi
    internal class WelfareQueryContext(world: World) : EntityQueryContext(world) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<WelfareRule>()
        }
    }
}

