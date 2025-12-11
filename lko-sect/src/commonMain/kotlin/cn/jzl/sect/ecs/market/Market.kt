package cn.jzl.sect.ecs.market

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.observers.exec
import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.count
import cn.jzl.ecs.query.forEach
import cn.jzl.ecs.query.query
import cn.jzl.ecs.query.singleQuery
import cn.jzl.sect.ecs.Countdown
import cn.jzl.sect.ecs.Money
import cn.jzl.sect.ecs.MoneyService
import cn.jzl.sect.ecs.OnCountdownComplete
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.countdownAddon
import cn.jzl.sect.ecs.item.Amount
import cn.jzl.sect.ecs.item.Item
import cn.jzl.sect.ecs.item.ItemService
import cn.jzl.sect.ecs.item.itemAddon
import kotlin.math.min
import kotlin.time.Duration.Companion.seconds

/**
 * 市场
 */
sealed class Market

/**
 * 物品的寄售
 */
sealed class ConsignmentOrder

/**
 * 物品的收购单
 */
sealed class AcquisitionOrder

/**
 * 物品的托寄售
 */
@JvmInline
value class ConsignmentItemData(val unitPrice: Int)

/**
 * 物品的收购单
 */
data class AcquisitionItemData(val count: Int, val unitPrice: Int)

val marketAddon = createAddon("market", {}) {
    install(itemAddon)
    install(countdownAddon)
    injects {
        this bind singleton { new(::MarketService) }
    }
    components {
        world.componentId<Market> { it.tag() }
        world.componentId<OwnedBy> {
            it.tag()
            it.singleRelation()
        }
        world.componentId<ConsignmentOrder> {
            it.tag()
            it.singleRelation()
        }
        world.componentId<AcquisitionOrder> {
            it.tag()
            it.singleRelation()
        }
        world.componentId<ConsignmentItemData> { it.singleRelation() }
        world.componentId<AcquisitionItemData>()
    }
}


sealed class OnAcquisitionOrderCompleted
sealed class OnConsignmentOrderCompleted

class MarketService(world: World) : EntityRelationContext(world) {
    private val consignmentOrderCountdown = Countdown(5.seconds)
    private val acquisitionOrderCountdown = Countdown(5.seconds)

    private val itemService by world.di.instance<ItemService>()
    private val moneyService by world.di.instance<MoneyService>()

    init {
        val acquisitionOrderQuery = world.query {
            object : EntityQueryContext(this) {
                val market: Entity? get() = getRelationUp<AcquisitionOrder>()
                val owner: Entity? get() = getRelationUp<OwnedBy>()

                override fun FamilyMatcher.FamilyBuilder.configure() {
                    kind(relations.id<AcquisitionOrder>())
                }
            }
        }
        val consignmentOrderQuery = world.query {
            object : EntityQueryContext(this) {

                val market: Entity? get() = getRelationUp<ConsignmentOrder>()
                val owner: Entity? get() = getRelationUp<OwnedBy>()

                override fun FamilyMatcher.FamilyBuilder.configure() {
                    kind(relations.id<ConsignmentOrder>())
                }
            }
        }

        world.observe<OnCountdownComplete>().exec(acquisitionOrderQuery) {
            val market = it.market ?: return@exec
            val owner = it.owner ?: return@exec
            cancelAcquisition(market, owner, entity)
        }
        world.observe<OnCountdownComplete>().exec(consignmentOrderQuery) {
            val market = it.market ?: return@exec
            val owner = it.owner ?: return@exec
            cancelConsignment(market, owner, entity)
        }
    }

    fun createMarket(player: Entity, named: Named): Entity = world.entity {
        it.addTag<Market>()
        it.addComponent(named)
        it.addRelation<OwnedBy>(player)
    }

    fun createConsignmentOrder(market: Entity, owner: Entity): Entity {
        market.requireTag<Market> { "实体${market.id}不是市场" }
        return world.entity { consignmentOrder ->
            consignmentOrder.addRelation<ConsignmentOrder>(market)
            consignmentOrder.addRelation<OwnedBy>(owner)
            consignmentOrder.addComponent(consignmentOrderCountdown)
        }
    }

    fun addConsignItems(consignmentOrder: Entity, item: Entity, unitPrice: Int) {
        require(consignmentOrder.getRelationUp<ConsignmentOrder>() != null) {
            "订单${consignmentOrder.id}不是寄售订单"
        }
        val owner = consignmentOrder.getRelationUp<OwnedBy>()
        require(item.getRelationUp<OwnedBy>() == owner) { "物品${item.id}不是玩家${owner?.id}所有" }
        world.entity(item) { item.addRelation(consignmentOrder, ConsignmentItemData(unitPrice)) }
    }

    /**
     * 寄售物品
     * @param market 市场实体
     * @param player 玩家实体
     * @param block 寄售物品的lambda表达式
     * @return 寄售订单实体
     */
    fun consignItems(market: Entity, player: Entity, block: MarketContext.(Entity) -> Unit): Entity {
        val consignmentOrder = createConsignmentOrder(market, player)
        val context = MarketContext { item: Entity, count: Int, unitPrice: Int ->
            require(item.getRelationUp<OwnedBy>() == player) { "物品${item.id}不是玩家${player.id}所有" }
            addConsignItems(consignmentOrder, itemService.splitItem(item, count), unitPrice)
        }
        context.block(consignmentOrder)
        return consignmentOrder
    }

    fun cancelConsignment(market: Entity, player: Entity, consignmentOrder: Entity) {
        require(consignmentOrder.getRelationUp<OwnedBy>() == player) { "订单${consignmentOrder.id}不是玩家${player.id}所有" }
        require(consignmentOrder.getRelationUp<ConsignmentOrder>() == market) { "订单${consignmentOrder.id}不是市场${market.id}的寄售订单" }
        val items = world.singleQuery {
            object : EntityQueryContext(this) {
                override fun FamilyMatcher.FamilyBuilder.configure() {
                    relation<ConsignmentItemData>(consignmentOrder)
                }
            }
        }
        items.use { it.forEach { removeRelation(relations.relation<ConsignmentItemData>(consignmentOrder)) } }
        world.destroy(consignmentOrder)
    }

    fun createAcquisitionOrder(market: Entity, player: Entity, block: MarketContext.() -> Unit): Unit = world.entity(player) {
        val money = it.getComponent<Money?>()
        require(money != null) { "玩家${player.id}没有货币组件" }
        val items = mutableMapOf<Entity, AcquisitionItemData>()
        var remainingMoney = money.value
        val context = MarketContext { item: Entity, count: Int, unitPrice: Int ->
            require(item.hasTag<Item>()) { "物品${item.id}不是物品" }
            require(item.hasPrefab()) { "物品${item.id}不是预制物品" }
            require(count > 0) { "物品${item.id}数量不能小于等于0" }
            require(unitPrice > 0) { "物品${item.id}单价不能小于等于0" }
            require(remainingMoney >= count * unitPrice) { "玩家${player.id}货币不足" }
            require(item !in items) { "物品${item.id}已经在订单中" }
            items[item] = AcquisitionItemData(count, unitPrice)
            remainingMoney -= count * unitPrice
        }
        context.block()
        world.entity { acquisitionOrder ->
            acquisitionOrder.addRelation<OwnedBy>(player)
            acquisitionOrder.addRelation<AcquisitionOrder>(market)
            acquisitionOrder.addComponent(Money(money.value - remainingMoney))
            items.forEach { (item, data) ->
                acquisitionOrder.addRelation(item, data)
            }
            acquisitionOrder.addComponent(acquisitionOrderCountdown)
        }
        it.addComponent(Money(remainingMoney))
    }

    fun cancelAcquisition(market: Entity, player: Entity, acquisitionOrder: Entity) {
        require(acquisitionOrder.getRelationUp<OwnedBy>() == player) { "订单${acquisitionOrder.id}不是玩家${player.id}所有" }
        require(acquisitionOrder.getRelationUp<AcquisitionOrder>() == market) { "订单${acquisitionOrder.id}不是市场${market.id}的收购订单" }
        val money = acquisitionOrder.getComponent<Money>()
        moneyService.transferMoney(acquisitionOrder, player, money.value) {
            world.destroy(acquisitionOrder)
        }
    }

    /**
     * 购买物品
     * @param consignmentOrder 寄售订单实体
     * @param buyer 购买物品的玩家实体
     * @param item 购买的物品实体
     * @param count 购买的物品数量，默认购买所有物品
     */
    fun buyItems(consignmentOrder: Entity, buyer: Entity, item: Entity, count: Int? = null) = world.entity(consignmentOrder) {
        val market = it.getRelationUp<ConsignmentOrder>()
        require(market != null) { "订单${it.id}不是市场的收购订单" }
        val owner = it.getRelationUp<OwnedBy>()
        require(owner != null && owner != buyer) { "玩家${buyer.id}不能购买自己的收购订单" }
        val itemData = item.getRelation<ConsignmentItemData?>(consignmentOrder)
        require(itemData != null) { "订单${consignmentOrder.id}没有物品${item.id}" }
        val amount = item.getComponent<Amount?>()?.value ?: 1
        val buyCount = count ?: amount
        require(amount >= buyCount) { "物品${item.id}数量不足" }
        moneyService.transferMoney(buyer, owner, buyCount * itemData.unitPrice) {
            itemService.transferItem(buyer, item, buyCount)
        }
        // 买家购买了所有物品，移除寄售订单关系
        if (item.getRelationUp<OwnedBy>() == buyer) {
            world.entity(item) { item.removeRelation<ConsignmentItemData>(consignmentOrder) }
            return@entity
        }
        if (it.getRelationDown<ConsignmentItemData>().count() > 0) {
            return@entity
        }
        world.destroy(it)
        world.emit<OnConsignmentOrderCompleted>(market)
    }

    /**
     * 为收购订单提供物品
     * @param acquisitionOrder 收购订单实体
     * @param supplier 提供物品的玩家实体
     * @param item 提供的物品实体
     * @param count 提供的物品数量，默认提供所有物品
     */
    fun supplyItems(acquisitionOrder: Entity, supplier: Entity, item: Entity, count: Int? = null): Unit = world.entity(acquisitionOrder) {
        val money = it.getComponent<Money?>()
        require(money != null) { "玩家${supplier.id}没有货币组件" }
        val market = it.getRelationUp<AcquisitionOrder>()
        require(market != null) { "订单${it.id}不是市场的收购订单" }
        val owner = it.getRelationUp<OwnedBy>()
        require(owner != null && owner != supplier) { "玩家${supplier.id}不能为订单${acquisitionOrder.id}提供物品" }
        val itemPrefab = item.prefab
        require(itemPrefab != null) { "物品${item.id}不是预制物品" }
        val itemData = it.getRelation<AcquisitionItemData?>(itemPrefab)
        require(itemData != null) { "订单${acquisitionOrder.id}没有物品${item.id}" }
        val amount = item.getComponent<Amount?>()?.value ?: 1
        val supplyCount = min(count ?: itemData.count, amount)
        moneyService.transferMoney(it, supplier, itemData.unitPrice * supplyCount) {
            itemService.transferItem(it, item, supplyCount)
        }
        if (itemData.count > supplyCount) {
            it.addRelation(itemPrefab, itemData.copy(count = itemData.count - supplyCount))
            return@entity
        }
        it.removeRelation<AcquisitionItemData>(itemPrefab)
        if (it.getRelations<AcquisitionItemData>().count() == 0) {
            world.destroy(it)
            world.emit<OnAcquisitionOrderCompleted>(market)
        }
    }

    fun interface MarketContext {
        fun addItem(item: Entity, count: Int, unitPrice: Int)
    }
}