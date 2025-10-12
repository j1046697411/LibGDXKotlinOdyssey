package cn.jzl.datastructure.list

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FloatFastListTest {

    // 测试默认构造函数
    @Test
    fun testDefaultConstructor() {
        val list = FloatFastList()
        assertEquals(0, list.size)
    }

    // 测试自定义容量的构造函数
    @Test
    fun testCustomCapacityConstructor() {
        val list = FloatFastList(10)
        assertEquals(0, list.size)
    }

    // 测试通过 set 方法修改元素
    @Test
    fun testSet() {
        val list = FloatFastList()
        list.insertLast(1.0f, 2.0f, 3.0f)

        val oldValue = list.set(1, 22.0f)
        assertEquals(2.0f, oldValue)
        assertEquals(22.0f, list[1])
    }

    // 测试 set 方法的索引越界检查
    @Test
    fun testSetWithInvalidIndex() {
        val list = FloatFastList()
        list.insertLast(1.0f)

        assertFailsWith<IndexOutOfBoundsException> { list[-1] = 10.0f }

        assertFailsWith<IndexOutOfBoundsException> { list[1] = 10.0f }
    }

    // 测试通过 get 方法获取元素
    @Test
    fun testGet() {
        val list = FloatFastList()
        list.insertLast(10.0f, 20.0f, 30.0f)

        assertEquals(10.0f, list[0])
        assertEquals(20.0f, list[1])
        assertEquals(30.0f, list[2])
    }

    // 测试 get 方法的索引越界检查
    @Test
    fun testGetWithInvalidIndex() {
        val list = FloatFastList()
        list.insertLast(1.0f)

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
        val list = FloatFastList()
        list.insertLast(1.0f, 2.0f, 3.0f, 4.0f)

        val removed = list.removeAt(1)
        assertEquals(2.0f, removed)
        assertEquals(3, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(3.0f, list[1])
        assertEquals(4.0f, list[2])
    }

    // 测试 removeAt 方法的索引越界检查
    @Test
    fun testRemoveAtWithInvalidIndex() {
        val list = FloatFastList()
        list.insertLast(1.0f)

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
        val list = FloatFastList()
        list.insertLast(1.0f)
        list.insertLast(2.0f)

        assertEquals(2, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(2.0f, list[1])
    }

    // 测试通过 insertLast 添加多个元素
    @Test
    fun testInsertLastMultipleElements() {
        val list = FloatFastList()
        list.insertLast(1.0f, 2.0f, 3.0f)

        assertEquals(3, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(2.0f, list[1])
        assertEquals(3.0f, list[2])
    }

    // 测试通过 insert 在指定位置添加元素
    @Test
    fun testInsertAtIndex() {
        val list = FloatFastList()
        list.insertLast(1.0f, 3.0f)
        list.insert(1, 2.0f)

        assertEquals(3, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(2.0f, list[1])
        assertEquals(3.0f, list[2])
    }

    // 测试 insert 方法的索引越界检查
    @Test
    fun testInsertWithInvalidIndex() {
        val list = FloatFastList()
        list.insertLast(1.0f)

        assertFailsWith<IndexOutOfBoundsException> {
            list.insert(-1, 10.0f)
        }

        assertFailsWith<IndexOutOfBoundsException> {
            list.insert(2, 10.0f)
        }
    }

    // 测试通过 insert 在指定位置添加多个元素
    @Test
    fun testInsertMultipleElementsAtIndex() {
        val list = FloatFastList()
        list.insertLast(1.0f, 4.0f)
        list.insert(1, 2.0f, 3.0f)

        assertEquals(4, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(2.0f, list[1])
        assertEquals(3.0f, list[2])
        assertEquals(4.0f, list[3])
    }

    // 测试 insertLastAll 方法（从 Iterable 添加元素）
    @Test
    fun testInsertLastAllFromIterable() {
        val list = FloatFastList()
        list.insertLast(1.0f, 2.0f)
        val otherList = listOf(3.0f, 4.0f, 5.0f)
        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(2.0f, list[1])
        assertEquals(3.0f, list[2])
        assertEquals(4.0f, list[3])
        assertEquals(5.0f, list[4])
    }

    // 测试 insertLastAll 方法（从 FloatArray 添加元素）
    @Test
    fun testInsertLastAllFromFloatArray() {
        val list = FloatFastList()
        list.insertLast(1.0f, 2.0f)
        val array = floatArrayOf(3.0f, 4.0f, 5.0f)
        list.insertLastAll(array)

        assertEquals(5, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(2.0f, list[1])
        assertEquals(3.0f, list[2])
        assertEquals(4.0f, list[3])
        assertEquals(5.0f, list[4])
    }

    // 测试 insertLastAll 方法（从 FloatFastList 添加元素）
    @Test
    fun testInsertLastAllFromFloatFastList() {
        val list = FloatFastList()
        list.insertLast(1.0f, 2.0f)

        val otherList = FloatFastList()
        otherList.insertLast(3.0f, 4.0f, 5.0f)

        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(2.0f, list[1])
        assertEquals(3.0f, list[2])
        assertEquals(4.0f, list[3])
        assertEquals(5.0f, list[4])
    }

    // 测试 insertAll 方法（在指定位置从 Iterable 添加元素）
    @Test
    fun testInsertAllAtIndexFromIterable() {
        val list = FloatFastList()
        list.insertLast(1.0f, 5.0f)
        val otherList = listOf(2.0f, 3.0f, 4.0f)
        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(2.0f, list[1])
        assertEquals(3.0f, list[2])
        assertEquals(4.0f, list[3])
        assertEquals(5.0f, list[4])
    }

    // 测试 insertAll 方法（在指定位置从 FloatArray 添加元素）
    @Test
    fun testInsertAllAtIndexFromFloatArray() {
        val list = FloatFastList()
        list.insertLast(1.0f, 5.0f)
        val array = floatArrayOf(2.0f, 3.0f, 4.0f)
        list.insertAll(1, array)

        assertEquals(5, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(2.0f, list[1])
        assertEquals(3.0f, list[2])
        assertEquals(4.0f, list[3])
        assertEquals(5.0f, list[4])
    }

    // 测试 insertAll 方法（在指定位置从 FloatFastList 添加元素）
    @Test
    fun testInsertAllAtIndexFromFloatFastList() {
        val list = FloatFastList()
        list.insertLast(1.0f, 5.0f)

        val otherList = FloatFastList()
        otherList.insertLast(2.0f, 3.0f, 4.0f)

        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals(1.0f, list[0])
        assertEquals(2.0f, list[1])
        assertEquals(3.0f, list[2])
        assertEquals(4.0f, list[3])
        assertEquals(5.0f, list[4])
    }

    // 测试 ensure 方法触发的容量扩展
    @Test
    fun testEnsureCapacity() {
        // 从容量 2 开始，然后添加足够多的元素以触发扩展
        val list = FloatFastList(2)

        // 添加 10 个元素，这应该会触发多次扩容
        for (i in 0 until 10) {
            list.insertLast(i.toFloat())
        }

        assertEquals(10, list.size)
        // 验证所有元素都被正确存储
        for (i in 0 until 10) {
            assertEquals(i.toFloat(), list[i])
        }
    }

    // 测试空列表的行为
    @Test
    fun testEmptyListBehavior() {
        val list = FloatFastList()
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
        val list = FloatFastList()
        list.insertLast(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)

        val collected = mutableListOf<Float>()
        for (element in list) {
            collected.add(element)
        }

        assertContentEquals(listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f), collected)
    }
}