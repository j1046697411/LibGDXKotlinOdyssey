/**
 * 宗门系统模块，负责管理宗门实体、成员关系和玩家所属宗门。
 *
 * 宗门系统包括：
 * - 宗门实体的创建和管理
 * - 宗门成员关系的建立和维护
 * - 玩家专属宗门的处理
 * - 宗门成员类型的定义（内门弟子、亲传弟子、长老、宗主）
 */
package cn.jzl.sect.ecs.sect

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.entity
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.Query
import cn.jzl.ecs.query.QueryStream
import cn.jzl.ecs.query.filter
import cn.jzl.ecs.query.firstOrNull
import cn.jzl.ecs.query.map
import cn.jzl.ecs.query.query
import cn.jzl.ecs.relation
import cn.jzl.ecs.relations
import cn.jzl.sect.ecs.character.Character
import cn.jzl.sect.ecs.character.CharacterService
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.character.characterAddon

/**
 * 宗门标签组件，用于标识实体为宗门。
 *
 * 所有宗门实体都必须添加此标签，以便宗门系统进行管理和识别。
 */
sealed class Sect

/**
 * 玩家所属标签组件，用于标识宗门为玩家所有。
 *
 * 玩家的专属宗门会添加此标签，以便系统区分玩家宗门和其他AI宗门。
 */
sealed class PlayerOwner

/**
 * 宗门成员类型枚举，定义了宗门内不同层级的成员。
 *
 * 成员类型决定了角色在宗门内的地位、权限和资源分配。
 */
enum class Member {
    /** 内门弟子，宗门中的核心成员，拥有较高的修炼资源 */
    INNER_DISCIPLE,

    /** 亲传弟子，宗主或长老的直接徒弟，享有特殊培养资源 */
    PERSONAL_DISCIPLE,

    /** 长老，宗门中的高级管理层，负责管理特定事务 */
    ELDER,

    /** 宗主，宗门的最高领导者，拥有最高决策权 */
    SECT_MASTER
}

/**
 * 宗门系统插件，用于注册宗门相关的组件、服务和依赖注入。
 *
 * 该插件负责：
 * - 安装角色系统插件
 * - 注册宗门相关的标签组件和关系组件
 * - 注入SectService服务单例
 */
val sectAddon = createAddon("sect") {
    install(characterAddon)
    injects { this bind singleton { new(::SectService) } }
    components {
        world.componentId<Sect> { it.tag() }
        world.componentId<PlayerOwner> { it.tag() }
        world.componentId<Member>()
    }
}

/**
 * 宗门服务类，负责创建和管理宗门实体、成员关系以及玩家宗门。
 *
 * 该服务提供了宗门的创建、成员管理和查询功能，是宗门系统的核心组件。
 *
 * @property world ECS世界实例
 */
class SectService(world: World) : EntityRelationContext(world) {

    private val characterService by world.di.instance<CharacterService>()
    private val sects by lazy { world.query { SectContext(this) } }
    private val sectMembers = mutableMapOf<Entity, Query<SectMemberContext>>()

    /**
     * 玩家所属的宗门实体。
     *
     * 如果不存在，系统会自动创建一个默认的玩家宗门，包含一个默认的宗主角色。
     */
    val playerSect by lazy {
        sects.filter { isPlayerOwner }.map { entity }.firstOrNull() ?: createPlayerSect(
            Named("玩家宗门"),
            characterService.createCharacter(Named("宗主")) {}
        )
    }

    /**
     * 创建玩家专属宗门。
     *
     * 玩家专属宗门会自动添加PlayerOwner标签，以便系统识别。
     *
     * @param named 宗门名称组件
     * @param leader 宗门领导者实体
     * @return 创建的玩家宗门实体
     */
    @ECSDsl
    fun createPlayerSect(named: Named, leader: Entity, block: EntityCreateContext.(Entity) -> Unit = {}): Entity = createSect(named, leader) {
        it.addTag<PlayerOwner>()
        block(it)
    }

    /**
     * 创建宗门实体。
     *
     * 创建宗门时，会自动将指定的领导者添加为宗主。
     *
     * @param named 宗门名称组件
     * @param leader 宗门领导者实体
     * @param block 可选的额外配置块，用于自定义宗门属性
     * @return 创建的宗门实体
     */
    @ECSDsl
    fun createSect(named: Named, leader: Entity, block: EntityCreateContext.(Entity) -> Unit = {}): Entity {
        val sect = world.entity {
            it.addTag<Sect>()
            it.addComponent(named)
            block(it)
        }
        addSectMember(sect, leader, Member.SECT_MASTER)
        return sect
    }

    /**
     * 向宗门添加成员。
     *
     * @param sect 宗门实体
     * @param member 要添加的成员实体
     * @param type 成员类型
     * @throws IllegalArgumentException 如果实体不是宗门或不是角色
     */
    private fun addSectMember(sect: Entity, member: Entity, type: Member) {
        require(sect.hasTag<Sect>()) { "Entity $sect is not a Sect" }
        require(member.hasTag<Character>()) { "Entity $member is not a Character" }
        world.entity(member) { it.addRelation(sect, type) }
    }

    /**
     * 获取所有宗门的查询流。
     *
     * @return 包含所有宗门的查询流
     */
    fun getSects(): QueryStream<SectContext> = sects

    /**
     * 获取指定宗门的所有成员。
     *
     * @param sect 宗门实体
     * @return 包含宗门所有成员的查询流
     * @throws IllegalArgumentException 如果实体不是宗门
     */
    fun getSectMembers(sect: Entity): Query<SectMemberContext> {
        require(sect.hasTag<Sect>()) { "Entity $sect is not a Sect" }
        return sectMembers.getOrPut(sect) { world.query { SectMemberContext(this, sect) } }
    }

    /**
     * 宗门查询上下文，用于查询宗门实体的属性。
     *
     * @property world ECS世界实例
     */
    class SectContext(world: World) : EntityQueryContext(world) {

        /** 宗门名称组件 */
        val named by component<Named>()

        /** 是否为玩家所属宗门 */
        val isPlayerOwner by ifRelationExist(relations.component<PlayerOwner>())

        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Sect>())
        }
    }

    /**
     * 宗门成员查询上下文，用于查询宗门成员的属性。
     *
     * @property world ECS世界实例
     * @property sect 要查询的宗门实体
     */
    class SectMemberContext(world: World, val sect: Entity) : EntityQueryContext(world) {

        /** 成员在宗门中的身份类型 */
        val member by relation<Member>(sect)

        /** 成员名称 */
        val named by component<Named>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Character>())
        }
    }
}