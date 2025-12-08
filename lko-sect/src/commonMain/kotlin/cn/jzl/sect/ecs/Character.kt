package cn.jzl.sect.ecs

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
import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.associatedBy
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.core.Named
import kotlin.getValue


sealed class Character
sealed class OnCreatedCharacter

val characterAddon = createAddon("Character", {}) {
    install(levelingAddon)
    injects { this bind singleton { new(::CharacterService) } }
    components {
        world.componentId<Character> { it.tag() }
        world.componentId<OnCreatedCharacter> { it.tag() }
    }
}

class CharacterService(world: World) : EntityRelationContext(world) {

    private val levelingService by world.di.instance<LevelingService>()

    private val characters = world.query { CharacterContext(this) }.associatedBy { named }

    @ECSDsl
    fun createCharacter(named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity {
        require(named !in characters) { }
        val character = world.entity {
            it.addTag<Character>()
            it.addComponent(named)
            levelingService.upgradeable(this, it)
            block(it)
        }
        world.emit<OnCreatedCharacter>(character)
        return character
    }

    class CharacterContext(world: World) : EntityQueryContext(world) {
        val named by component<Named>()
        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<Character>()
        }
    }
}