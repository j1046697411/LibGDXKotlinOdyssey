package cn.jzl.datastructure.geom.generic

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GenericVectorListTest {

    // 测试基本的构造和获取向量
    @Test
    fun testBasicOperations() {
        val list = GenericVectorList.int(3) // 3维向量列表
        list.insertLast(ArrayGenericVector(arrayOf(1, 2, 3)))
        list.insertLast(ArrayGenericVector(arrayOf(4, 5, 6)))

        assertEquals(2, list.size)
        assertEquals(3, list.dimensions)
        
        val vector1 = list[0]
        assertEquals(1, vector1[0])
        assertEquals(2, vector1[1])
        assertEquals(3, vector1[2])
        
        val vector2 = list[1]
        assertEquals(4, vector2[0])
        assertEquals(5, vector2[1])
        assertEquals(6, vector2[2])
    }

    // 测试通过索引和维度获取元素
    @Test
    fun testGetByIndexAndDimension() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(10, 20)))
        list.insertLast(ArrayGenericVector(arrayOf(30, 40)))

        assertEquals(10, list[0, 0])
        assertEquals(20, list[0, 1])
        assertEquals(30, list[1, 0])
        assertEquals(40, list[1, 1])
    }

    // 测试通过索引和维度设置元素
    @Test
    fun testSetByIndexAndDimension() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))

        list[0, 0] = 100
        list[0, 1] = 200

        assertEquals(100, list[0, 0])
        assertEquals(200, list[0, 1])
    }

    // 测试修改向量
    @Test
    fun testSetVector() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))
        list.insertLast(ArrayGenericVector(arrayOf(3, 4)))

        val oldVector = list.set(1, ArrayGenericVector(arrayOf(30, 40)))
        assertEquals(3, oldVector[0])
        assertEquals(4, oldVector[1])

        assertEquals(30, list[1, 0])
        assertEquals(40, list[1, 1])
    }

    // 测试删除向量
    @Test
    fun testRemoveAt() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))
        list.insertLast(ArrayGenericVector(arrayOf(3, 4)))
        list.insertLast(ArrayGenericVector(arrayOf(5, 6)))

        val removed = list.removeAt(1)
        assertEquals(3, removed[0])
        assertEquals(4, removed[1])
        assertEquals(2, list.size)
        assertEquals(1, list[0, 0])
        assertEquals(2, list[0, 1])
        assertEquals(5, list[1, 0])
        assertEquals(6, list[1, 1])
    }

    // 测试插入单个向量到末尾
    @Test
    fun testInsertLast() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))
        list.insertLast(ArrayGenericVector(arrayOf(3, 4)))

        assertEquals(2, list.size)
        assertEquals(1, list[0, 0])
        assertEquals(2, list[0, 1])
        assertEquals(3, list[1, 0])
        assertEquals(4, list[1, 1])
    }

    // 测试插入多个向量到末尾
    @Test
    fun testInsertLastMultiple() {
        val list = GenericVectorList.int(2)
        list.insertLast(
            ArrayGenericVector(arrayOf(1, 2)),
            ArrayGenericVector(arrayOf(3, 4)),
            ArrayGenericVector(arrayOf(5, 6))
        )

        assertEquals(3, list.size)
        assertEquals(1, list[0, 0])
        assertEquals(2, list[0, 1])
        assertEquals(3, list[1, 0])
        assertEquals(4, list[1, 1])
        assertEquals(5, list[2, 0])
        assertEquals(6, list[2, 1])
    }

    // 测试插入单个向量到指定位置
    @Test
    fun testInsert() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))
        list.insertLast(ArrayGenericVector(arrayOf(5, 6)))

        list.insert(1, ArrayGenericVector(arrayOf(3, 4)))

        assertEquals(3, list.size)
        assertEquals(1, list[0, 0])
        assertEquals(2, list[0, 1])
        assertEquals(3, list[1, 0])
        assertEquals(4, list[1, 1])
        assertEquals(5, list[2, 0])
        assertEquals(6, list[2, 1])
    }

    // 测试插入多个向量到指定位置
    @Test
    fun testInsertMultiple() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))
        list.insertLast(ArrayGenericVector(arrayOf(7, 8)))

        list.insert(
            1,
            ArrayGenericVector(arrayOf(3, 4)),
            ArrayGenericVector(arrayOf(5, 6))
        )

        assertEquals(4, list.size)
        assertEquals(1, list[0, 0])
        assertEquals(2, list[0, 1])
        assertEquals(3, list[1, 0])
        assertEquals(4, list[1, 1])
        assertEquals(5, list[2, 0])
        assertEquals(6, list[2, 1])
        assertEquals(7, list[3, 0])
        assertEquals(8, list[3, 1])
    }

    // 测试插入所有元素
    @Test
    fun testInsertAll() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))

        val vectors = listOf(
            ArrayGenericVector(arrayOf(3, 4)),
            ArrayGenericVector(arrayOf(5, 6))
        )
        list.insertAll(1, vectors)

        assertEquals(3, list.size)
        assertEquals(1, list[0, 0])
        assertEquals(2, list[0, 1])
        assertEquals(3, list[1, 0])
        assertEquals(4, list[1, 1])
        assertEquals(5, list[2, 0])
        assertEquals(6, list[2, 1])
    }

    // 测试工厂方法创建不同类型的向量列表
    @Test
    fun testFactoryMethods() {
        val intList = GenericVectorList.int(2)
        intList.insertLast(ArrayGenericVector(arrayOf(1, 2)))
        assertEquals(1, intList[0, 0])
        
        val floatList = GenericVectorList.float(2)
        floatList.insertLast(ArrayGenericVector(arrayOf(1.5f, 2.5f)))
        assertEquals(1.5f, floatList[0, 0])
        
        val doubleList = GenericVectorList.double(2)
        doubleList.insertLast(ArrayGenericVector(arrayOf(1.5, 2.5)))
        assertEquals(1.5, doubleList[0, 0])
        
        val longList = GenericVectorList.long(2)
        longList.insertLast(ArrayGenericVector(arrayOf(1L, 2L)))
        assertEquals(1L, longList[0, 0])
        
        val shortList = GenericVectorList.short(2)
        shortList.insertLast(ArrayGenericVector(arrayOf(1.toShort(), 2.toShort())))
        assertEquals(1.toShort(), shortList[0, 0])
        
        val byteList = GenericVectorList.byte(2)
        byteList.insertLast(ArrayGenericVector(arrayOf(1.toByte(), 2.toByte())))
        assertEquals(1.toByte(), byteList[0, 0])
        
        val charList = GenericVectorList.char(2)
        charList.insertLast(ArrayGenericVector(arrayOf('a', 'b')))
        assertEquals('a', charList[0, 0])
    }

    // 测试索引越界检查 - get 方法
    @Test
    fun testGetWithInvalidIndex() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))

        assertFailsWith<IndexOutOfBoundsException> { list[-1] }
        assertFailsWith<IndexOutOfBoundsException> { list[1] }
    }

    // 测试索引越界检查 - get(index, dimension) 方法
    @Test
    fun testGetWithInvalidIndexOrDimension() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))

        assertFailsWith<IndexOutOfBoundsException> { list[-1, 0] }
        assertFailsWith<IndexOutOfBoundsException> { list[1, 0] }
        assertFailsWith<IndexOutOfBoundsException> { list[0, -1] }
        assertFailsWith<IndexOutOfBoundsException> { list[0, 2] }
    }

    // 测试索引越界检查 - set 方法
    @Test
    fun testSetWithInvalidIndex() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))

        assertFailsWith<IndexOutOfBoundsException> { list[-1] = ArrayGenericVector(arrayOf(3, 4)) }
        assertFailsWith<IndexOutOfBoundsException> { list[1] = ArrayGenericVector(arrayOf(3, 4)) }
    }

    // 测试索引越界检查 - removeAt 方法
    @Test
    fun testRemoveAtWithInvalidIndex() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))

        assertFailsWith<IndexOutOfBoundsException> { list.removeAt(-1) }
        assertFailsWith<IndexOutOfBoundsException> { list.removeAt(1) }
    }

    // 测试修改可变向量
    @Test
    fun testModifyMutableVector() {
        val list = GenericVectorList.int(2)
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))

        val mutableVector = list[0]
        mutableVector[0] = 100
        mutableVector[1] = 200

        assertEquals(100, list[0, 0])
        assertEquals(200, list[0, 1])
    }

    // 测试向量维度一致性检查
    @Test
    fun testVectorDimensionConsistency() {
        val list = GenericVectorList.int(2) // 2维向量列表
        list.insertLast(ArrayGenericVector(arrayOf(1, 2))) // 正确维度的向量

        // 尝试插入维度不匹配的向量应抛出异常
        assertFailsWith<IllegalArgumentException> { 
            list.insertLast(ArrayGenericVector(arrayOf(1, 2, 3))) // 3维向量
        }
    }

    // 测试空列表的行为
    @Test
    fun testEmptyList() {
        val list = GenericVectorList.int(2)
        assertEquals(0, list.size)
        assertTrue(list.isEmpty())
        
        list.insertLast(ArrayGenericVector(arrayOf(1, 2)))
        assertFalse(list.isEmpty())
        
        list.removeAt(0)
        assertTrue(list.isEmpty())
    }
}