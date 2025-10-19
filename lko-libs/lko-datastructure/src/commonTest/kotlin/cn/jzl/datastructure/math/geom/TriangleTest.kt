package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TriangleTest {
    @Test
    fun testProperties() {
        val t = Triangle(Point(0f, 0f), Point(10f, 0f), Point(0f, 10f))
        feq(50f, t.area)
        feq(10f + 10f + sqrt(200f), t.perimeter)
        assertPointEquals(Point(10f / 3f, 10f / 3f), t.center)
    }

    @Test
    fun testContainsDistanceProjectionNormalBounds() {
        val t = Triangle(Point(0f, 0f), Point(10f, 0f), Point(0f, 10f))
        assertTrue(Point(1f, 1f) in t)
        assertTrue(Point(5f, 5f) in t) // on edge x+y=10
        assertFalse(Point(10f, 10f) in t)
        feq(5f, t.distance(Point(5f, -5f)))
        assertPointEquals(Point(5f, 0f), t.projectedPoint(Point(5f, -5f)))
        val n = t.normalVectorAt(Point(5f, -5f))
        assertPointEquals(Point(0f, -1f), n)
        val b = t.getBounds()
        feq(0f, b.x); feq(0f, b.y); feq(10f, b.width); feq(10f, b.height)
    }

    @Test
    fun testInsideProjectionAndNormalFallback() {
        val t = Triangle(Point(0f, 0f), Point(10f, 0f), Point(0f, 10f))
        val inside = Point(2f, 2f)
        assertPointEquals(inside, t.projectedPoint(inside))
        val nInside = t.normalVectorAt(inside)
        assertPointEquals(Point(0f, 1f), nInside)
    }
}
