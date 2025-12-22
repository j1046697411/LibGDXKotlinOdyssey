package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.ECSDsl


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
