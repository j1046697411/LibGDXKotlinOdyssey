package cn.jzl.sect.ecs.market

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.entity
import cn.jzl.ecs.instanceOf
import cn.jzl.ecs.query.count
import cn.jzl.ecs.query.first
import cn.jzl.ecs.query.map
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.Money
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.item.*
import kotlin.test.*

class MarketServiceTest {

    private val world by lazy {
        world {
            install(itemAddon)
            install(marketAddon) {}
        }
    }

    private val marketService by world.di.instance<MarketService>()
    private val itemService by world.di.instance<ItemService>()

    // 测试创建市场
    @Test
    fun testCreateMarket() {
        val player = world.entity {
            it.addComponent(Named("Player"))
            it.addComponent(Money(500))
        }
        val named = Named("Test Market")

        val market = marketService.createMarket(player, named)

        // 验证市场创建成功
        assertNotNull(market)
        world.entity(market) {
            // 验证市场标签
            assertTrue(it.hasTag<Market>())
            // 验证市场名称
            val marketNamed = it.getComponent<Named>()
            assertNotNull(marketNamed)
            assertEquals("Test Market", marketNamed.name)
            // 验证市场所有者
            val owner = it.getRelationUp<OwnedBy>()
            assertNotNull(owner)
            assertEquals(player, owner)
        }
    }

    // 测试寄售物品
    @Test
    fun testConsignItems() {
        // 创建玩家、市场和物品
        val player = world.entity {
            it.addComponent(Named("Player"))
            it.addComponent(Money(500))
        }
        val market = marketService.createMarket(player, Named("Test Market"))
        
        // 创建物品预制体
        val itemPrefab = itemService.itemPrefab(Named("Test Item")) {}
        val item = world.instanceOf(itemPrefab) {
            it.addRelation<OwnedBy>(player)
        }

        // 寄售物品
        val consignmentOrder = marketService.consignItems(market, player) { order ->
            addItem(item, 1, 100)
        }

        // 验证寄售成功
        assertNotNull(consignmentOrder)
        
        // 验证寄售订单
        world.entity(consignmentOrder) {
            // 验证订单所有者
            val owner = it.getRelationUp<OwnedBy>()
            assertNotNull(owner)
            assertEquals(player, owner)
            // 验证订单所属市场
            val orderMarket = it.getRelationUp<ConsignmentOrder>()
            assertNotNull(orderMarket)
            assertEquals(market, orderMarket)
        }
        
        // 验证物品被正确寄售
        world.entity(item) {
            // 验证物品与寄售订单的关系
            val consignmentData = it.getRelation<ConsignmentItemData>(consignmentOrder)
            assertNotNull(consignmentData)
            assertEquals(100, consignmentData.unitPrice)
        }
    }

    // 测试取消寄售
    @Test
    fun testCancelConsignment() {
        // 创建玩家、市场、物品和寄售订单
        val player = world.entity {
            it.addComponent(Named("Player"))
            it.addComponent(Money(500))
        }
        val market = marketService.createMarket(player, Named("Test Market"))
        
        // 创建物品预制体
        val itemPrefab = itemService.itemPrefab(Named("itemPrefab")) {}
        
        val item = world.instanceOf(itemPrefab) {
            it.addRelation<OwnedBy>(player)
        }


        val consignmentOrder: Entity = marketService.consignItems(market, player) { order ->
            addItem(item, 1, 100)
        }

        // 取消寄售
        marketService.cancelConsignment(market, player, consignmentOrder)

        // 验证取消成功
        // 验证物品不再与寄售订单关联
        world.entity(item) {
            val consignmentData = it.getRelation<ConsignmentItemData?>(consignmentOrder)
            assertNull(consignmentData)
        }
        
        // 验证订单被销毁（如果实现中会销毁订单）
        // 此处根据实际实现调整，可能订单会被保留但状态改变
        // world.entity(consignmentOrder) { /* 验证订单状态 */ }
    }

    // 测试创建收购订单
    @Test
    fun testCreateAcquisitionOrder() {
        // 记录创建订单前的货币
        val player = world.entity {
            it.addComponent(Money(1000))
            it.addComponent(Named("Player"))
        }
        val market = marketService.createMarket(player, Named("Test Market"))
        
        // 创建物品预制体
        val itemPrefab = itemService.itemPrefab(Named("Test Item")) {}

        // 创建收购订单（10个物品，每个50货币，总共500货币）
        marketService.createAcquisitionOrder(market, player) {
            addItem(itemPrefab, 10, 50)
        }

        // 验证收购订单创建成功
        // 验证玩家货币减少
        world.entity(player) {
            val money = it.getComponent<Money>()
            assertNotNull(money)
            assertEquals(500, money.value) // 1000 - 10*50 = 500
        }
        
        // 验证市场中存在收购订单
        // 此处可以添加查询收购订单的逻辑，根据实际实现调整
    }

    // 测试购买寄售物品
    @Test
    fun testPurchaseConsignment() {
        // 创建卖家、买家、市场和物品
        val seller = world.entity {
            it.addComponent(Money(1000))
        }
        val buyer = world.entity {
            it.addComponent(Money(2000))
        }
        val market = marketService.createMarket(seller, Named("Test Market"))
        val itemPrefab = itemService.itemPrefab(Named("name")) {}
        val item = world.instanceOf(itemPrefab) {
            it.addRelation<OwnedBy>(seller)
        }

        val consignmentOrder: Entity = marketService.consignItems(market, seller) { order ->
            addItem(item, 1, 100)
        }

        // 购买寄售物品
        marketService.buyItems(buyer, item, 1)

        // 验证购买成功
        // 验证买家货币减少
        world.entity(buyer) {
            val buyerMoney = it.getComponent<Money>()
            assertNotNull(buyerMoney)
            assertEquals(1900, buyerMoney.value) // 2000 - 100 = 1900
        }
        
        // 验证物品所有权转移
        world.entity(item) {
            val owner = it.getRelationUp<OwnedBy>()
            assertNotNull(owner)
            assertEquals(buyer, owner)
        }
        
        // 验证物品不再与寄售订单关联
        world.entity(item) {
            val consignmentData = it.getRelation<ConsignmentItemData?>(consignmentOrder)
            assertNull(consignmentData)
        }
    }

    // 测试寄售堆叠物品
    @Test
    fun testConsignStackableItem() {
        // 创建玩家、市场和堆叠物品
        val player = world.entity {
            it.addComponent(Named("Player"))
            it.addComponent(Money(500))
        }
        val market = marketService.createMarket(player, Named("Test Market"))
        val itemPrefab =itemService.itemPrefab(Named("itemPrefab")) {
            it.addTag<Stackable>()
        }
        val item = world.instanceOf(itemPrefab) {
            it.addRelation<OwnedBy>(player)
            it.addComponent(Amount(10))
        }

        // 寄售部分堆叠物品（5个，单价100）
        val consignmentOrder = marketService.consignItems(market, player) { order ->
            addItem(item, 5, 100)
        }

        // 验证寄售成功
        assertNotNull(consignmentOrder)
        
        // 验证物品剩余数量（应剩余5个）
        world.entity(item) {
            val remainingAmount = it.getComponent<Amount>()
            assertNotNull(remainingAmount)
            assertEquals(5, remainingAmount.value) // 10 - 5 = 5
        }

        world.entity(consignmentOrder) {
            val items = it.getRelationDown<ConsignmentItemData>()
            assertEquals(1, items.count())
            val item = items.map { entity }.first()
            assertEquals(5, item.getComponent<Amount?>()?.value ?: 0)
        }
    }

    // 测试取消寄售异常情况
    @Test
    fun testCancelConsignmentWithWrongOwner() {
        // 创建卖家、买家、市场、物品和寄售订单
        val seller = world.entity {
            it.addComponent(Named("Player"))
            it.addComponent(Money(500))
        }
        val buyer = world.entity {
            it.addComponent(Named("Player"))
            it.addComponent(Money(500))
        }
        val market = marketService.createMarket(seller, Named("Test Market"))
        val itemPrefab = itemService.itemPrefab(Named("itemPrefab")) {}
        val item = world.instanceOf(itemPrefab) {
            it.addRelation<OwnedBy>(seller)
        }

        val consignmentOrder: Entity = marketService.consignItems(market, seller) { order ->
            addItem(item, 1, 100)
        }

        // 尝试用买家取消卖家的寄售订单，应该失败
        assertFailsWith<IllegalArgumentException> {
            marketService.cancelConsignment(market, buyer, consignmentOrder)
        }
    }

    // 测试购买寄售物品时货币不足
    @Test
    fun testPurchaseConsignmentWithInsufficientMoney() {
        // 创建卖家、买家（货币不足）、市场和物品
        val seller = world.entity {
            it.addComponent(Money(1000))
        }
        val buyer = world.entity {
            it.addComponent(Money(50)) // 只有50货币，不足以购买100货币的物品
        }
        val market = marketService.createMarket(seller, Named("Test Market"))
        val itemPrefab = itemService.itemPrefab(Named("itemPrefab")) {
            it.addTag<Item>()
        }
        val item = world.instanceOf(itemPrefab) {
            it.addRelation<OwnedBy>(seller)
        }

        val consignmentOrder: Entity = marketService.consignItems(market, seller) { order ->
            addItem(item, 1, 100)
        }

        // 尝试购买，应该失败
        assertFailsWith<IllegalArgumentException> {
            marketService.buyItems(buyer, item, 1)
        }
    }

    // 测试创建收购订单时货币不足
    @Test
    fun testCreateAcquisitionOrderWithInsufficientMoney() {
        // 创建玩家（货币不足）、市场和物品
        val player = world.entity {
            it.addComponent(Money(500)) // 只有500货币，不足以创建1000货币的收购订单
        }
        val market = marketService.createMarket(player, Named("Test Market"))
        val itemPrefab = itemService.itemPrefab(Named("itemPrefab")) {}

        // 尝试创建收购订单，应该失败
        assertFailsWith<IllegalArgumentException> {
            marketService.createAcquisitionOrder(market, player) {
                addItem(itemPrefab, 20, 100) // 20 * 100 = 2000货币，超出玩家的500货币
            }
        }
    }

    // 测试购买寄售物品数量不足
    @Test
    fun testPurchaseConsignmentWithInsufficientItemQuantity() {
        // 创建卖家、买家、市场和堆叠物品
        val seller = world.entity {
            it.addComponent(Money(1000))
        }
        val buyer = world.entity {
            it.addComponent(Money(1000))
        }
        val market = marketService.createMarket(seller, Named("Test Market"))
        val itemPrefab = itemService.itemPrefab(Named("itemPrefab")) {
        }
        val item = world.instanceOf(itemPrefab) {
            it.addRelation<OwnedBy>(seller)
            it.addComponent(Amount(5)) // 只有5个物品
        }

        val consignmentOrder: Entity = marketService.consignItems(market, seller) { order ->
            addItem(item, 5, 100)
        }

        // 尝试购买6个物品，应该失败

        assertFailsWith<IllegalArgumentException> {
            marketService.buyItems(buyer, item, 6)
        }
    }

    // 测试购买自己的寄售订单
    @Test
    fun testPurchaseOwnConsignment() {
        // 创建玩家、市场和物品
        val player = world.entity {
            it.addComponent(Money(1000))
        }
        val market = marketService.createMarket(player, Named("Test Market"))
        val itemPrefab = itemService.itemPrefab("") {
            it.addTag<Item>()
        }
        val item = world.instanceOf(itemPrefab) {
            it.addRelation<OwnedBy>(player)
        }

        val consignmentOrder: Entity = marketService.consignItems(market, player) { order ->
            addItem(item, 1, 100)
        }
        assertFailsWith<IllegalArgumentException> {
            marketService.buyItems(player, item, 1)
        }
    }
}
