package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * 椭圆（轴对齐，基于圆心与半轴）。
 */
data class Ellipse(
    override val center: Point,
    val radiusX: Float,
    val radiusY: Float
) : SimpleShape2D {
    init {
        require(radiusX >= 0f && radiusY >= 0f) { "radiusX() and radiusY() must be non-negative" }
    }

    override val closed: Boolean = true
    override val area: Float = PI.toFloat() * radiusX * radiusY

    // Ramanujan 近似外周长
    override val perimeter: Float
        get() {
            val a = radiusX
            val b = radiusY
            val h = ((a - b) * (a - b)) / ((a + b) * (a + b))
            return (PI.toFloat()) * (a + b) * (1f + 3f * h / (10f + sqrt(4f - 3f * h)))
        }

    private fun scaleFactor(dx: Float, dy: Float): Float {
        val rx2 = radiusX * radiusX
        val ry2 = radiusY * radiusY
        return sqrt(dx * dx / rx2 + dy * dy / ry2)
    }

    override fun distance(point: Point): Float {
        val dx = point.x - center.x
        val dy = point.y - center.y
        val s = scaleFactor(dx, dy)
        return if (s <= 1f) 0f else {
            val len = hypot(dx, dy)
            len * (1f - 1f / s)
        }
    }

    override fun normalVectorAt(point: Point): Point {
        val dx = point.x - center.x
        val dy = point.y - center.y
        val s = scaleFactor(dx, dy)
        val bx: Float
        val by: Float
        if (s <= 1f) {
            // 内部点：使用隐式函数梯度作为外法线方向
            bx = dx
            by = dy
        } else {
            // 外部点：取射线与边界的交点，再用梯度
            bx = dx / s
            by = dy / s
        }
        val nx = bx / (radiusX * radiusX)
        val ny = by / (radiusY * radiusY)
        val nLen = sqrt(nx * nx + ny * ny)
        return if (nLen == 0f) Point(1f, 0f) else Point(nx / nLen, ny / nLen)
    }

    override fun projectedPoint(point: Point): Point {
        val dx = point.x - center.x
        val dy = point.y - center.y
        val s = scaleFactor(dx, dy)
        return if (s <= 1f) point else Point(center.x + dx / s, center.y + dy / s)
    }

    override operator fun contains(point: Point): Boolean {
        val dx = point.x - center.x
        val dy = point.y - center.y
        return scaleFactor(dx, dy) <= 1f
    }

    override fun getBounds(): Rectangle = Rectangle(center.x - radiusX, center.y - radiusY, 2f * radiusX, 2f * radiusY)
}
