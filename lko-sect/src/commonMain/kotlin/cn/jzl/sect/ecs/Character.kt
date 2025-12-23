package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.sect.ecs.cultivation.CultivationService
import cn.jzl.sect.ecs.cultivation.cultivationAddon


sealed class Character

val characterAddon = createAddon("character") {
    install(healthAddon)
    install(levelingAddon)
    install(cultivationAddon)
    injects { this bind singleton { new(::CharacterService) } }
    components {
        world.componentId<Character> { it.tag() }
    }
}

class CharacterService(world: World) : EntityRelationContext(world) {

    private val cultivationService by world.di.instance<CultivationService>()

    @ECSDsl
    fun createCharacter(named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity {
        return world.entity {
            cultivationService.cultivable(this, it)
            it.addTag<Character>()
            it.addComponent(named)
            block(it)
        }
    }
}
