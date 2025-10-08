package cn.jzl.lko.geom

import cn.jzl.datastructure.FastListAdder
import cn.jzl.datastructure.FloatArrayList
import cn.jzl.lko.geom.vector.Point2
import cn.jzl.lko.geom.vector.Vector2
import cn.jzl.lko.geom.vector.checkDimensions

class PointList(capacity: Int = 7) : IPointList {
    private val points = FloatArrayList(capacity shl 1)
    override val size: Int get() = points.size ushr 1
    override var closed: Boolean = false

    private fun index(index: Int, dimension: Int) = index * 2 + dimension

    override fun get(index: Int, dimension: Int): Float {
        check(index >= 0 && index < size) { "index $index is out of bounds" }
        check(dimension >= 0 && dimension < dimensions) { "dimension $dimension is out of bounds" }
        return points[index(index, dimension)]
    }

    override fun get(index: Int): Point2 = Point2(get(index, 0), get(index, 1))

    override fun add(e: Point2) = points.add(e.x, e.y)

    override fun add(e1: Point2, e2: Point2) = points.add(e1.x, e1.y, e2.x, e2.y)

    override fun add(e1: Point2, e2: Point2, e3: Point2) = points.safeAdd(6) {
        unsafeAdd(e1.x)
        unsafeAdd(e1.y)
        unsafeAdd(e2.x)
        unsafeAdd(e2.y)
        unsafeAdd(e3.x)
        unsafeAdd(e3.y)
    }

    override fun add(e1: Point2, e2: Point2, e3: Point2, e4: Point2) = points.safeAdd(8) {
        unsafeAdd(e1.x)
        unsafeAdd(e1.y)
        unsafeAdd(e2.x)
        unsafeAdd(e2.y)
        unsafeAdd(e3.x)
        unsafeAdd(e3.y)
        unsafeAdd(e4.x)
        unsafeAdd(e4.y)
    }

    override fun add(e1: Point2, e2: Point2, e3: Point2, e4: Point2, e5: Point2) = points.safeAdd(10) {
        unsafeAdd(e1.x)
        unsafeAdd(e1.y)
        unsafeAdd(e2.x)
        unsafeAdd(e2.y)
        unsafeAdd(e3.x)
        unsafeAdd(e3.y)
        unsafeAdd(e4.x)
        unsafeAdd(e4.y)
        unsafeAdd(e5.x)
        unsafeAdd(e5.y)
    }

    private fun FastListAdder<Float>.addPoint(point: Point2) {
        unsafeAdd(point.x)
        unsafeAdd(point.y)
    }

    override fun safeAdd(count: Int, callback: FastListAdder<Point2>.() -> Unit): Unit = points.safeAdd(count shl 1) {
        FastListAdder<Point2> { addPoint(it) }.callback()
    }

    override fun safeSet(index: Int, element: Point2) {
        points.safeSet(index(index, 0), element.x)
        points.safeSet(index(index, 1), element.y)
    }

    override fun set(index: Int, element: Point2) {
        points[index(index, 0)] = element.x
        points[index(index, 1)] = element.y
    }

    override fun plusAssign(elements: Iterable<Point2>) {
        when (elements) {
            is PointList -> points += elements.points
            is Collection<Point2> -> points.safeAdd(elements.size shl 1) {
                elements.forEach { addPoint(it) }
            }

            else -> elements.forEach { points.add(it.x, it.y) }
        }
    }

    override fun clear() {
        points.clear()
    }

    override fun toString(): String {
        return "PointList(size=$size, closed=$closed, points=${joinToString(", ")})"
    }
}