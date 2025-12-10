package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.core.Named
import kotlin.test.*

class MoneyServiceTest {

    private val world by lazy {
        world {
            install(moneyAddon)
        }
    }

    private val moneyService by world.di.instance<MoneyService>()

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
        val buyer = world.entity {
            it.addComponent(Money(200))
        }
        val seller = world.entity {
            it.addComponent(Money(100))
        }

        // 转移货币
        moneyService.transferMoney(buyer, seller, 50) {}

        // 验证买家货币减少
        world.entity(buyer) {
            val buyerMoney = it.getComponent<Money>()
            assertNotNull(buyerMoney)
            assertEquals(150, buyerMoney.value)
        }

        // 验证卖家货币增加
        world.entity(seller) {
            val sellerMoney = it.getComponent<Money>()
            assertNotNull(sellerMoney)
            assertEquals(150, sellerMoney.value)
        }
    }

    // 测试货币转移时余额不足
    @Test
    fun testTransferMoneyWithInsufficientBalance() {
        val buyer = world.entity {
            it.addComponent(Money(50))
        }
        val seller = world.entity {
            it.addComponent(Money(100))
        }

        // 尝试转移超过余额的货币，应该失败
        assertFailsWith<IllegalArgumentException> {
            moneyService.transferMoney(buyer, seller, 100) {}
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
        val buyer = world.entity {
            it.addComponent(Money(200))
        }
        val seller = world.entity {
            it.addComponent(Money(100))
        }

        var businessLogicExecuted = false

        // 转移货币并执行业务逻辑
        moneyService.transferMoney(buyer, seller, 50) {
            businessLogicExecuted = true
        }

        // 验证业务逻辑被执行
        assertTrue(businessLogicExecuted)

        // 验证货币转移成功
        world.entity(buyer) {
            val buyerMoney = it.getComponent<Money>()
            assertNotNull(buyerMoney)
            assertEquals(150, buyerMoney.value)
        }
        world.entity(seller) {
            val sellerMoney = it.getComponent<Money>()
            assertNotNull(sellerMoney)
            assertEquals(150, sellerMoney.value)
        }
    }
}