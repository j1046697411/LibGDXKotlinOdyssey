package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.hypot
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class RectangleTest {
    @Test
    fun testConstructorValidation() {
        assertFailsWith<IllegalArgumentException> { Rectangle(0f, 0f, -1f, 1f) }
        assertFailsWith<IllegalArgumentException> { Rectangle(0f, 0f, 1f, -1f) }
    }

    @Test
    fun testContainsDistanceProjectionNormal() {
        val r = Rectangle(0f, 0f, 10f, 10f)
        assertTrue(Point(5f, 5f) in r)
        assertTrue(Point(0f, 0f) in r)
        assertTrue(Point(10f, 10f) in r)
        assertFalse(Point(-1f, 5f) in r)
        feq(0f, r.distance(Point(3f, 3f)))
        feq(5f, r.distance(Point(15f, 5f)))
        feq(hypot(5f, 5f), r.distance(Point(-5f, -5f)))
        assertPointEquals(Point(0f, 5f), r.projectedPoint(Point(-5f, 5f)))
        val n1 = r.normalVectorAt(Point(-5f, 5f))
        assertPointEquals(Point(-1f, 0f), n1)
        val n2 = r.normalVectorAt(Point(5f, -5f))
        assertPointEquals(Point(0f, -1f), n2)
        val nInside = r.normalVectorAt(Point(1f, 5f))
        assertPointEquals(Point(-1f, 0f), nInside)
    }

    @Test
    fun testBoundsSelf() {
        val r = Rectangle(1f, 2f, 3f, 4f)
        val b = r.getBounds()
        feq(1f, b.x); feq(2f, b.y); feq(3f, b.width); feq(4f, b.height)
        assertPointEquals(Point(2.5f, 4f), r.center)
    }

    @Test
    fun testCornerNormalAndInsideProjection() {
        val r = Rectangle(0f, 0f, 10f, 10f)
        val inside = Point(1f, 1f)
        assertPointEquals(inside, r.projectedPoint(inside))
        val nCorner = r.normalVectorAt(Point(-5f, -5f))
        // 归一化(-5, -5)
        assertPointEquals(Point(-0.70710677f, -0.70710677f), nCorner)
    }
}
