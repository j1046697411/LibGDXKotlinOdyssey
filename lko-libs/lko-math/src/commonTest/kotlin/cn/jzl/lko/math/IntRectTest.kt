package cn.jzl.lko.math

import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IntRectTest {

    @Test
    fun `create rect from position and size`() {
        val position = IntPoint2(10, 20)
        val size = IntSize(30, 40)
        val rect = IntRect(position, size)
        
        assertEquals(10, rect.left)
        assertEquals(20, rect.top)
        assertEquals(40, rect.right)  // 10 + 30
        assertEquals(60, rect.bottom) // 20 + 40
        assertEquals(30, rect.width)
        assertEquals(40, rect.height)
    }

    @Test
    fun `create rect from coordinates`() {
        val rect = IntRect(5, 10, 25, 30)
        
        assertEquals(5, rect.left)
        assertEquals(10, rect.top)
        assertEquals(25, rect.right)
        assertEquals(30, rect.bottom)
        assertEquals(20, rect.width)  // 25 - 5
        assertEquals(20, rect.height) // 30 - 10
    }

    @Test
    fun `test corner points`() {
        val rect = IntRect(0, 0, 100, 200)
        
        assertEquals(IntPoint2(0, 0), rect.topLeft)
        assertEquals(IntPoint2(100, 0), rect.topRight)
        assertEquals(IntPoint2(0, 200), rect.bottomLeft)
        assertEquals(IntPoint2(100, 200), rect.bottomRight)
    }

    @Test
    fun `test center points`() {
        val rect = IntRect(0, 0, 100, 200)
        
        assertEquals(IntPoint2(50, 100), rect.center)
        assertEquals(IntPoint2(50, 0), rect.centerTop)
        assertEquals(IntPoint2(50, 200), rect.centerBottom)
        assertEquals(IntPoint2(0, 100), rect.centerLeft)
        assertEquals(IntPoint2(100, 100), rect.centerRight)
    }

    @Test
    fun `translate rect by vector`() {
        val rect = IntRect(0, 0, 10, 20)
        val translated = rect.translate(IntVector2(5, -3))

        println(translated)

        assertEquals(5, translated.left)
        assertEquals(-3, translated.top)
        assertEquals(15, translated.right)
        assertEquals(17, translated.bottom)
    }

    @Test
    fun `check rectangle overlaps`() {
        val rect1 = IntRect(0, 0, 10, 10)
        val rect2 = IntRect(5, 5, 15, 15)
        val rect3 = IntRect(20, 20, 30, 30)
        
        assertTrue(rect1.overlaps(rect2))
        assertFalse(rect1.overlaps(rect3))
    }

    @Test
    fun `component decomposition`() {
        val rect = IntRect(1, 2, 3, 4)
        val (left, top, right, bottom) = rect
        
        assertEquals(1, left)
        assertEquals(2, top)
        assertEquals(3, right)
        assertEquals(4, bottom)
    }

    @Test
    fun `string representation`() {
        val rect = IntRect(1, 2, 3, 4)
        assertEquals("IntRect(1, 2, 3, 4)", rect.toString())
    }
}