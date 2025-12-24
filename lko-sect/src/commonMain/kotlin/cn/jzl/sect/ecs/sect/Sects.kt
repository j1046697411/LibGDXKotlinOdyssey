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
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.QueryStream
import cn.jzl.ecs.query.filter
import cn.jzl.ecs.query.firstOrNull
import cn.jzl.ecs.query.map
import cn.jzl.ecs.query.query
import cn.jzl.ecs.relation
import cn.jzl.ecs.relations
import cn.jzl.sect.ecs.Character
import cn.jzl.sect.ecs.CharacterService
import cn.jzl.sect.ecs.Named
import cn.jzl.sect.ecs.characterAddon

sealed class Sect
sealed class PlayerOwner

enum class Member {
    INNER_DISCIPLE, // 内门弟子
    PERSONAL_DISCIPLE, // 亲传弟子
    ELDER, // 长老
    SECT_MASTER // 宗主
}

val sectAddon = createAddon("sect") {
    install(characterAddon)
    injects { this bind singleton { new(::SectService) } }
    components {
        world.componentId<Sect> { it.tag() }
        world.componentId<PlayerOwner> { it.tag() }
        world.componentId<Member>()
    }
}

class SectService(world: World) : EntityRelationContext(world) {

    private val characterService by world.di.instance<CharacterService>()
    private val sects by lazy { world.query { SectContext(this) } }

    val playerSect by lazy {
        sects.filter { isPlayerOwner }.map { entity }.firstOrNull() ?: createPlayerSect(
            Named("玩家宗门"),
            characterService.createCharacter(Named("宗主")) {}
        )
    }

    fun createPlayerSect(named: Named, leader: Entity): Entity = createSect(named, leader) {
        it.addTag<PlayerOwner>()
    }

    fun createSect(named: Named, leader: Entity, block: EntityCreateContext.(Entity) -> Unit = {}): Entity {
        val sect = world.entity {
            it.addTag<Sect>()
            it.addComponent(named)
            block(it)
        }
        addSectMember(sect, leader, Member.SECT_MASTER)
        return sect
    }

    private fun addSectMember(sect: Entity, member: Entity, type: Member) {
        require(sect.hasTag<Sect>()) { "Entity $sect is not a Sect" }
        require(member.hasTag<cn.jzl.sect.ecs.Character>()) { "Entity $member is not a Character" }
        world.entity(member) { it.addRelation(sect, type) }
    }

    fun getSects(): QueryStream<SectContext> = sects

    fun getSectMembers(sect: Entity): QueryStream<SectMemberContext> {
        require(sect.hasTag<Sect>()) { "Entity $sect is not a Sect" }
        return world.query { SectMemberContext(this, sect) }
    }

    class SectContext(world: World) : EntityQueryContext(world) {

        val named by component<Named>()
        val isPlayerOwner by ifRelationExist(relations.component<PlayerOwner>())

        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Sect>())
        }
    }

    class SectMemberContext(world: World, val sect: Entity) : EntityQueryContext(world) {

        val member by relation<Member>(sect)
        val named by component<Named>()
        val isPlayerOwner by ifRelationExist(relations.component<PlayerOwner>())

        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Character>())
        }
    }
}