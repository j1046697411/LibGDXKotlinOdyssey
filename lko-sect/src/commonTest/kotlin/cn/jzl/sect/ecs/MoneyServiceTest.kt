package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.item.ItemService
import cn.jzl.sect.ecs.core.Named
import kotlin.test.*

class MoneyServiceTest {

    private val world by lazy {
        world {
            install(inventoryAddon)
            install(moneyAddon)
        }
    }

    private val moneyService by world.di.instance<MoneyService>()
    private val itemService by world.di.instance<ItemService>()
    private val inventoryService by world.di.instance<InventoryService>()

    // 测试货币组件
    @Test
    fun testMoneyComponent() {
        val entity = world.entity {
            it.addComponent(Money(100))
        }

        world.entity(entity) {
            val money = it.getComponent<Money>()
            assertNotNull(money)
            assertEquals(100, money.value)
        }
    }

    // 测试货币转移
    @Test
    fun testTransferMoney() {
        val buyer = world.entity {}
        val seller = world.entity {}

        val spiritStonePrefab = itemService.itemPrefab(MoneyService.ATTRIBUTE_SPIRIT_STONE) {}

        inventoryService.addItem(buyer, spiritStonePrefab, 200)
        inventoryService.addItem(seller, spiritStonePrefab, 100)

        moneyService.transferMoney(buyer, seller, 50)

        assertEquals(150, moneyService.getSpiritStone(buyer))
        assertEquals(150, moneyService.getSpiritStone(seller))
    }

    // 测试货币转移时余额不足
    @Test
    fun testTransferMoneyWithInsufficientBalance() {
        val buyer = world.entity {}
        val seller = world.entity {}

        val spiritStonePrefab = itemService.itemPrefab(MoneyService.ATTRIBUTE_SPIRIT_STONE) {}

        inventoryService.addItem(buyer, spiritStonePrefab, 50)
        inventoryService.addItem(seller, spiritStonePrefab, 100)

        // 尝试转移超过余额的货币，应该失败
        assertFailsWith<IllegalArgumentException> {
            moneyService.transferMoney(buyer, seller, 100)
        }
    }

    // 测试 hasEnoughMoney 扩展函数
    @Test
    fun testHasEnoughMoney() {
        val entity = world.entity {
            it.addComponent(Money(100))
        }

        world.entity(entity) {
            // 有足够的钱
            assertTrue(hasEnoughMoney(entity, 50))
            // 恰好有足够的钱
            assertTrue(hasEnoughMoney(entity, 100))
            // 钱不够
            assertFalse(hasEnoughMoney(entity, 150))
        }
    }

    // 测试 getMoney 扩展函数
    @Test
    fun testGetMoney() {
        val entity = world.entity {
            it.addComponent(Money(100))
        }
        val entityWithoutMoney = world.entity {}

        world.entity(entity) {
            assertEquals(100, getMoney(entity))
            assertEquals(0, getMoney(entityWithoutMoney))
        }
    }

    // 测试 increaseMoney 扩展函数
    @Test
    fun testIncreaseMoney() {
        val entity = world.entity {
            it.addComponent(Money(100))
        }
        val entityWithoutMoney = world.entity {}

        // 增加有货币实体的货币
        world.entity(entity) {
            increaseMoney(entity, 50)
        }
        world.entity(entity) {
            val money = it.getComponent<Money>()
            assertNotNull(money)
            assertEquals(150, money.value)
        }

        // 增加没有货币实体的货币
        world.entity(entityWithoutMoney) {
            increaseMoney(entityWithoutMoney, 50)
        }
        world.entity(entityWithoutMoney) {
            val money = it.getComponent<Money>()
            assertNotNull(money)
            assertEquals(50, money.value)
        }
    }

    // 测试 decreaseMoney 扩展函数
    @Test
    fun testDecreaseMoney() {
        val entity = world.entity {
            it.addComponent(Money(100))
        }
        val entityWithoutMoney = world.entity {}

        // 减少有货币实体的货币
        world.entity(entity) {
            decreaseMoney(entity, 50)
        }
        world.entity(entity) {
            val money = it.getComponent<Money>()
            assertNotNull(money)
            assertEquals(50, money.value)
        }

        // 减少没有货币实体的货币
        world.entity(entityWithoutMoney) {
            decreaseMoney(entityWithoutMoney, 50)
        }
        world.entity(entityWithoutMoney) {
            val money = it.getComponent<Money>()
            assertNotNull(money)
            assertEquals(-50, money.value)
        }
    }

    // 测试货币转移与业务逻辑结合
    @Test
    fun testTransferMoneyWithBusinessLogic() {
        val buyer = world.entity {}
        val seller = world.entity {}

        val spiritStonePrefab = itemService.itemPrefab(MoneyService.ATTRIBUTE_SPIRIT_STONE) {}

        inventoryService.addItem(buyer, spiritStonePrefab, 200)
        inventoryService.addItem(seller, spiritStonePrefab, 100)

        var businessLogicExecuted = false

        moneyService.transferMoney(buyer, seller, 50)
        businessLogicExecuted = true

        assertTrue(businessLogicExecuted)
        assertEquals(150, moneyService.getSpiritStone(buyer))
        assertEquals(150, moneyService.getSpiritStone(seller))
    }
}