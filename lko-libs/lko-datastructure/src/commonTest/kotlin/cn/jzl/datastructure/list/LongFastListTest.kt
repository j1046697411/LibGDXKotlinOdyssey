package cn.jzl.datastructure.list

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LongFastListTest {

    // 测试默认构造函数
    @Test
    fun testDefaultConstructor() {
        val list = LongFastList()
        assertEquals(0, list.size)
    }

    // 测试自定义容量的构造函数
    @Test
    fun testCustomCapacityConstructor() {
        val list = LongFastList(10)
        assertEquals(0, list.size)
    }

    // 测试通过 set 方法修改元素
    @Test
    fun testSet() {
        val list = LongFastList()
        list.insertLast(1L, 2L, 3L)

        val oldValue = list.set(1, 22L)
        assertEquals(2L, oldValue)
        assertEquals(22L, list[1])
    }

    // 测试 set 方法的索引越界检查
    @Test
    fun testSetWithInvalidIndex() {
        val list = LongFastList()
        list.insertLast(1L)

        assertFailsWith<IndexOutOfBoundsException> { list[-1] = 10L }

        assertFailsWith<IndexOutOfBoundsException> { list[1] = 10L }
    }

    // 测试通过 get 方法获取元素
    @Test
    fun testGet() {
        val list = LongFastList()
        list.insertLast(10L, 20L, 30L)

        assertEquals(10L, list[0])
        assertEquals(20L, list[1])
        assertEquals(30L, list[2])
    }

    // 测试 get 方法的索引越界检查
    @Test
    fun testGetWithInvalidIndex() {
        val list = LongFastList()
        list.insertLast(1L)

        assertFailsWith<IndexOutOfBoundsException> {
            list[-1]
        }

        assertFailsWith<IndexOutOfBoundsException> {
            list[1]
        }
    }

    // 测试 removeAt 方法
    @Test
    fun testRemoveAt() {
        val list = LongFastList()
        list.insertLast(1L, 2L, 3L, 4L)

        val removed = list.removeAt(1)
        assertEquals(2L, removed)
        assertEquals(3, list.size)
        assertEquals(1L, list[0])
        assertEquals(3L, list[1])
        assertEquals(4L, list[2])
    }

    // 测试 removeAt 方法的索引越界检查
    @Test
    fun testRemoveAtWithInvalidIndex() {
        val list = LongFastList()
        list.insertLast(1L)

        assertFailsWith<IndexOutOfBoundsException> {
            list.removeAt(-1)
        }

        assertFailsWith<IndexOutOfBoundsException> {
            list.removeAt(1)
        }
    }

    // 测试通过 insertLast 添加单个元素
    @Test
    fun testInsertLastSingleElement() {
        val list = LongFastList()
        list.insertLast(1L)
        list.insertLast(2L)

        assertEquals(2, list.size)
        assertEquals(1L, list[0])
        assertEquals(2L, list[1])
    }

    // 测试通过 insertLast 添加多个元素
    @Test
    fun testInsertLastMultipleElements() {
        val list = LongFastList()
        list.insertLast(1L, 2L, 3L)

        assertEquals(3, list.size)
        assertEquals(1L, list[0])
        assertEquals(2L, list[1])
        assertEquals(3L, list[2])
    }

    // 测试通过 insert 在指定位置添加元素
    @Test
    fun testInsertAtIndex() {
        val list = LongFastList()
        list.insertLast(1L, 3L)
        list.insert(1, 2L)

        assertEquals(3, list.size)
        assertEquals(1L, list[0])
        assertEquals(2L, list[1])
        assertEquals(3L, list[2])
    }

    // 测试 insert 方法的索引越界检查
    @Test
    fun testInsertWithInvalidIndex() {
        val list = LongFastList()
        list.insertLast(1L)

        assertFailsWith<IndexOutOfBoundsException> {
            list.insert(-1, 10L)
        }

        assertFailsWith<IndexOutOfBoundsException> {
            list.insert(2, 10L)
        }
    }

    // 测试通过 insert 在指定位置添加多个元素
    @Test
    fun testInsertMultipleElementsAtIndex() {
        val list = LongFastList()
        list.insertLast(1L, 4L)
        list.insert(1, 2L, 3L)

        assertEquals(4, list.size)
        assertEquals(1L, list[0])
        assertEquals(2L, list[1])
        assertEquals(3L, list[2])
        assertEquals(4L, list[3])
    }

    // 测试 insertLastAll 方法（从 Iterable 添加元素）
    @Test
    fun testInsertLastAllFromIterable() {
        val list = LongFastList()
        list.insertLast(1L, 2L)
        val otherList = listOf(3L, 4L, 5L)
        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals(1L, list[0])
        assertEquals(2L, list[1])
        assertEquals(3L, list[2])
        assertEquals(4L, list[3])
        assertEquals(5L, list[4])
    }

    // 测试 insertLastAll 方法（从 LongArray 添加元素）
    @Test
    fun testInsertLastAllFromLongArray() {
        val list = LongFastList()
        list.insertLast(1L, 2L)
        val array = longArrayOf(3L, 4L, 5L)
        list.insertLastAll(array)

        assertEquals(5, list.size)
        assertEquals(1L, list[0])
        assertEquals(2L, list[1])
        assertEquals(3L, list[2])
        assertEquals(4L, list[3])
        assertEquals(5L, list[4])
    }

    // 测试 insertLastAll 方法（从 LongFastList 添加元素）
    @Test
    fun testInsertLastAllFromLongFastList() {
        val list = LongFastList()
        list.insertLast(1L, 2L)

        val otherList = LongFastList()
        otherList.insertLast(3L, 4L, 5L)

        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals(1L, list[0])
        assertEquals(2L, list[1])
        assertEquals(3L, list[2])
        assertEquals(4L, list[3])
        assertEquals(5L, list[4])
    }

    // 测试 insertAll 方法（在指定位置从 Iterable 添加元素）
    @Test
    fun testInsertAllAtIndexFromIterable() {
        val list = LongFastList()
        list.insertLast(1L, 5L)
        val otherList = listOf(2L, 3L, 4L)
        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals(1L, list[0])
        assertEquals(2L, list[1])
        assertEquals(3L, list[2])
        assertEquals(4L, list[3])
        assertEquals(5L, list[4])
    }

    // 测试 insertAll 方法（在指定位置从 LongArray 添加元素）
    @Test
    fun testInsertAllAtIndexFromLongArray() {
        val list = LongFastList()
        list.insertLast(1L, 5L)
        val array = longArrayOf(2L, 3L, 4L)
        list.insertAll(1, array)

        assertEquals(5, list.size)
        assertEquals(1L, list[0])
        assertEquals(2L, list[1])
        assertEquals(3L, list[2])
        assertEquals(4L, list[3])
        assertEquals(5L, list[4])
    }

    // 测试 insertAll 方法（在指定位置从 LongFastList 添加元素）
    @Test
    fun testInsertAllAtIndexFromLongFastList() {
        val list = LongFastList()
        list.insertLast(1L, 5L)

        val otherList = LongFastList()
        otherList.insertLast(2L, 3L, 4L)

        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals(1L, list[0])
        assertEquals(2L, list[1])
        assertEquals(3L, list[2])
        assertEquals(4L, list[3])
        assertEquals(5L, list[4])
    }

    // 测试 ensure 方法触发的容量扩展
    @Test
    fun testEnsureCapacity() {
        // 从容量 2 开始，然后添加足够多的元素以触发扩展
        val list = LongFastList(2)

        // 添加 10 个元素，这应该会触发多次扩容
        for (i in 0 until 10) {
            list.insertLast(i.toLong())
        }

        assertEquals(10, list.size)
        // 验证所有元素都被正确存储
        for (i in 0 until 10) {
            assertEquals(i.toLong(), list[i])
        }
    }

    // 测试空列表的行为
    @Test
    fun testEmptyListBehavior() {
        val list = LongFastList()
        assertEquals(0, list.size)

        assertFailsWith<IndexOutOfBoundsException> {
            list[0]
        }

        assertFailsWith<IndexOutOfBoundsException> {
            list.removeAt(0)
        }
    }

    // 测试迭代功能
    @Test
    fun testIteration() {
        val list = LongFastList()
        list.insertLast(1L, 2L, 3L, 4L, 5L)

        val collected = mutableListOf<Long>()
        for (element in list) {
            collected.add(element)
        }

        assertContentEquals(listOf(1L, 2L, 3L, 4L, 5L), collected)
    }
}