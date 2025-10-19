package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class CircleTest {
    @Test
    fun testConstructorValidation() {
        assertFailsWith<IllegalArgumentException> { Circle(Point(0f, 0f), -1f) }
    }

    @Test
    fun testProperties() {
        val c = Circle(Point.ZERO, 5f)
        assertTrue(c.closed)
        feq(PI.toFloat() * 25f, c.area)
        feq(2f * PI.toFloat() * 5f, c.perimeter)
        assertPointEquals(Point(0f, 0f), c.center)
    }

    @Test
    fun testContainsDistanceProjectionNormal() {
        val c = Circle(Point(0f, 0f), 5f)
        assertTrue(Point(4f, 0f) in c)
        assertTrue(Point(5f, 0f) in c)
        assertFalse(Point(6f, 0f) in c)

        feq(0f, c.distance(Point(0f, 0f)))
        feq(0f, c.distance(Point(1f, 1f)))
        feq(5f, c.distance(Point(10f, 0f)))

        assertPointEquals(Point(5f, 0f), c.projectedPoint(Point(10f, 0f)))
        val n = c.normalVectorAt(Point(10f, 0f))
        assertPointEquals(Point(1f, 0f), n)
    }

    @Test
    fun testBounds() {
        val c = Circle(Point(2f, 3f), 4f)
        val b = c.getBounds()
        assertPointEquals(Point(b.x + b.width / 2f, b.y + b.height / 2f), c.center)
        feq(8f, b.width)
        feq(8f, b.height)
        feq(-2f, b.x)
        feq(-1f, b.y)
    }

    @Test
    fun testProjectionInsideAndCenterNormal() {
        val c = Circle(Point(0f, 0f), 5f)
        val inside = Point(1f, 1f)
        assertPointEquals(inside, c.projectedPoint(inside))
        val nCenter = c.normalVectorAt(Point(0f, 0f))
        assertPointEquals(Point(1f, 0f), nCenter)
    }
}
