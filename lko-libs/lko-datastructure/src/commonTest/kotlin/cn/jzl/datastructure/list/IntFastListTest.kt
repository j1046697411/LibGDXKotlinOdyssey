package cn.jzl.datastructure.list

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IntFastListTest {

    // 测试默认构造函数
    @Test
    fun testDefaultConstructor() {
        val list = IntFastList()
        assertEquals(0, list.size)
    }

    // 测试自定义容量的构造函数
    @Test
    fun testCustomCapacityConstructor() {
        val list = IntFastList(10)
        assertEquals(0, list.size)
    }

    // 测试通过 set 方法修改元素
    @Test
    fun testSet() {
        val list = IntFastList()
        list.insertLast(1, 2, 3)

        val oldValue = list.set(1, 22)
        assertEquals(2, oldValue)
        assertEquals(22, list[1])
    }

    // 测试 set 方法的索引越界检查
    @Test
    fun testSetWithInvalidIndex() {
        val list = IntFastList()
        list.insertLast(1)

        assertFailsWith<IndexOutOfBoundsException> { list[-1] = 10 }

        assertFailsWith<IndexOutOfBoundsException> { list[1] = 10 }
    }

    // 测试通过 get 方法获取元素
    @Test
    fun testGet() {
        val list = IntFastList()
        list.insertLast(10, 20, 30)

        assertEquals(10, list[0])
        assertEquals(20, list[1])
        assertEquals(30, list[2])
    }

    // 测试 get 方法的索引越界检查
    @Test
    fun testGetWithInvalidIndex() {
        val list = IntFastList()
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
        val list = IntFastList()
        list.insertLast(1, 2, 3, 4)

        val removed = list.removeAt(1)
        assertEquals(2, removed)
        assertEquals(3, list.size)
        assertEquals(1, list[0])
        assertEquals(3, list[1])
        assertEquals(4, list[2])
    }

    // 测试 removeAt 方法的索引越界检查
    @Test
    fun testRemoveAtWithInvalidIndex() {
        val list = IntFastList()
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
        val list = IntFastList()
        list.insertLast(1)
        list.insertLast(2)

        assertEquals(2, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
    }

    // 测试通过 insertLast 添加多个元素
    @Test
    fun testInsertLastMultipleElements() {
        val list = IntFastList()
        list.insertLast(1, 2, 3)

        assertEquals(3, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
    }

    // 测试通过 insert 在指定位置添加元素
    @Test
    fun testInsertAtIndex() {
        val list = IntFastList()
        list.insertLast(1, 3)
        list.insert(1, 2)

        assertEquals(3, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
    }

    // 测试 insert 方法的索引越界检查
    @Test
    fun testInsertWithInvalidIndex() {
        val list = IntFastList()
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
        val list = IntFastList()
        list.insertLast(1, 4)
        list.insert(1, 2, 3)

        assertEquals(4, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
        assertEquals(4, list[3])
    }

    // 测试 insertLastAll 方法（从 Iterable 添加元素）
    @Test
    fun testInsertLastAllFromIterable() {
        val list = IntFastList()
        list.insertLast(1, 2)
        val otherList = listOf(3, 4, 5)
        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
        assertEquals(4, list[3])
        assertEquals(5, list[4])
    }

    // 测试 insertLastAll 方法（从 IntArray 添加元素）
    @Test
    fun testInsertLastAllFromIntArray() {
        val list = IntFastList()
        list.insertLast(1, 2)
        val array = intArrayOf(3, 4, 5)
        list.insertLastAll(array)

        assertEquals(5, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
        assertEquals(4, list[3])
        assertEquals(5, list[4])
    }

    // 测试 insertLastAll 方法（从 IntFastList 添加元素）
    @Test
    fun testInsertLastAllFromIntFastList() {
        val list = IntFastList()
        list.insertLast(1, 2)

        val otherList = IntFastList()
        otherList.insertLast(3, 4, 5)

        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
        assertEquals(4, list[3])
        assertEquals(5, list[4])
    }

    // 测试 insertAll 方法（在指定位置从 Iterable 添加元素）
    @Test
    fun testInsertAllAtIndexFromIterable() {
        val list = IntFastList()
        list.insertLast(1, 5)
        val otherList = listOf(2, 3, 4)
        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
        assertEquals(4, list[3])
        assertEquals(5, list[4])
    }

    // 测试 insertAll 方法（在指定位置从 IntArray 添加元素）
    @Test
    fun testInsertAllAtIndexFromIntArray() {
        val list = IntFastList()
        list.insertLast(1, 5)
        val array = intArrayOf(2, 3, 4)
        list.insertAll(1, array)

        assertEquals(5, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
        assertEquals(4, list[3])
        assertEquals(5, list[4])
    }

    // 测试 insertAll 方法（在指定位置从 IntFastList 添加元素）
    @Test
    fun testInsertAllAtIndexFromIntFastList() {
        val list = IntFastList()
        list.insertLast(1, 5)

        val otherList = IntFastList()
        otherList.insertLast(2, 3, 4)

        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
        assertEquals(4, list[3])
        assertEquals(5, list[4])
    }

    // 测试 ensure 方法触发的容量扩展
    @Test
    fun testEnsureCapacity() {
        // 从容量 2 开始，然后添加足够多的元素以触发扩展
        val list = IntFastList(2)

        // 添加 10 个元素，这应该会触发多次扩容
        for (i in 0 until 10) {
            list.insertLast(i)
        }

        assertEquals(10, list.size)
        // 验证所有元素都被正确存储
        for (i in 0 until 10) {
            assertEquals(i, list[i])
        }
    }

    // 测试空列表的行为
    @Test
    fun testEmptyListBehavior() {
        val list = IntFastList()
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
        val list = IntFastList()
        list.insertLast(1, 2, 3, 4, 5)

        val collected = mutableListOf<Int>()
        for (element in list) {
            collected.add(element)
        }

        assertContentEquals(listOf(1, 2, 3, 4, 5), collected)
    }

    // 额外用例：ensureCapacity 填充并扩容；再次调用较小容量不改变内容
    @Test
    fun ensureCapacity_should_fill_and_resize() {
        val list = IntFastList()
        list.ensureCapacity(5, 7)
        assertEquals(5, list.size)
        for (i in 0 until 5) assertEquals(7, list[i])
    
        list.ensureCapacity(3, 9) // 较小容量不影响当前内容
        assertEquals(5, list.size)
        for (i in 0 until 5) assertEquals(7, list[i])
    }
    
    // 额外用例：fill 在指定范围更新元素
    @Test
    fun fill_should_update_range() {
        val list = IntFastList()
        list.ensureCapacity(6, 0)
        list.fill(1, 2, 5)
        assertEquals(0, list[0])
        assertEquals(0, list[1])
        assertEquals(1, list[2])
        assertEquals(1, list[3])
        assertEquals(1, list[4])
        assertEquals(0, list[5])
    }
    
    // 额外用例：safeInsert 在指定索引按顺序提交
    @Test
    fun safeInsert_should_insert_at_index_in_order() {
        val list = IntFastList()
        list.insertLast(1, 4)
        list.safeInsert(1, 3) {
            unsafeInsert(2)
            unsafeInsert(3)
            unsafeInsert(99)
        }
        assertEquals(5, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
        assertEquals(99, list[3])
        assertEquals(4, list[4])
    }
    
    // 额外用例：safeInsert 计数不匹配抛异常
    @Test
    fun safeInsert_should_throw_when_count_mismatch() {
        val list = IntFastList()
        assertFailsWith<IllegalStateException> {
            list.safeInsert(0, 2) {
                unsafeInsert(1)
            }
        }
    }
    
    // 额外用例：safeInsertLast 在尾部顺序追加
    @Test
    fun safeInsertLast_should_append_in_order() {
        val list = IntFastList()
        list.safeInsertLast(3) {
            unsafeInsert(5)
            unsafeInsert(6)
            unsafeInsert(7)
        }
        assertEquals(3, list.size)
        assertEquals(5, list[0])
        assertEquals(6, list[1])
        assertEquals(7, list[2])
    }
    
    // 额外用例：safeInsertLast 计数不匹配时应保留已插入元素（非事务）
    @Test
    fun safeInsertLast_mismatch_should_keep_inserted_elements() {
        val list = IntFastList()
        val offset = list.size
        assertFailsWith<IllegalStateException> {
            list.safeInsertLast(1) {
                unsafeInsert(100)
                unsafeInsert(200)
            }
        }
        assertEquals(offset + 2, list.size)
        assertEquals(100, list[offset])
        assertEquals(200, list[offset + 1])
    }
    
    // 额外用例：add 在头部/中间/尾部插入并正确移动元素
    @Test
    fun add_should_insert_and_shift() {
        val list = IntFastList()
        list.insertLast(1, 3)
        list.add(1, 2)
        assertEquals(3, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
    
        list.add(0, 0)
        assertEquals(4, list.size)
        assertEquals(0, list[0])
        assertEquals(1, list[1])
        assertEquals(2, list[2])
        assertEquals(3, list[3])
    
        list.add(4, 4)
        assertEquals(5, list.size)
        for (i in 0 until 5) assertEquals(i, list[i])
    }
    
    // 额外用例：insertLastAll/insertAll 处理非 Collection 的 Iterable
    @Test
    fun insertAll_iterable_non_collection_should_work() {
        val list = IntFastList()
        val src = object : Iterable<Int> {
            override fun iterator(): Iterator<Int> = listOf(1, 2, 3).iterator()
        }
        list.insertLastAll(src)
        assertEquals(3, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
    
        val srcEmpty = object : Iterable<Int> {
            override fun iterator(): Iterator<Int> = emptyList<Int>().iterator()
        }
        list.insertAll(1, srcEmpty) // 空 Iterable 不改变内容
        assertEquals(3, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
    }
    
    // 额外用例：insert 多参数重载（4~6 个参数）在当前 size 处插入，避免负长度 copy
    @Test
    fun insert_at_index_multi_parameters_4_to_6() {
        val list = IntFastList()
        list.insertLast(1)
        list.insert(1, 2, 3, 4)
        val idx = list.size
        list.insert(idx, 5, 6)
        assertEquals(6, list.size)
        for (i in 0 until 6) assertEquals(i + 1, list[i])
    }
}