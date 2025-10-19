package cn.jzl.datastructure.math.geom

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RectangleIntTest {
    @Test
    fun testConstructorValidation() {
        assertFailsWith<IllegalArgumentException> { RectangleInt(0, 0, -1, 1) }
        assertFailsWith<IllegalArgumentException> { RectangleInt(0, 0, 1, -1) }
    }

    @Test
    fun testBasicProperties() {
        val r = RectangleInt(1, 2, 3, 4)
        assertEquals(1, r.x); assertEquals(2, r.y); assertEquals(3, r.width); assertEquals(4, r.height)
    }
}
