package cn.jzl.lko.geom

import cn.jzl.lko.geom.vector.Point2
import cn.jzl.lko.geom.vector.length
import cn.jzl.lko.geom.vector.minus
import cn.jzl.lko.geom.vector.normalized
import kotlin.math.sqrt

data class Line(val a: Point2, val b: Point2) : SimpleShape2D {
    override val closed: Boolean get() = false
    override val area: Float get() = 0f
    override val perimeter: Float get() = (b - a).length
    override val center: Point2 = Point2((a.x + b.x) / 2, (a.y + b.y) / 2)

    override fun contains(point: Point2): Boolean = false

    override fun getBounds(): Rectangle = TODO("Not yet implemented")

    override fun distance(point: Point2): Float {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val nx = -dy
        val ny = dx
        val d = nx * a.x + ny * a.y
        return (nx * point.x + ny * point.y - d) / sqrt(nx * nx + ny * ny)
    }

    override fun normalVectorAt(point: Point2): Point2 {
        val dx = b.x - a.x
        val dy = b.y - a.y
        return Point2(-dy, dx).normalized
    }

    override fun projectedPoint(point: Point2): Point2 {
        val dx = point.x - a.x
        val dy = point.y - a.y
        val nx = -dy
        val ny = dx
        val d = nx * a.x + ny * a.y
        return Point2((nx * point.x + ny * point.y - d) / (nx * nx + ny * ny) * nx + a.x, (nx * point.x + ny * point.y - d) / (nx * nx + ny * ny) * ny + a.y)
    }
}