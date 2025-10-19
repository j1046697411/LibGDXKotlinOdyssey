package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class PolygonTest {
    @Test
    fun testConstructorValidation() {
        assertFailsWith<IllegalArgumentException> { Polygon(listOf(Point(0f, 0f), Point(1f, 1f))) }
    }

    @Test
    fun testSquarePropertiesAndOps() {
        val pts = listOf(Point(0f, 0f), Point(10f, 0f), Point(10f, 10f), Point(0f, 10f))
        val poly = Polygon(pts)
        assertTrue(poly.closed)
        feq(100f, poly.area)
        feq(40f, poly.perimeter)
        assertPointEquals(Point(5f, 5f), poly.center)

        assertTrue(Point(5f, 5f) in poly)
        assertTrue(Point(10f, 5f) in poly) // edge
        assertFalse(Point(15f, 5f) in poly)

        feq(5f, poly.distance(Point(15f, 5f)))
        assertPointEquals(Point(10f, 5f), poly.projectedPoint(Point(15f, 5f)))
        val n = poly.normalVectorAt(Point(15f, 5f))
        assertPointEquals(Point(1f, 0f), n)

        val b = poly.getBounds()
        feq(0f, b.x); feq(0f, b.y); feq(10f, b.width); feq(10f, b.height)
    }

    @Test
    fun testOnEdgeNormalAndInsideProjection() {
        val pts = listOf(Point(0f, 0f), Point(10f, 0f), Point(10f, 10f), Point(0f, 10f))
        val poly = Polygon(pts)
        val onEdge = Point(10f, 5f)
        assertPointEquals(onEdge, poly.projectedPoint(onEdge))
        val nEdge = poly.normalVectorAt(onEdge)
        assertPointEquals(Point(-1f, 0f), nEdge)
    }
}
