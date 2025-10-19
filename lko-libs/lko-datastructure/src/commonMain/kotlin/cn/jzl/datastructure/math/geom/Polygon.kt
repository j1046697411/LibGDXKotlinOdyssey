package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * 多边形（闭合形状）。
 *
 * 约定：
 * - 顶点按顺序排列，允许顺/逆时针；不要求凸性。
 * - 面积使用鞋带公式；质心使用标准多边形质心公式（对简单多边形有效）。
 */
data class Polygon(val points: List<Point>) : SimpleShape2D {
    init {
        require(points.size >= 3) { "Polygon requires at least 3 points" }
    }

    override val closed: Boolean = true

    private val n: Int get() = points.size

    override val perimeter: Float
        get() {
            var sum = 0f
            for (i in 0 until n) {
                val p = points[i]
                val q = points[(i + 1) % n]
                sum += hypot(q.x - p.x, q.y - p.y)
            }
            return sum
        }

    override val area: Float
        get() {
            var s = 0f
            for (i in 0 until n) {
                val p = points[i]
                val q = points[(i + 1) % n]
                s += p.x * q.y - p.y * q.x
            }
            return abs(s) * 0.5f
        }

    override val center: Point
        get() {
            // 多边形质心（基于鞋带公式）的推广；对于退化多边形，退回到点平均值
            var s = 0f
            var cx = 0f
            var cy = 0f
            for (i in 0 until n) {
                val p = points[i]
                val q = points[(i + 1) % n]
                val cross = p.x * q.y - p.y * q.x
                s += cross
                cx += (p.x + q.x) * cross
                cy += (p.y + q.y) * cross
            }
            val denom = s * 3f // 6A，其中 A = s/2
            return if (denom == 0f) {
                var ax = 0f; var ay = 0f
                for (p in points) { ax += p.x; ay += p.y }
                Point(ax / n, ay / n)
            } else Point(cx / denom, cy / denom)
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
        if (contains(point)) return 0f
        var best = Float.POSITIVE_INFINITY
        for (i in 0 until n) {
            val s = points[i]
            val e = points[(i + 1) % n]
            val proj = projectToSegment(point, s, e)
            val d = hypot(point.x - proj.x, point.y - proj.y)
            if (d < best) best = d
        }
        return if (best.isFinite()) best else 0f
    }

    override fun normalVectorAt(point: Point): Point {
        var bestSegS = points[0]
        var bestSegE = points[1 % n]
        var bestProj = projectToSegment(point, bestSegS, bestSegE)
        var bestDist = hypot(point.x - bestProj.x, point.y - bestProj.y)
        for (i in 1 until n) {
            val s = points[i]
            val e = points[(i + 1) % n]
            val proj = projectToSegment(point, s, e)
            val d = hypot(point.x - proj.x, point.y - proj.y)
            if (d < bestDist) {
                bestDist = d
                bestProj = proj
                bestSegS = s
                bestSegE = e
            }
        }
        val dx = point.x - bestProj.x
        val dy = point.y - bestProj.y
        val len = sqrt(dx * dx + dy * dy)
        return if (len == 0f) {
            // 垂直于最近边的单位法线（方向与边法线选择一致）
            val vx = bestSegE.x - bestSegS.x
            val vy = bestSegE.y - bestSegS.y
            val nLen = sqrt(vx * vx + vy * vy)
            if (nLen == 0f) Point(1f, 0f) else Point(-vy / nLen, vx / nLen)
        } else Point(dx / len, dy / len)
    }

    override fun projectedPoint(point: Point): Point {
        if (contains(point)) return point
        var bestProj = points[0]
        var bestDist = Float.POSITIVE_INFINITY
        for (i in 0 until n) {
            val s = points[i]
            val e = points[(i + 1) % n]
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
        // 射线法（向右水平射线），统计与边的相交次数
        var count = 0
        for (i in 0 until n) {
            val a = points[i]
            val b = points[(i + 1) % n]
            val minY = min(a.y, b.y)
            val maxY = max(a.y, b.y)
            if (point.y < minY || point.y > maxY) continue
            // 计算 x 方向的交点
            val dy = b.y - a.y
            val dx = b.x - a.x
            val t = if (dy == 0f) Float.POSITIVE_INFINITY else (point.y - a.y) / dy
            if (t < 0f || t > 1f) continue
            val xInt = a.x + dx * t
            if (xInt >= point.x) count++
        }
        return count % 2 == 1
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
