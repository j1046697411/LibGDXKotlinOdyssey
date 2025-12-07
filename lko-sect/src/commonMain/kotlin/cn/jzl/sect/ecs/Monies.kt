package cn.jzl.sect.ecs

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityUpdateContext
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.entity
import cn.jzl.sect.ecs.core.coreAddon

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

    inline fun transferMoney(buyer: Entity, seller: Entity, money: Int, block: ()-> Unit): Unit = world.entity(buyer) {
        val buyerMoney = it.getComponent<Money>()
        val sellerMoney = seller.getComponent<Money>()
        require(buyerMoney.value >= money) { "buyer $buyer has not enough money $money" }
        block()

        it.addComponent(Money(buyerMoney.value - money))
        world.entity(seller) { seller -> seller.addComponent(Money(sellerMoney.value + money)) }
    }
}

fun EntityUpdateContext.hasEnoughMoney(entity: Entity, money: Int): Boolean {
    val currentMoney = entity.getComponent<Money?>() ?: return false
    return currentMoney.value >= money
}

fun EntityUpdateContext.getMoney(entity: Entity): Int = entity.getComponent<Money?>()?.value ?: 0

fun EntityUpdateContext.increaseMoney(entity: Entity, money: Int) {
    val currentMoney = entity.getComponent<Money?>()
    entity.addComponent(Money(currentMoney?.value ?: money))
}

fun EntityUpdateContext.decreaseMoney(entity: Entity, money: Int) {
    val currentMoney = entity.getComponent<Money?>()
    entity.addComponent(Money(currentMoney?.value ?: -money))
}
