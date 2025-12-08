package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.associatedBy
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.core.coreAddon

/**
 * 宗门
 */
sealed class Sect

/**
 * 宗门声望
 */
@JvmInline
value class SectReputation(val value: Int)

/**
 * 宗门贡献度
 */
@JvmInline
value class Contribution(val value: Int)

/**
 * 成员角色
 */
enum class MemberRole {
    LEADER,    // 宗主
    ELDER,     // 长老
    DISCIPLE   // 弟子
}

/**
 * 成员数据
 */
data class MemberData(
    val player: Entity,
    val role: MemberRole,
    val contribution: Contribution
)

/**
 * 建筑类型
 */
sealed class Building

/**
 * 炼丹房
 */
sealed class AlchemyHall : Building()

/**
 * 藏经阁
 */
sealed class Library : Building()

val sectAddon = createAddon("sect") {
    install(coreAddon)
    install(levelingAddon)
    install(moneyAddon)
    install(characterAddon)
    injects {
        this bind singleton { new(::SectService) }
    }
    components {
        world.componentId<Sect> { it.tag() }
        world.componentId<SectReputation>()
        world.componentId<MemberData>()
        world.componentId<Building> { it.tag() }
        world.componentId<AlchemyHall> { it.tag() }
        world.componentId<Library> { it.tag() }
    }
}

class SectService(world: World) : EntityRelationContext(world) {
    @PublishedApi
    internal val levelingService by world.di.instance<LevelingService>()

    @PublishedApi
    internal val sects = world.query { SectContext(this) }.associatedBy { named }

    @ECSDsl
    inline fun createSect(named: Named, leader: Entity, block: EntityCreateContext.(Entity) -> Unit): Entity {
        val sect = world.entity {
            require(named !in sects) { "Sect named ${named.name} already exists" }
            it.addComponent(named)
            it.addTag<Sect>()
            levelingService.upgradeable(this, it)
            it.addComponent(SectReputation(0))
            it.addComponent(Money(1000))
            block(it)
        }
        // 添加宗主
        addMember(sect, leader, MemberRole.LEADER)
        return sect
    }

    @ECSDsl
    fun createSect(name: String, leader: Entity, block: EntityCreateContext.(Entity) -> Unit): Entity = createSect(Named(name), leader, block)

    operator fun get(name: String): Entity? = sects[Named(name)]

    /**
     * 添加成员
     */
    fun addMember(sect: Entity, player: Entity, role: MemberRole) = world.entity(sect) {
        require(it.hasTag<Sect>()) { "实体${sect.id}不是宗门" }

        // 检查玩家是否已经在宗门中
        val existingMember = getMemberData(sect, player)
        require(existingMember == null) { "玩家${player.id}已经在宗门${sect.id}中" }

        it.addRelation(player, MemberData(player, role, Contribution(0)))
    }

    /**
     * 移除成员
     */
    fun removeMember(sect: Entity, player: Entity) = world.entity(sect) {
        require(it.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        it.removeRelation<MemberData>(player)
    }

    /**
     * 获取成员数据
     */
    fun getMemberData(sect: Entity, player: Entity): MemberData? {
        return sect.getRelation<MemberData?>(player)
    }

    /**
     * 获取所有成员
     */
    fun getMembers(sect: Entity): Sequence<Pair<Entity, MemberData>> {
        return sect.getRelationsWithData<MemberData>().map { it.relation.target to it.data }
    }

    /**
     * 改变成员角色
     */
    fun changeMemberRole(sect: Entity, player: Entity, newRole: MemberRole) = world.entity(sect) {
        val memberData = getMemberData(sect, player)
        require(memberData != null) { "玩家${player.id}不是宗门${sect.id}的成员" }

        it.addRelation(player, memberData.copy(role = newRole))
    }

    /**
     * 增加贡献度
     */
    fun addContribution(sect: Entity, player: Entity, amount: Int) = world.entity(sect) {
        val memberData = getMemberData(sect, player)
        require(memberData != null) { "玩家${player.id}不是宗门${sect.id}的成员" }

        val newContribution = memberData.contribution.value + amount
        it.addRelation(player, memberData.copy(contribution = Contribution(newContribution)))
    }

    /**
     * 增加宗门经验值
     */
    fun addExperience(sect: Entity, exp: Long) {
        levelingService.addExperience(sect, exp)
    }

    /**
     * 增加声望
     */
    fun addReputation(sect: Entity, amount: Int) = world.entity(sect) {
        val currentRep = it.getComponent<SectReputation?>() ?: SectReputation(0)
        it.addComponent(SectReputation(currentRep.value + amount))
    }

    /**
     * 创建建筑
     */
    @ECSDsl
    fun createAlchemyHall(sect: Entity, named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity = world.entity {
        it.addTag<Building>()
        it.addTag<AlchemyHall>()
        it.addRelation<OwnedBy>(sect)
        it.addComponent(named)
        block(it)
    }

    @ECSDsl
    fun createLibrary(sect: Entity, named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity = world.entity {
        it.addTag<Building>()
        it.addTag<Library>()
        it.addRelation<OwnedBy>(sect)
        it.addComponent(named)
        block(it)
    }

    @PublishedApi
    internal class SectContext(world: World) : EntityQueryContext(world) {

        val named by component<Named>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<Sect>()
        }
    }
}