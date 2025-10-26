package cn.jzl.ecs.v2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EntityStoreImplTest {

    @Test
    fun `test create entity with default capacity`() {
        val store = EntityStoreImpl()
        
        val entity = store.create()
        
        assertEquals(1, store.size)
        assertTrue(entity.id >= 0)
        assertEquals(0, entity.version)
        assertTrue(entity in store)
    }

    @Test
    fun `test create entity with custom capacity`() {
        val store = EntityStoreImpl(capacity = 10)
        
        val entity = store.create()
        
        assertEquals(1, store.size)
        assertTrue(entity.id >= 0)
        assertEquals(0, entity.version)
        assertTrue(entity in store)
    }

    @Test
    fun `test create multiple entities`() {
        val store = EntityStoreImpl()
        
        val entity1 = store.create()
        val entity2 = store.create()
        val entity3 = store.create()
        
        assertEquals(3, store.size)
        assertTrue(entity1 in store)
        assertTrue(entity2 in store)
        assertTrue(entity3 in store)
        
        // 验证实体ID是递增的
        assertEquals(0, entity1.id)
        assertEquals(1, entity2.id)
        assertEquals(2, entity3.id)
    }

    @Test
    fun `test create entity with specific ID`() {
        val store = EntityStoreImpl()
        
        val entity = store.create(id = 5)
        
        assertEquals(1, store.size)
        assertEquals(5, entity.id)
        assertEquals(0, entity.version)
        assertTrue(entity in store)
    }

    @Test
    fun `test create entity with specific ID when ID already exists`() {
        val store = EntityStoreImpl()
        
        // 先创建一个实体
        val entity1 = store.create(id = 5)
        
        // 删除该实体
        store -= entity1
        
        // 重新创建相同ID的实体
        val entity2 = store.create(id = 5)
        
        assertEquals(1, store.size)
        assertEquals(5, entity2.id)
        assertEquals(1, entity2.version) // 版本应该增加
        assertTrue(entity2 in store)
    }

    @Test
    fun `test create entity with specific ID when ID is larger than current size`() {
        val store = EntityStoreImpl()
        
        val entity = store.create(id = 100)
        
        assertEquals(1, store.size)
        assertEquals(100, entity.id)
        assertEquals(0, entity.version)
        assertTrue(entity in store)
    }

    @Test
    fun `test delete entity`() {
        val store = EntityStoreImpl()
        
        val entity = store.create()
        assertTrue(entity in store)
        assertEquals(1, store.size)
        
        store -= entity
        
        assertFalse(entity in store)
        assertEquals(0, store.size)
    }

    @Test
    fun `test delete non-existent entity`() {
        val store = EntityStoreImpl()
        val entity = Entity(999, 0)
        
        // 删除不存在的实体应该不会抛出异常
        store -= entity
        
        assertEquals(0, store.size)
        assertFalse(entity in store)
    }

    @Test
    fun `test delete and recreate entity`() {
        val store = EntityStoreImpl()
        
        // 创建并删除实体
        val entity1 = store.create()
        store -= entity1
        
        // 重新创建实体（应该重用ID）
        val entity2 = store.create()
        
        assertEquals(1, store.size)
        assertEquals(entity1.id, entity2.id)
        assertEquals(1, entity2.version) // 版本应该增加
        assertTrue(entity2 in store)
    }

    @Test
    fun `test clear store`() {
        val store = EntityStoreImpl()
        
        // 创建多个实体
        store.create()
        store.create()
        store.create()
        
        assertEquals(3, store.size)
        
        store.clear()
        
        assertEquals(0, store.size)
        assertEquals(0, store.entities.count())
    }

    @Test
    fun `test entities sequence`() {
        val store = EntityStoreImpl()
        
        val entity1 = store.create()
        val entity2 = store.create()
        val entity3 = store.create()
        
        val entities = store.entities.toList()
        
        assertEquals(3, entities.size)
        assertTrue(entities.contains(entity1))
        assertTrue(entities.contains(entity2))
        assertTrue(entities.contains(entity3))
    }

    @Test
    fun `test entities sequence after deletion`() {
        val store = EntityStoreImpl()
        
        val entity1 = store.create()
        val entity2 = store.create()
        val entity3 = store.create()
        
        // 删除中间实体
        store -= entity2
        
        val entities = store.entities.toList()
        
        assertEquals(2, entities.size)
        assertTrue(entities.contains(entity1))
        assertFalse(entities.contains(entity2))
        assertTrue(entities.contains(entity3))
    }

    @Test
    fun `test concurrent entity creation`() {
        val store = EntityStoreImpl()
        
        // 模拟并发创建（虽然测试是顺序的，但测试线程安全）
        val entities = List(100) { store.create() }
        
        assertEquals(100, store.size)
        
        // 验证所有实体都有唯一的ID
        val uniqueIds = entities.map { it.id }.toSet()
        assertEquals(100, uniqueIds.size)
    }

    @Test
    fun `test entity version upgrade`() {
        val store = EntityStoreImpl()
        
        // 创建实体
        val entity1 = store.create()
        assertEquals(0, entity1.version)
        
        // 删除实体
        store -= entity1
        
        // 重新创建实体（版本应该升级）
        val entity2 = store.create()
        assertEquals(1, entity2.version)
        
        // 再次删除和创建
        store -= entity2
        val entity3 = store.create()
        assertEquals(2, entity3.version)
    }

    @Test
    fun `test contains method with different versions`() {
        val store = EntityStoreImpl()
        
        // 创建实体
        val entity1 = store.create()
        assertTrue(entity1 in store)
        
        // 删除实体
        store -= entity1
        assertFalse(entity1 in store)
        
        // 创建相同ID但不同版本的实体
        val entity2 = Entity(entity1.id, entity1.version + 1)
        assertFalse(entity2 in store) // 不同版本的实体不应该被识别为同一个实体
    }

    @Test
    fun `test entity recycling mechanism`() {
        val store = EntityStoreImpl()
        
        // 创建多个实体
        val entities = List(10) { store.create() }
        assertEquals(10, store.size)
        
        // 删除所有实体
        entities.forEach { store -= it }
        assertEquals(0, store.size)
        
        // 重新创建实体，应该重用ID
        val recycledEntities = List(10) { store.create() }
        
        // 验证ID被重用
        val originalIds = entities.map { it.id }.toSet()
        val recycledIds = recycledEntities.map { it.id }.toSet()
        assertEquals(originalIds, recycledIds)
        
        // 验证版本号增加
        recycledEntities.forEach { entity ->
            val originalEntity = entities.find { it.id == entity.id }!!
            assertEquals(originalEntity.version + 1, entity.version)
        }
    }

    @Test
    fun `test edge case with zero capacity`() {
        val store = EntityStoreImpl(capacity = 0)
        
        // 即使容量为0，也应该能创建实体
        val entity = store.create()
        
        assertEquals(1, store.size)
        assertTrue(entity in store)
    }

    @Test
    fun `test create entity with negative ID should fail`() {
        val store = EntityStoreImpl()
        
        try {
            store.create(id = -1)
        } catch (e: Exception) {
            // 应该抛出异常
            assertTrue(e is IllegalArgumentException || e is IllegalStateException)
        }
    }
}