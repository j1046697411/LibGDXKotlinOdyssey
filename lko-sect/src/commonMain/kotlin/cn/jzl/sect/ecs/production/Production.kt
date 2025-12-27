package cn.jzl.sect.ecs.production

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.sect.ecs.inventory.InventoryService
import cn.jzl.sect.ecs.item.ItemService
import kotlin.getValue
import kotlin.sequences.forEach

val resourceProductionAddon = createAddon("resourceProduction") {
    injects {
        this bind singleton { new(::ResourceProductionService) }
    }
    entities { world.componentId<ResourceOutputAmount>() }
}


fun interface ResourceOutputConfig {
    fun addOutput(itemPrefab: Entity, amount: Int)
}

@JvmInline
value class ResourceOutputAmount(val amount: Int)

class ResourceProductionService(world: World) : EntityRelationContext(world) {

    private val inventoryService by world.di.instance<InventoryService>()
    private val itemService by world.di.instance<ItemService>()

    @ECSDsl
    fun configureOutput(context: EntityCreateContext, entity: Entity, block: ResourceOutputConfig.() -> Unit) = context.run {
        ResourceOutputConfig { itemPrefab, amount ->
            require(itemService.isItemPrefab(itemPrefab)) { }
            entity.addRelation(itemPrefab, ResourceOutputAmount(amount))
        }.block()
    }

    fun executeProduction(context: EntityCreateContext, receiver: Entity, producer: Entity) = context.run {
        producer.getRelationsWithData<ResourceOutputAmount>().forEach {
            inventoryService.addItem(receiver, it.relation.target, it.data.amount)
        }
    }

    fun getOutputAmount(producer: Entity, itemPrefab: Entity): Int = producer.getRelation<ResourceOutputAmount?>(itemPrefab)?.amount ?: 0
}