package cn.jzl.datastructure.list

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CharFastListTest {

    // 测试默认构造函数
    @Test
    fun testDefaultConstructor() {
        val list = CharFastList()
        assertEquals(0, list.size)
    }

    // 测试自定义容量的构造函数
    @Test
    fun testCustomCapacityConstructor() {
        val list = CharFastList(10)
        assertEquals(0, list.size)
    }

    // 测试通过 set 方法修改元素
    @Test
    fun testSet() {
        val list = CharFastList()
        list.insertLast('a', 'b', 'c')

        val oldValue = list.set(1, 'B')
        assertEquals('b', oldValue)
        assertEquals('B', list[1])
    }

    // 测试 set 方法的索引越界检查
    @Test
    fun testSetWithInvalidIndex() {
        val list = CharFastList()
        list.insertLast('a')

        assertFailsWith<IndexOutOfBoundsException> { list[-1] = 'x' }

        assertFailsWith<IndexOutOfBoundsException> { list[1] = 'x' }
    }

    // 测试通过 get 方法获取元素
    @Test
    fun testGet() {
        val list = CharFastList()
        list.insertLast('a', 'b', 'c')

        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
    }

    // 测试 get 方法的索引越界检查
    @Test
    fun testGetWithInvalidIndex() {
        val list = CharFastList()
        list.insertLast('a')

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
        val list = CharFastList()
        list.insertLast('a', 'b', 'c', 'd')

        val removed = list.removeAt(1)
        assertEquals('b', removed)
        assertEquals(3, list.size)
        assertEquals('a', list[0])
        assertEquals('c', list[1])
        assertEquals('d', list[2])
    }

    // 测试 removeAt 方法的索引越界检查
    @Test
    fun testRemoveAtWithInvalidIndex() {
        val list = CharFastList()
        list.insertLast('a')

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
        val list = CharFastList()
        list.insertLast('a')
        list.insertLast('b')

        assertEquals(2, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
    }

    // 测试通过 insertLast 添加多个元素
    @Test
    fun testInsertLastMultipleElements() {
        val list = CharFastList()
        list.insertLast('a', 'b', 'c')

        assertEquals(3, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
    }

    // 测试通过 insert 在指定位置添加元素
    @Test
    fun testInsertAtIndex() {
        val list = CharFastList()
        list.insertLast('a', 'c')
        list.insert(1, 'b')

        assertEquals(3, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
    }

    // 测试 insert 方法的索引越界检查
    @Test
    fun testInsertWithInvalidIndex() {
        val list = CharFastList()
        list.insertLast('a')

        assertFailsWith<IndexOutOfBoundsException> {
            list.insert(-1, 'x')
        }

        assertFailsWith<IndexOutOfBoundsException> {
            list.insert(2, 'x')
        }
    }

    // 测试通过 insert 在指定位置添加多个元素
    @Test
    fun insert_multiple_at_index_should_shift_existing() {
        val list = CharFastList()
        list.insertLast('a', 'd')
        list.insert(1, 'b', 'c')

        assertEquals(4, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
    }

    // 测试 insertLastAll 方法（从 Iterable 添加元素）
    @Test
    fun insertLastAll_iterable_should_append_all() {
        val list = CharFastList()
        list.insertLast('a', 'b')
        val otherList = listOf('c', 'd', 'e')
        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 insertLastAll 方法（从 CharArray 添加元素）
    @Test
    fun insertLastAll_array_should_append_all() {
        val list = CharFastList()
        list.insertLast('a', 'b')
        val array = charArrayOf('c', 'd', 'e')
        list.insertLastAll(array)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 insertLastAll 方法（从 CharFastList 添加元素）
    @Test
    fun insertLastAll_list_should_append_all() {
        val list = CharFastList()
        list.insertLast('a', 'b')

        val otherList = CharFastList()
        otherList.insertLast('c', 'd', 'e')

        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 insertAll 方法（在指定位置从 Iterable 添加元素）
    @Test
    fun insertAll_iterable_at_index_should_insert_and_shift() {
        val list = CharFastList()
        list.insertLast('a', 'e')
        val otherList = listOf('b', 'c', 'd')
        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 insertAll 方法（在指定位置从 CharArray 添加元素）
    @Test
    fun insertAll_array_at_index_should_insert_and_shift() {
        val list = CharFastList()
        list.insertLast('a', 'e')
        val array = charArrayOf('b', 'c', 'd')
        list.insertAll(1, array)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 insertAll 方法（在指定位置从 CharFastList 添加元素）
    @Test
    fun insertAll_list_at_index_should_insert_and_shift() {
        val list = CharFastList()
        list.insertLast('a', 'e')

        val otherList = CharFastList()
        otherList.insertLast('b', 'c', 'd')

        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 ensure 方法触发的容量扩展
    @Test
    fun ensure_should_expand_capacity() {
        // 从容量 2 开始，然后添加足够多的元素以触发扩展
        val list = CharFastList(2)

        // 添加 10 个元素，这应该会触发多次扩容
        for (i in 0 until 10) {
            list.insertLast((i + 'a'.code).toChar())
        }

        assertEquals(10, list.size)
        // 验证所有元素都被正确存储
        for (i in 0 until 10) {
            assertEquals((i + 'a'.code).toChar(), list[i])
        }
    }

    // 测试空列表的行为
    @Test
    fun empty_list_should_throw_on_get_and_remove() {
        val list = CharFastList()
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
    fun iteration_should_traverse_all_elements_in_order() {
        val list = CharFastList()
        list.insertLast('a', 'b', 'c', 'd', 'e')

        val collected = mutableListOf<Char>()
        for (element in list) {
            collected.add(element)
        }

        assertContentEquals(listOf('a', 'b', 'c', 'd', 'e'), collected)
    }

    // 额外用例：ensureCapacity 与 fill 覆盖容量填充与区间更新
    @Test
    fun ensure_and_fill_should_update_range() {
        val list = CharFastList()
        list.ensureCapacity(5, 'a')
        assertEquals(5, list.size)
        for (i in 0 until 5) assertEquals('a', list[i])
        list.fill('b', 1, 4)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('b', list[2])
        assertEquals('b', list[3])
        assertEquals('a', list[4])
    }

    // 额外用例：safeInsert 与 safeInsertLast 的顺序提交与异常行为
    @Test
    fun safeInsert_and_safeInsertLast_should_behave() {
        val list = CharFastList()
        list.insertLast('x', 'z')
        list.safeInsert(1, 2) {
            unsafeInsert('y')
            unsafeInsert('Y')
        }
        assertEquals(4, list.size)
        assertEquals('x', list[0])
        assertEquals('y', list[1])
        assertEquals('Y', list[2])
        assertEquals('z', list[3])
    
        assertFailsWith<IllegalStateException> {
            list.safeInsertLast(1) { /* 故意不插入以触发计数不匹配 */ }
        }
        list.safeInsertLast(2) {
            unsafeInsert('a')
            unsafeInsert('b')
        }
        assertEquals(6, list.size)
        assertEquals('a', list[4])
        assertEquals('b', list[5])
    }

    // 额外用例：safeInsertLast 计数不匹配时应保留已插入元素（非事务）
    @Test
    fun safeInsertLast_mismatch_should_keep_inserted_elements() {
        val list = CharFastList()
        val offset = list.size
        assertFailsWith<IllegalStateException> {
            list.safeInsertLast(1) {
                unsafeInsert('A')
                unsafeInsert('B')
            }
        }
        assertEquals(offset + 2, list.size)
        assertEquals('A', list[offset])
        assertEquals('B', list[offset + 1])
    }
    
    // 额外用例：add 在头部/中间/尾部插入并正确移动元素
    @Test
    fun add_should_insert_and_shift() {
        val list = CharFastList()
        list.insertLast('a', 'c')
        list.add(1, 'b')
        assertEquals(3, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
    
        list.add(0, '0')
        assertEquals(4, list.size)
        assertEquals('0', list[0])
        assertEquals('a', list[1])
        assertEquals('b', list[2])
        assertEquals('c', list[3])
    
        list.add(4, 'd')
        assertEquals(5, list.size)
        assertEquals('0', list[0])
        assertEquals('a', list[1])
        assertEquals('b', list[2])
        assertEquals('c', list[3])
        assertEquals('d', list[4])
    }

    // 测试 insert 方法的索引越界检查
    @Test
    fun testInsertMultipleElementsAtIndex() {
        val list = CharFastList()
        list.insertLast('a', 'd')
        list.insert(1, 'b', 'c')

        assertEquals(4, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
    }

    // 测试 insertLastAll 方法（从 Iterable 添加元素）
    @Test
    fun testInsertLastAllFromIterable() {
        val list = CharFastList()
        list.insertLast('a', 'b')
        val otherList = listOf('c', 'd', 'e')
        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 insertLastAll 方法（从 CharArray 添加元素）
    @Test
    fun testInsertLastAllFromCharArray() {
        val list = CharFastList()
        list.insertLast('a', 'b')
        val array = charArrayOf('c', 'd', 'e')
        list.insertLastAll(array)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 insertLastAll 方法（从 CharFastList 添加元素）
    @Test
    fun testInsertLastAllFromCharFastList() {
        val list = CharFastList()
        list.insertLast('a', 'b')

        val otherList = CharFastList()
        otherList.insertLast('c', 'd', 'e')

        list.insertLastAll(otherList)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 insertAll 方法（在指定位置从 Iterable 添加元素）
    @Test
    fun testInsertAllAtIndexFromIterable() {
        val list = CharFastList()
        list.insertLast('a', 'e')
        val otherList = listOf('b', 'c', 'd')
        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 insertAll 方法（在指定位置从 CharArray 添加元素）
    @Test
    fun testInsertAllAtIndexFromCharArray() {
        val list = CharFastList()
        list.insertLast('a', 'e')
        val array = charArrayOf('b', 'c', 'd')
        list.insertAll(1, array)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 insertAll 方法（在指定位置从 CharFastList 添加元素）
    @Test
    fun testInsertAllAtIndexFromCharFastList() {
        val list = CharFastList()
        list.insertLast('a', 'e')

        val otherList = CharFastList()
        otherList.insertLast('b', 'c', 'd')

        list.insertAll(1, otherList)

        assertEquals(5, list.size)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('c', list[2])
        assertEquals('d', list[3])
        assertEquals('e', list[4])
    }

    // 测试 ensure 方法触发的容量扩展
    @Test
    fun testEnsureCapacity() {
        // 从容量 2 开始，然后添加足够多的元素以触发扩展
        val list = CharFastList(2)

        // 添加 10 个元素，这应该会触发多次扩容
        for (i in 0 until 10) {
            list.insertLast((i + 'a'.code).toChar())
        }

        assertEquals(10, list.size)
        // 验证所有元素都被正确存储
        for (i in 0 until 10) {
            assertEquals((i + 'a'.code).toChar(), list[i])
        }
    }

    // 测试空列表的行为
    @Test
    fun testEmptyListBehavior() {
        val list = CharFastList()
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
        val list = CharFastList()
        list.insertLast('a', 'b', 'c', 'd', 'e')

        val collected = mutableListOf<Char>()
        for (element in list) {
            collected.add(element)
        }

        assertContentEquals(listOf('a', 'b', 'c', 'd', 'e'), collected)
    }

    // 额外用例：ensureCapacity 与 fill 覆盖容量填充与区间更新
    @Test
    fun ensureCapacity_and_fill_should_work() {
        val list = CharFastList()
        list.ensureCapacity(5, 'a')
        assertEquals(5, list.size)
        for (i in 0 until 5) assertEquals('a', list[i])
        list.fill('b', 1, 4)
        assertEquals('a', list[0])
        assertEquals('b', list[1])
        assertEquals('b', list[2])
        assertEquals('b', list[3])
        assertEquals('a', list[4])
    }

    // 额外用例：safeInsert 与 safeInsertLast 的顺序提交与异常行为
    @Test
    fun safeInsert_and_safeInsertLast_should_work() {
        val list = CharFastList()
        list.insertLast('x', 'z')
        list.safeInsert(1, 2) {
            unsafeInsert('y')
            unsafeInsert('Y')
        }
        assertEquals(4, list.size)
        assertEquals('x', list[0])
        assertEquals('y', list[1])
        assertEquals('Y', list[2])
        assertEquals('z', list[3])
    
        assertFailsWith<IllegalStateException> {
            list.safeInsertLast(1) { /* 故意不插入以触发计数不匹配 */ }
        }
        list.safeInsertLast(2) {
            unsafeInsert('a')
            unsafeInsert('b')
        }
        assertEquals(6, list.size)
        assertEquals('a', list[4])
        assertEquals('b', list[5])
    }

    // 额外用例：insert 多参数重载（4~6 个参数）在指定索引插入
    @Test
    fun insert_multi_parameters_4_to_6() {
        val list = CharFastList()
        list.insertLast('a', 'b', 'c', 'd')
        list.insert(4, 'e', 'f')
        assertEquals(6, list.size)
        val expected = charArrayOf('a', 'b', 'c', 'd', 'e', 'f')
        for (i in expected.indices) assertEquals(expected[i], list[i])
    }
}