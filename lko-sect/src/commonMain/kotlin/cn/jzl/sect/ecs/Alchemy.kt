package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.observers.exec
import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.core.UsedBy
import kotlin.time.Duration.Companion.seconds

sealed class AlchemyFurnace
data class OnAlchemyCompleteEvent(val alchemyFurnace: Entity, val user: Entity)

val alchemyAddon = createAddon("alchemy", {}) {
    install(formulaAddon)
    install(countdownAddon)
    injects {
        this bind singleton { new(::AlchemyService) }
    }
    components {
        world.componentId<AlchemyFurnace> { it.tag() }
        world.componentId<OnAlchemyCompleteEvent>()
    }
}

class AlchemyService(world: World) : EntityRelationContext(world) {

    private val formulaService by world.di.instance<FormulaService>()
    private val inventoryService by world.di.instance<InventoryService>()

    private val alchemyFurnaces = world.query { EntityAlchemyContext(this) }

    init {
        world.observe<OnCountdownComplete>().exec(alchemyFurnaces) {
            onAlchemyComplete(entity, it.user)
        }
    }

    private fun onAlchemyComplete(alchemyFurnace: Entity, user: Entity) {
        inventoryService.getAllItems(alchemyFurnace).forEach { entity ->
            world.entity(entity) { it.addRelation<OwnedBy>(user) }
        }
        world.entity(alchemyFurnace) { it.removeRelation<UsedBy>(user) }
        world.emit(alchemyFurnace, OnAlchemyCompleteEvent(alchemyFurnace, user))
    }

    @ECSDsl
    fun createAlchemyFurnace(named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity = world.entity {
        it.addTag<AlchemyFurnace>()
        it.addComponent(named)
        block(it)
    }

    fun alchemy(alchemyFurnace: Entity, user: Entity, formula: Entity) {
        require(alchemyFurnace.hasTag<AlchemyFurnace>())
        require(alchemyFurnace.getRelationUp<UsedBy>() == null)
        formulaService.executeFormula(user, alchemyFurnace, formula)
        world.entity(alchemyFurnace) {
            it.addRelation<UsedBy>(user)
            it.addComponent(Countdown(5.seconds))
        }
    }

    private class EntityAlchemyContext(world: World) : EntityQueryContext(world) {

        val user by relationUp<UsedBy>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<AlchemyFurnace>())
        }
    }
}

