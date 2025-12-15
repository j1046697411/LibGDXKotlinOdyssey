package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.entity
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.sect.ecs.core.Named
import kotlin.getValue


sealed class Character

val characterAddon = createAddon("character") {
    install(healthAddon)
    install(levelingAddon)
    injects { this bind singleton { new(::CharacterService) } }
    components {
        world.componentId<Character> { it.tag() }
    }
}

class CharacterService(world: World) : EntityRelationContext(world) {

    private val levelingService by world.di.instance<LevelingService>()
    private val healthService by world.di.instance<HealthService>()

    @ECSDsl
    fun createCharacter(named: Named, maxHealth: Long = 100, block: EntityCreateContext.(Entity) -> Unit): Entity {
        return world.entity {
            levelingService.upgradeable(this, it)
            healthService.initializeHealth(this, it, maxHealth)
            it.addTag<Character>()
            it.addComponent(named)
            block(it)
        }
    }
}
