package cn.jzl.datastructure.array2

import cn.jzl.datastructure.math.geom.RectangleInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Array2Test {

    @Test
    fun testGetSet() {
        val w = 3
        val h = 2
        val arr = Array2(width = w, height = h, data = Array(w * h) { 0 })

        // set some values
        arr[0, 0] = 1
        arr[1, 0] = 2
        arr[2, 1] = 3

        assertEquals(1, arr[0, 0])
        assertEquals(2, arr[1, 0])
        assertEquals(3, arr[2, 1])
    }

    @Test
    fun testSetRectFill() {
        val w = 4
        val h = 3
        val arr = Array2(width = w, height = h, data = Array(w * h) { 0 })

        // fill rectangle at x=1,y=1 width=2 height=1 with value 9
        arr.set(RectangleInt(1, 1, 2, 1), 9)

        // row-major indices
        // y=1: positions (1,1), (2,1)
        assertEquals(9, arr[1, 1])
        assertEquals(9, arr[2, 1])

        // unchanged others
        assertEquals(0, arr[0, 0])
        assertEquals(0, arr[3, 2])
    }

    @Test
    fun testContainsAndIterator() {
        val w = 2
        val h = 2
        val arr = Array2(width = w, height = h, data = arrayOf(1, 2, 3, 4))

        assertTrue(3 in arr)
        val collected = mutableListOf<Int>()
        for (v in arr) collected.add(v)
        assertEquals(listOf(1, 2, 3, 4), collected)
    }

    @Test
    fun testEqualsAndHashCode() {
        val w = 3
        val h = 2
        val a1 = Array2(width = w, height = h, data = arrayOf(1, 2, 3, 4, 5, 6))
        val a2 = Array2(width = w, height = h, data = arrayOf(1, 2, 3, 4, 5, 6))
        val a3 = Array2(width = w, height = h, data = arrayOf(1, 2, 3, 4, 5, 7))

        assertTrue(a1 == a2)
        assertTrue(a1.hashCode() == a2.hashCode())
        assertTrue(a1 != a3)
    }
}