package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * 三角形（闭合形状）。
 */
data class Triangle(
    val a: Point,
    val b: Point,
    val c: Point
) : SimpleShape2D {
    override val closed: Boolean = true
    override val center: Point get() = Point((a.x + b.x + c.x) / 3f, (a.y + b.y + c.y) / 3f)

    private fun edgeLength(p: Point, q: Point): Float = hypot(q.x - p.x, q.y - p.y)

    override val perimeter: Float get() = edgeLength(a, b) + edgeLength(b, c) + edgeLength(c, a)

    override val area: Float
        get() {
            val abx = b.x - a.x
            val aby = b.y - a.y
            val acx = c.x - a.x
            val acy = c.y - a.y
            return 0.5f * abs(abx * acy - aby * acx)
        }

    private fun projectToSegment(p: Point, s: Point, e: Point): Point {
        val vx = e.x - s.x
        val vy = e.y - s.y
        val wx = p.x - s.x
        val wy = p.y - s.y
        val denom = vx * vx + vy * vy
        val t = if (denom == 0f) 0f else (wx * vx + wy * vy) / denom
        val tc = t.coerceIn(0f, 1f)
        return Point(s.x + vx * tc, s.y + vy * tc)
    }

    private fun distanceToSegment(p: Point, s: Point, e: Point): Float {
        val proj = projectToSegment(p, s, e)
        return hypot(p.x - proj.x, p.y - proj.y)
    }

    private fun sameSide(p1: Point, p2: Point, a: Point, b: Point): Boolean {
        val abx = b.x - a.x
        val aby = b.y - a.y
        val ap1x = p1.x - a.x
        val ap1y = p1.y - a.y
        val ap2x = p2.x - a.x
        val ap2y = p2.y - a.y
        val c1 = abx * ap1y - aby * ap1x
        val c2 = abx * ap2y - aby * ap2x
        return c1 == 0f && c2 == 0f || c1 >= 0f && c2 >= 0f || c1 <= 0f && c2 <= 0f
    }

    override fun distance(point: Point): Float {
        if (contains(point)) return 0f
        return min(min(distanceToSegment(point, a, b), distanceToSegment(point, b, c)), distanceToSegment(point, c, a))
    }

    override fun normalVectorAt(point: Point): Point {
        // 选择最近边的外法线（指向点）
        val pa = projectToSegment(point, a, b)
        val pb = projectToSegment(point, b, c)
        val pc = projectToSegment(point, c, a)
        val da = hypot(point.x - pa.x, point.y - pa.y)
        val db = hypot(point.x - pb.x, point.y - pb.y)
        val dc = hypot(point.x - pc.x, point.y - pc.y)
        val (px, py) = when {
            da <= db && da <= dc -> pa
            db <= da && db <= dc -> pb
            else -> pc
        }
        val dx = point.x - px
        val dy = point.y - py
        val len = sqrt(dx * dx + dy * dy)
        return if (len == 0f) Point(1f, 0f) else Point(dx / len, dy / len)
    }

    override fun projectedPoint(point: Point): Point {
        if (contains(point)) return point
        val pa = projectToSegment(point, a, b)
        val pb = projectToSegment(point, b, c)
        val pc = projectToSegment(point, c, a)
        val da = hypot(point.x - pa.x, point.y - pa.y)
        val db = hypot(point.x - pb.x, point.y - pb.y)
        val dc = hypot(point.x - pc.x, point.y - pc.y)
        return when {
            da <= db && da <= dc -> pa
            db <= da && db <= dc -> pb
            else -> pc
        }
    }

    override operator fun contains(point: Point): Boolean {
        // 通过同侧性判断是否在三角形内部（含边）
        return sameSide(point, c, a, b) && sameSide(point, a, b, c) && sameSide(point, b, c, a)
    }

    override fun getBounds(): Rectangle {
        val minX = min(a.x, min(b.x, c.x))
        val maxX = max(a.x, max(b.x, c.x))
        val minY = min(a.y, min(b.y, c.y))
        val maxY = max(a.y, max(b.y, c.y))
        return Rectangle(minX, minY, maxX - minX, maxY - minY)
    }
}
