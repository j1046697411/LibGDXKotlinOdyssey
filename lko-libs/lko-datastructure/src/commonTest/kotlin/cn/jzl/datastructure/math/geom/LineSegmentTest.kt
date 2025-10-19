package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LineSegmentTest {
    @Test
    fun testBasicProperties() {
        val s = LineSegment(Point(0f, 0f), Point(10f, 0f))
        assertFalse(s.closed)
        feq(0f, s.area)
        feq(10f, s.perimeter)
        assertPointEquals(Point(5f, 0f), s.center)
    }

    @Test
    fun testDistanceProjectionContainsNormalBounds() {
        val s = LineSegment(Point(0f, 0f), Point(10f, 0f))
        feq(5f, s.distance(Point(5f, 5f)))
        assertPointEquals(Point(10f, 0f), s.projectedPoint(Point(12f, 3f)))
        assertTrue(Point(5f, 0f) in s)
        assertFalse(Point(5f, 1f) in s)
        val n = s.normalVectorAt(Point(5f, 5f))
        assertPointEquals(Point(0f, 1f), n)
        val b = s.getBounds()
        feq(0f, b.x); feq(0f, b.y); feq(10f, b.width); feq(0f, b.height)
    }

    @Test
    fun testOnSegmentProjectionAndNormal() {
        val s = LineSegment(Point(0f, 0f), Point(10f, 0f))
        val on = Point(3f, 0f)
        assertPointEquals(on, s.projectedPoint(on))
        val nOn = s.normalVectorAt(on)
        assertPointEquals(Point(0f, 1f), nOn)
    }
}
