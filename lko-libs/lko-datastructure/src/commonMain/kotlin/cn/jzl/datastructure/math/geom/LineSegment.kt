package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.abs

/**
 * 线段（开放形状）。
 */
data class LineSegment(
    val start: Point,
    val end: Point
) : SimpleShape2D {
    override val closed: Boolean = false
    override val area: Float = 0f
    override val perimeter: Float get() = hypot(end.x - start.x, end.y - start.y)
    override val center: Point get() = Point((start.x + end.x) / 2f, (start.y + end.y) / 2f)

    private fun project(point: Point): Point {
        val vx = end.x - start.x
        val vy = end.y - start.y
        val wx = point.x - start.x
        val wy = point.y - start.y
        val denom = vx * vx + vy * vy
        val t = if (denom == 0f) 0f else (wx * vx + wy * vy) / denom
        val tc = t.coerceIn(0f, 1f)
        return Point(start.x + vx * tc, start.y + vy * tc)
    }

    override fun distance(point: Point): Float {
        val p = project(point)
        return hypot(point.x - p.x, point.y - p.y)
    }

    override fun normalVectorAt(point: Point): Point {
        val p = project(point)
        val dx = point.x - p.x
        val dy = point.y - p.y
        val len = sqrt(dx * dx + dy * dy)
        return if (len == 0f) {
            // 垂直于线段方向
            val vx = end.x - start.x
            val vy = end.y - start.y
            val nLen = sqrt(vx * vx + vy * vy)
            if (nLen == 0f) Point(1f, 0f) else Point(-vy / nLen, vx / nLen)
        } else Point(dx / len, dy / len)
    }

    override fun projectedPoint(point: Point): Point = project(point)

    override operator fun contains(point: Point): Boolean {
        val p = project(point)
        val eps = 1e-6f
        return abs(point.x - p.x) <= eps && abs(point.y - p.y) <= eps
    }

    override fun getBounds(): Rectangle {
        val left = min(start.x, end.x)
        val right = max(start.x, end.x)
        val top = min(start.y, end.y)
        val bottom = max(start.y, end.y)
        return Rectangle(left, top, right - left, bottom - top)
    }
}
