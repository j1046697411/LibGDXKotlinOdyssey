package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.coreAddon
import cn.jzl.sect.ecs.item.ItemService

@JvmInline
value class Money(val value: Int)

val moneyAddon = createAddon("Money", {}) {
    install(coreAddon)
    injects {
        this bind singleton { new(::MoneyService) }
    }
    components {
        world.componentId<Money>()
    }
}

class MoneyService(@PublishedApi internal val world: World) {

    private val itemService by world.di.instance<ItemService>()
    private val inventoryService by world.di.instance<InventoryService>()

    private val spiritStone: Entity by lazy { itemService.itemPrefab(ATTRIBUTE_SPIRIT_STONE) }

    fun transferMoney(buyer: Entity, seller: Entity, money: Int) {
        inventoryService.transferItem(buyer, seller, spiritStone, money)
    }

    fun getSpiritStone(entity: Entity): Int = inventoryService.getItemCount(entity, spiritStone)

    fun hasEnoughMoney(entity: Entity, money: Int): Boolean {
        return inventoryService.hasEnoughItems(entity, spiritStone, money)
    }

    companion object {
        val ATTRIBUTE_SPIRIT_STONE = Named("spirit stone")
    }
}

fun EntityCreateContext.hasEnoughMoney(entity: Entity, money: Int): Boolean {
    val currentMoney = entity.getComponent<Money?>() ?: return false
    return currentMoney.value >= money
}

fun EntityCreateContext.getMoney(entity: Entity): Int = entity.getComponent<Money?>()?.value ?: 0

fun EntityCreateContext.increaseMoney(entity: Entity, money: Int) {
    val currentMoney = entity.getComponent<Money?>()
    val newValue = (currentMoney?.value ?: 0) + money
    entity.addComponent(Money(newValue))
}

fun EntityCreateContext.decreaseMoney(entity: Entity, money: Int) {
    val currentMoney = entity.getComponent<Money?>()
    val newValue = (currentMoney?.value ?: 0) - money
    entity.addComponent(Money(newValue))
}
