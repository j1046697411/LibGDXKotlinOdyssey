package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class EllipseTest {
    @Test
    fun testConstructorValidation() {
        assertFailsWith<IllegalArgumentException> { Ellipse(Point(0f, 0f), -1f, 1f) }
        assertFailsWith<IllegalArgumentException> { Ellipse(Point(0f, 0f), 1f, -1f) }
    }

    @Test
    fun testProperties() {
        val e = Ellipse(Point(0f, 0f), 4f, 2f)
        assertTrue(e.closed)
        feq(PI.toFloat() * 8f, e.area)
        assertPointEquals(Point(0f, 0f), e.center)
    }

    @Test
    fun testContainsDistanceProjectionNormal() {
        val e = Ellipse(Point(0f, 0f), 4f, 2f)
        assertTrue(Point(2f, 0f) in e)
        assertTrue(Point(4f, 0f) in e)
        assertFalse(Point(5f, 0f) in e)
        feq(0f, e.distance(Point(0f, 0f)))
        feq(1f, e.distance(Point(5f, 0f)))
        assertPointEquals(Point(4f, 0f), e.projectedPoint(Point(5f, 0f)))
        val n = e.normalVectorAt(Point(0f, 3f))
        assertPointEquals(Point(0f, 1f), n)
    }

    @Test
    fun testBounds() {
        val e = Ellipse(Point(2f, 3f), 4f, 2f)
        val b = e.getBounds()
        feq(-2f, b.x); feq(1f, b.y); feq(8f, b.width); feq(4f, b.height)
    }

    @Test
    fun testProjectionInsideAndAxisNormal() {
        val e = Ellipse(Point(0f, 0f), 4f, 2f)
        val inside = Point(1f, 1f)
        assertPointEquals(inside, e.projectedPoint(inside))
        val nAxis = e.normalVectorAt(Point(0f, 1f))
        assertPointEquals(Point(0f, 1f), nAxis)
        val nCenter = e.normalVectorAt(Point(0f, 0f))
        assertPointEquals(Point(1f, 0f), nCenter)
    }
}
