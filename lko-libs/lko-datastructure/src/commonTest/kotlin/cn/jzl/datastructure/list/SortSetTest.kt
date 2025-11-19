package cn.jzl.datastructure.list

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SortSetTest {

    @Test
    fun `create empty SortSet`() {
        val set = SortSet<Int>(compareBy { it })
        assertTrue(set.isEmpty())
        assertEquals(0, set.size)
    }

    @Test
    fun `add single element`() {
        val set = SortSet<Int>(compareBy { it })
        assertTrue(set.add(5))
        assertEquals(1, set.size)
        assertTrue(set.contains(5))
    }

    @Test
    fun `add duplicate element`() {
        val set = SortSet<Int>(compareBy { it })
        set.add(5)
        assertTrue(set.add(5)) // should return true, but will replace element
        assertEquals(1, set.size)
        assertTrue(set.contains(5))
    }

    @Test
    fun `add multiple elements maintaining order`() {
        val set = SortSet<Int>(compareBy { it })
        set.add(5)
        set.add(2)
        set.add(8)
        set.add(1)
        
        assertEquals(4, set.size)
        assertEquals(listOf(1, 2, 5, 8), set.toList())
    }

    @Test
    fun `add multiple elements maintaining order descending`() {
        val set = SortSet<Int>(compareByDescending { it })
        set.add(5)
        set.add(2)
        set.add(8)
        set.add(1)
        
        assertEquals(4, set.size)
        assertEquals(listOf(8, 5, 2, 1), set.toList())
    }

    @Test
    fun `remove existing element`() {
        val set = SortSet<Int>(compareBy { it })
        set.add(5)
        set.add(2)
        
        assertTrue(set.remove(2))
        assertEquals(1, set.size)
        assertFalse(set.contains(2))
        assertTrue(set.contains(5))
    }

    @Test
    fun `remove non-existing element`() {
        val set = SortSet<Int>(compareBy { it })
        set.add(5)
        
        assertFalse(set.remove(10))
        assertEquals(1, set.size)
        assertTrue(set.contains(5))
    }

    @Test
    fun `clear set`() {
        val set = SortSet<Int>(compareBy { it })
        set.add(5)
        set.add(2)
        
        set.clear()
        assertTrue(set.isEmpty())
        assertEquals(0, set.size)
    }

    @Test
    fun `add collection`() {
        val set = SortSet<Int>(compareBy { it })
        val elements = listOf(5, 2, 8, 1)
        
        assertTrue(set.addAll(elements))
        assertEquals(4, set.size)
        assertEquals(listOf(1, 2, 5, 8), set.toList())
    }

    @Test
    fun `remove collection`() {
        val set = SortSet<Int>(compareBy { it })
        set.addAll(listOf(1, 2, 3, 4, 5))
        
        assertTrue(set.removeAll(listOf(2, 4)))
        assertEquals(3, set.size)
        assertEquals(listOf(1, 3, 5), set.toList())
    }

    @Test
    fun `retain collection`() {
        val set = SortSet<Int>(compareBy { it })
        set.addAll(listOf(1, 2, 3, 4, 5))
        
        assertTrue(set.retainAll(listOf(2, 3, 4)))
        assertEquals(3, set.size)
        assertEquals(listOf(2, 3, 4), set.toList())
    }

    @Test
    fun `retain empty collection`() {
        val set = SortSet<Int>(compareBy { it })
        set.addAll(listOf(1, 2, 3))
        
        assertTrue(set.retainAll(emptyList()))
        assertTrue(set.isEmpty())
    }

    @Test
    fun `check contains all elements`() {
        val set = SortSet<Int>(compareBy { it })
        set.addAll(listOf(1, 2, 3, 4, 5))
        
        assertTrue(set.containsAll(listOf(2, 3, 4)))
        assertFalse(set.containsAll(listOf(2, 3, 6)))
    }

    @Test
    fun `iterator test`() {
        val set = SortSet<Int>(compareBy { it })
        set.addAll(listOf(3, 1, 2))
        
        val iterator = set.iterator()
        val result = mutableListOf<Int>()
        while (iterator.hasNext()) {
            result.add(iterator.next())
        }
        
        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun `iterator remove test`() {
        val set = SortSet<Int>(compareBy { it })
        set.addAll(listOf(1, 2, 3, 4, 5))
        
        val iterator = set.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (element % 2 == 0) {
                iterator.remove()
            }
        }
        
        assertEquals(listOf(1, 3, 5), set.toList())
    }

    @Test
    fun `string representation`() {
        val set = SortSet<Int>(compareBy { it })
        set.addAll(listOf(3, 1, 2))
        
        val str = set.toString()
        assertTrue(str.contains("1") && str.contains("2") && str.contains("3"))
    }

    @Test
    fun `edge case - insert at beginning`() {
        val set = SortSet<Int>(compareBy { it })
        set.add(5)
        set.add(3)
        set.add(1) // should insert at beginning
        
        assertEquals(listOf(1, 3, 5), set.toList())
    }

    @Test
    fun `edge case - insert at end`() {
        val set = SortSet<Int>(compareBy { it })
        set.add(1)
        set.add(3)
        set.add(5) // should insert at end
        
        assertEquals(listOf(1, 3, 5), set.toList())
    }

    @Test
    fun `edge case - insert in middle`() {
        val set = SortSet<Int>(compareBy { it })
        set.add(1)
        set.add(5)
        set.add(3) // should insert in middle
        
        assertEquals(listOf(1, 3, 5), set.toList())
    }

    @Test
    fun `special type - IntSortSet`() {
        val set = SortSet.int(compareBy { it })
        set.addAll(listOf(5, 2, 8, 1))
        
        assertEquals(4, set.size)
        assertEquals(listOf(1, 2, 5, 8), set.toList())
    }

    @Test
    fun `special type - LongSortSet`() {
        val set = SortSet.long(compareBy { it })
        set.addAll(listOf(5L, 2L, 8L, 1L))
        
        assertEquals(4, set.size)
        assertEquals(listOf(1L, 2L, 5L, 8L), set.toList())
    }

    @Test
    fun `special type - DoubleSortSet`() {
        val set = SortSet.double(compareBy { it })
        set.addAll(listOf(5.0, 2.0, 8.0, 1.0))
        
        assertEquals(4, set.size)
        assertEquals(listOf(1.0, 2.0, 5.0, 8.0), set.toList())
    }

    @Test
    fun `custom object sorting test`() {
        data class Person(val name: String, val age: Int)
        
        val set = SortSet<Person>(compareBy { it.age })
        set.add(Person("Alice", 25))
        set.add(Person("Bob", 30))
        set.add(Person("Charlie", 20))
        
        assertEquals(3, set.size)
        assertEquals(listOf("Charlie", "Alice", "Bob"), set.map { it.name })
    }

    @Test
    fun `performance test - insert large number of elements`() {
        val set = SortSet<Int>(compareBy { it })
        val elements = (1000 downTo 1).toList() // insert in reverse order
        
        set.addAll(elements)
        
        assertEquals(1000, set.size)
        // verify order is correct
        for (i in 1..1000) {
            assertTrue(set.contains(i))
        }
        assertEquals((1..1000).toList(), set.toList())
    }

    @Test
    fun `empty set operations`() {
        val set = SortSet<Int>(compareBy { it })
        
        assertFalse(set.remove(5))
        assertFalse(set.contains(5))
        assertTrue(set.containsAll(emptyList()))
        assertFalse(set.addAll(emptyList()))
        assertFalse(set.removeAll(emptyList()))
    }

    private fun <T> SortSet<T>.toList(): List<T> {
        return this.toMutableList()
    }
}