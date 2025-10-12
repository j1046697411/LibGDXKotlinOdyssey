package cn.jzl.datastructure.list

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ByteFastListTest {

    // 测试默认构造函数
    @Test
    fun testDefaultConstructor() {
        val list = ByteFastList()
        assertEquals(0, list.size)
    }

    // 测试自定义容量的构造函数
    @Test
    fun testCustomCapacityConstructor() {
        val list = ByteFastList(10)
        assertEquals(0, list.size)
    }

    // 测试通过 set 方法修改元素
    @Test
    fun testSet() {
        val list = ByteFastList()
        list.insertLast(1, 2, 3)

        val oldValue = list.set(1, 22)
        assertEquals(2, oldValue.toInt())
        assertEquals(22, list[1].toInt())
    }

    // 测试 set 方法的索引越界检查
    @Test
    fun testSetWithInvalidIndex() {
        val list = ByteFastList()
        list.insertLast(1)

        assertFailsWith<IndexOutOfBoundsException> { list[-1] = 10 }

        assertFailsWith<IndexOutOfBoundsException> { list[1] = 10 }
    }

    // 测试通过 get 方法获取元素
    @Test
    fun testGet() {
        val list = ByteFastList()
        list.insertLast(10, 20, 30)

        assertEquals(10, list[0].toInt())
        assertEquals(20, list[1].toInt())
        assertEquals(30, list[2].toInt())
    }

    // 测试 get 方法的索引越界检查
    @Test
    fun testGetWithInvalidIndex() {
        val list = ByteFastList()
        list.insertLast(1)

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
        val list = ByteFastList()
        list.insertLast(1, 2, 3, 4)

        val removed = list.removeAt(1)
        assertEquals(2, removed.toInt())
        assertEquals(3, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(3, list[1].toInt())
        assertEquals(4, list[2].toInt())
    }

    // 测试 removeAt 方法的索引越界检查
    @Test
    fun testRemoveAtWithInvalidIndex() {
        val list = ByteFastList()
        list.insertLast(1)

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
        val list = ByteFastList()
        list.insertLast(1)
        list.insertLast(2)

        assertEquals(2, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(2, list[1].toInt())
    }

    // 测试通过 insertLast 添加多个元素
    @Test
    fun testInsertLastMultipleElements() {
        val list = ByteFastList()
        list.insertLast(1, 2, 3)

        assertEquals(3, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(2, list[1].toInt())
        assertEquals(3, list[2].toInt())
    }

    // 测试通过 insert 在指定位置添加元素
    @Test
    fun testInsertAtIndex() {
        val list = ByteFastList()
        list.insertLast(1, 3)
        list.insert(1, 2)

        assertEquals(3, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(2, list[1].toInt())
        assertEquals(3, list[2].toInt())
    }

    // 测试 insert 方法的索引越界检查
    @Test
    fun testInsertWithInvalidIndex() {
        val list = ByteFastList()
        list.insertLast(1)

        assertFailsWith<IndexOutOfBoundsException> {
            list.insert(-1, 10)
        }

        assertFailsWith<IndexOutOfBoundsException> {
            list.insert(2, 10)
        }
    }

    // 测试通过 insert 在指定位置添加多个元素
    @Test
    fun testInsertMultipleElementsAtIndex() {
        val list = ByteFastList()
        list.insertLast(1, 4)
        list.insert(1, 2, 3)

        assertEquals(4, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(2, list[1].toInt())
        assertEquals(3, list[2].toInt())
        assertEquals(4, list[3].toInt())
    }

    // 测试 insertLastAll 方法（从 Iterable 添加元素）
    @Test
    fun testInsertLastAllFromIterable() {
        val list = ByteFastList()
        list.insertLast(1, 2)
        val otherList = listOf<Byte>(3, 4, 5)
        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(2, list[1].toInt())
        assertEquals(3, list[2].toInt())
        assertEquals(4, list[3].toInt())
        assertEquals(5, list[4].toInt())
    }

    // 测试 insertLastAll 方法（从 ByteArray 添加元素）
    @Test
    fun testInsertLastAllFromByteArray() {
        val list = ByteFastList()
        list.insertLast(1, 2)
        val array = byteArrayOf(3, 4, 5)
        list.insertLastAll(array)

        assertEquals(5, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(2, list[1].toInt())
        assertEquals(3, list[2].toInt())
        assertEquals(4, list[3].toInt())
        assertEquals(5, list[4].toInt())
    }

    // 测试 insertLastAll 方法（从 ByteFastList 添加元素）
    @Test
    fun testInsertLastAllFromByteFastList() {
        val list = ByteFastList()
        list.insertLast(1, 2)

        val otherList = ByteFastList()
        otherList.insertLast(3, 4, 5)

        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(2, list[1].toInt())
        assertEquals(3, list[2].toInt())
        assertEquals(4, list[3].toInt())
        assertEquals(5, list[4].toInt())
    }

    // 测试 insertAll 方法（在指定位置从 Iterable 添加元素）
    @Test
    fun testInsertAllAtIndexFromIterable() {
        val list = ByteFastList()
        list.insertLast(1, 5)
        val otherList = listOf<Byte>(2, 3, 4)
        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(2, list[1].toInt())
        assertEquals(3, list[2].toInt())
        assertEquals(4, list[3].toInt())
        assertEquals(5, list[4].toInt())
    }

    // 测试 insertAll 方法（在指定位置从 ByteArray 添加元素）
    @Test
    fun testInsertAllAtIndexFromByteArray() {
        val list = ByteFastList()
        list.insertLast(1, 5)
        val array = byteArrayOf(2, 3, 4)
        list.insertAll(1, array)

        assertEquals(5, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(2, list[1].toInt())
        assertEquals(3, list[2].toInt())
        assertEquals(4, list[3].toInt())
        assertEquals(5, list[4].toInt())
    }

    // 测试 insertAll 方法（在指定位置从 ByteFastList 添加元素）
    @Test
    fun testInsertAllAtIndexFromByteFastList() {
        val list = ByteFastList()
        list.insertLast(1, 5)

        val otherList = ByteFastList()
        otherList.insertLast(2, 3, 4)

        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals(1, list[0].toInt())
        assertEquals(2, list[1].toInt())
        assertEquals(3, list[2].toInt())
        assertEquals(4, list[3].toInt())
        assertEquals(5, list[4].toInt())
    }

    // 测试 ensure 方法触发的容量扩展
    @Test
    fun testEnsureCapacity() {
        // 从容量 2 开始，然后添加足够多的元素以触发扩展
        val list = ByteFastList(2)

        // 添加 10 个元素，这应该会触发多次扩容
        for (i in 0 until 10) {
            list.insertLast(i.toByte())
        }

        assertEquals(10, list.size)
        // 验证所有元素都被正确存储
        for (i in 0 until 10) {
            assertEquals(i, list[i].toInt())
        }
    }

    // 测试空列表的行为
    @Test
    fun testEmptyListBehavior() {
        val list = ByteFastList()
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
        val list = ByteFastList()
        list.insertLast(1, 2, 3, 4, 5)

        val collected = mutableListOf<Byte>()
        for (element in list) {
            collected.add(element)
        }

        assertContentEquals(listOf<Byte>(1, 2, 3, 4, 5), collected)
    }
}