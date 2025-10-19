package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.abs

/**
 * 折线（开放形状）。
 *
 * 约定：
 * - 至少包含 2 个顶点；按顺序连接相邻点形成线段集合。
 * - 面积固定为 0；周长为所有线段长度之和。
 */
data class Polyline(val points: List<Point>) : SimpleShape2D {
    init {
        require(points.size >= 2) { "Polyline requires at least 2 points" }
    }

    override val closed: Boolean = false
    private val n: Int get() = points.size

    override val area: Float = 0f

    override val perimeter: Float
        get() {
            var sum = 0f
            for (i in 0 until n - 1) {
                val p = points[i]
                val q = points[i + 1]
                sum += hypot(q.x - p.x, q.y - p.y)
            }
            return sum
        }

    override val center: Point
        get() {
            var ax = 0f; var ay = 0f
            for (p in points) { ax += p.x; ay += p.y }
            return Point(ax / n, ay / n)
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

    override fun distance(point: Point): Float {
        var best = Float.POSITIVE_INFINITY
        for (i in 0 until n - 1) {
            val s = points[i]
            val e = points[i + 1]
            val proj = projectToSegment(point, s, e)
            val d = hypot(point.x - proj.x, point.y - proj.y)
            if (d < best) best = d
        }
        return if (best.isFinite()) best else 0f
    }

    override fun normalVectorAt(point: Point): Point {
        var bestS = points[0]
        var bestE = points[1]
        var bestProj = projectToSegment(point, bestS, bestE)
        var bestDist = hypot(point.x - bestProj.x, point.y - bestProj.y)
        for (i in 1 until n - 1) {
            val s = points[i]
            val e = points[i + 1]
            val proj = projectToSegment(point, s, e)
            val d = hypot(point.x - proj.x, point.y - proj.y)
            if (d < bestDist) {
                bestDist = d
                bestProj = proj
                bestS = s
                bestE = e
            }
        }
        val dx = point.x - bestProj.x
        val dy = point.y - bestProj.y
        val len = sqrt(dx * dx + dy * dy)
        return if (len == 0f) {
            val vx = bestE.x - bestS.x
            val vy = bestE.y - bestS.y
            val nLen = sqrt(vx * vx + vy * vy)
            if (nLen == 0f) Point(1f, 0f) else Point(-vy / nLen, vx / nLen)
        } else Point(dx / len, dy / len)
    }

    override fun projectedPoint(point: Point): Point {
        var bestProj = points[0]
        var bestDist = Float.POSITIVE_INFINITY
        for (i in 0 until n - 1) {
            val s = points[i]
            val e = points[i + 1]
            val proj = projectToSegment(point, s, e)
            val d = hypot(point.x - proj.x, point.y - proj.y)
            if (d < bestDist) {
                bestDist = d
                bestProj = proj
            }
        }
        return bestProj
    }

    override operator fun contains(point: Point): Boolean {
        val eps = 1e-6f
        for (i in 0 until n - 1) {
            val s = points[i]
            val e = points[i + 1]
            val proj = projectToSegment(point, s, e)
            if (abs(point.x - proj.x) <= eps && abs(point.y - proj.y) <= eps) return true
        }
        return false
    }

    override fun getBounds(): Rectangle {
        var minX = points[0].x; var maxX = points[0].x
        var minY = points[0].y; var maxY = points[0].y
        for (i in 1 until n) {
            val p = points[i]
            if (p.x < minX) minX = p.x
            if (p.x > maxX) maxX = p.x
            if (p.y < minY) minY = p.y
            if (p.y > maxY) maxY = p.y
        }
        return Rectangle(minX, minY, maxX - minX, maxY - minY)
    }
}
