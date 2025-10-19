package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class PolylineTest {
    @Test
    fun testConstructorValidation() {
        assertFailsWith<IllegalArgumentException> { Polyline(listOf(Point(0f, 0f))) }
    }

    @Test
    fun testBasicPropertiesAndOps() {
        val pts = listOf(Point(0f, 0f), Point(10f, 0f), Point(10f, 10f))
        val pl = Polyline(pts)
        assertFalse(pl.closed)
        feq(0f, pl.area)
        feq(20f, pl.perimeter)
        assertPointEquals(Point(20f / 3f, 10f / 3f), pl.center)

        assertTrue(Point(5f, 0f) in pl)
        assertTrue(Point(10f, 0f) in pl)
        assertFalse(Point(9f, 1f) in pl)
        feq(1f, pl.distance(Point(9f, 1f)))
        assertPointEquals(Point(9f, 0f), pl.projectedPoint(Point(9f, 1f)))

        val b = pl.getBounds()
        feq(0f, b.x); feq(0f, b.y); feq(10f, b.width); feq(10f, b.height)
    }

    @Test
    fun testOnSegmentProjectionAndNormal() {
        val pts = listOf(Point(0f, 0f), Point(10f, 0f), Point(10f, 10f))
        val pl = Polyline(pts)
        val on = Point(5f, 0f)
        assertPointEquals(on, pl.projectedPoint(on))
        val nOn = pl.normalVectorAt(on)
        assertPointEquals(Point(0f, 1f), nOn)
    }
}
