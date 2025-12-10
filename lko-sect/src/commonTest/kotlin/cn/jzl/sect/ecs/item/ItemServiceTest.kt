package cn.jzl.sect.ecs.item

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.entity
import cn.jzl.ecs.instanceOf
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import kotlin.test.*

class ItemServiceTest {

    private val world by lazy {
        world {
            install(itemAddon)
        }
    }

    private val itemService by world.di.instance<ItemService>()

    // 测试物品预制体创建
    @Test
    fun testCreateItemPrefab() {
        val named = Named("Test Item")
        val prefab = itemService.itemPrefab(named) {}

        assertNotNull(prefab)
        world.entity(prefab) {
            assertTrue(it.hasTag<Item>())
            val prefabNamed = it.getComponent<Named>()
            assertNotNull(prefabNamed)
            assertEquals("Test Item", prefabNamed.name)
        }
    }

    // 测试通过名称创建物品预制体
    @Test
    fun testCreateItemPrefabByName() {
        val prefab = itemService.itemPrefab("Test Item 2") {}

        assertNotNull(prefab)
        world.entity(prefab) {
            assertTrue(it.hasTag<Item>())
            val prefabNamed = it.getComponent<Named>()
            assertNotNull(prefabNamed)
            assertEquals("Test Item 2", prefabNamed.name)
        }
    }

    // 测试物品实例化
    @Test
    fun testInstantiateItem() {
        val prefab = itemService.itemPrefab("Test Item") {}
        val item = itemService.item(prefab) {}

        assertNotNull(item)
        world.entity(item) {
            assertTrue(it.hasTag<Item>())
            assertNotNull(it.prefab)
            assertEquals(prefab, it.prefab)
        }
    }

    // 测试通过名称实例化物品
    @Test
    fun testInstantiateItemByName() {
        itemService.itemPrefab("Test Item") {}
        val item = itemService.item("Test Item") {}

        assertNotNull(item)
        world.entity(item) {
            assertTrue(it.hasTag<Item>())
        }
    }

    // 测试获取不存在的物品预制体
    @Test
    fun testGetNonExistentItemPrefab() {
        assertFailsWith<IllegalArgumentException> {
            itemService.item("NonExistentItem") {}
        }
    }

    // 测试可堆叠物品
    @Test
    fun testStackableItem() {
        val prefab = itemService.itemPrefab("Stackable Item") {
            it.addTag<Stackable>()
        }
        val item = world.instanceOf(prefab) {
            it.addComponent(Amount(10))
        }

        world.entity(item) {
            assertTrue(it.hasTag<Stackable>())
            val amount = it.getComponent<Amount>()
            assertNotNull(amount)
            assertEquals(10, amount.value)
        }
    }

    // 测试物品拆分
    @Test
    fun testSplitItem() {
        val player = world.entity {}
        val prefab = itemService.itemPrefab("Stackable Item") {
            it.addTag<Stackable>()
        }
        val item = world.instanceOf(prefab) {
            it.addComponent(Amount(10))
            it.addRelation<OwnedBy>(player)
        }

        val splitItem = itemService.splitItem(item, 3)

        // 验证原物品数量减少
        world.entity(item) {
            val remainingAmount = it.getComponent<Amount>()
            assertNotNull(remainingAmount)
            assertEquals(7, remainingAmount.value)
        }

        // 验证拆分出的物品
        world.entity(splitItem) {
            val splitAmount = it.getComponent<Amount>()
            assertNotNull(splitAmount)
            assertEquals(3, splitAmount.value)
            val owner = it.getRelationUp<OwnedBy>()
            assertNotNull(owner)
            assertEquals(player, owner)
        }
    }

    // 测试拆分非堆叠物品
    @Test
    fun testSplitNonStackableItem() {
        val player = world.entity {}
        val prefab = itemService.itemPrefab("Non-Stackable Item") {}
        val item = world.instanceOf(prefab) {
            it.addRelation<OwnedBy>(player)
        }

        val splitItem = itemService.splitItem(item, 1)

        // 非堆叠物品拆分应返回原物品
        assertEquals(item, splitItem)
    }

    // 测试拆分数量不足的物品
    @Test
    fun testSplitItemWithInsufficientAmount() {
        val player = world.entity {}
        val prefab = itemService.itemPrefab("Stackable Item") {
            it.addTag<Stackable>()
        }
        val item = world.instanceOf(prefab) {
            it.addComponent(Amount(5))
            it.addRelation<OwnedBy>(player)
        }

        // 尝试拆分超过物品数量的数量，应该失败
        assertFailsWith<IllegalArgumentException> {
            itemService.splitItem(item, 10)
        }
    }

    // 测试物品转移
    @Test
    fun testTransferItem() {
        val sender = world.entity {}
        val receiver = world.entity {}
        val prefab = itemService.itemPrefab("Test Item") {}
        val item = world.instanceOf(prefab) {
            it.addRelation<OwnedBy>(sender)
        }

        itemService.transferItem(receiver, item, 1)

        // 验证物品所有权转移
        world.entity(item) {
            val owner = it.getRelationUp<OwnedBy>()
            assertNotNull(owner)
            assertEquals(receiver, owner)
        }
    }

    // 测试可堆叠物品部分转移
    @Test
    fun testTransferPartialStackableItem() {
        val sender = world.entity {}
        val receiver = world.entity {}
        val prefab = itemService.itemPrefab("Stackable Item") {
            it.addTag<Stackable>()
        }
        val item = world.instanceOf(prefab) {
            it.addComponent(Amount(10))
            it.addRelation<OwnedBy>(sender)
        }

        itemService.transferItem(receiver, item, 3)

        // 验证原物品数量减少
        world.entity(item) {
            val remainingAmount = it.getComponent<Amount>()
            assertNotNull(remainingAmount)
            assertEquals(7, remainingAmount.value)
            val owner = it.getRelationUp<OwnedBy>()
            assertNotNull(owner)
            assertEquals(sender, owner)
        }

        // TODO: 验证接收者收到了新物品
        // 由于当前没有查询接收者物品的方法，暂时跳过
    }

    // 测试批量物品转移
    @Test
    fun testTransferItems() {
        val sender = world.entity {}
        val receiver = world.entity {}
        val prefab1 = itemService.itemPrefab("Item 1") {}
        val prefab2 = itemService.itemPrefab("Item 2") {
            it.addTag<Stackable>()
        }
        
        val item1 = world.instanceOf(prefab1) {
            it.addRelation<OwnedBy>(sender)
        }
        val item2 = world.instanceOf(prefab2) {
            it.addComponent(Amount(10))
            it.addRelation<OwnedBy>(sender)
        }

        val itemsToTransfer = mapOf(
            item1 to 1,
            item2 to 3
        )
        
        itemService.transferItems(receiver, itemsToTransfer)

        // 验证物品1所有权转移
        world.entity(item1) {
            val owner = it.getRelationUp<OwnedBy>()
            assertNotNull(owner)
            assertEquals(receiver, owner)
        }

        // 验证物品2剩余数量
        world.entity(item2) {
            val remainingAmount = it.getComponent<Amount>()
            assertNotNull(remainingAmount)
            assertEquals(7, remainingAmount.value)
        }
    }

    // 测试向自己转移物品
    @Test
    fun testTransferItemToSelf() {
        val player = world.entity {}
        val prefab = itemService.itemPrefab("Test Item") {}
        val item = world.instanceOf(prefab) {
            it.addRelation<OwnedBy>(player)
        }

        // 向自己转移物品应该不做任何操作
        itemService.transferItem(player, item, 1)

        // 验证物品所有权不变
        world.entity(item) {
            val owner = it.getRelationUp<OwnedBy>()
            assertNotNull(owner)
            assertEquals(player, owner)
        }
    }
}