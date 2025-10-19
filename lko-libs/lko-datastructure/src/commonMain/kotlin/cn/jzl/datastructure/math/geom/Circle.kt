package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import cn.jzl.datastructure.math.vector.distanceTo
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sqrt

/**
 * 圆形。
 */
data class Circle(override val center: Point, val radius: Float) : SimpleShape2D {
    init {
        require(radius >= 0f) { "radius() must be non-negative" }
    }

    override val closed: Boolean = true
    override val area: Float = radius * radius * PI.toFloat()
    override val perimeter: Float = 2f * radius * PI.toFloat()

    override fun distance(point: Point): Float = max(0f, center.distanceTo(point) - radius)

    override fun normalVectorAt(point: Point): Point {
        val vx = point.x - center.x
        val vy = point.y - center.y
        val len = sqrt(vx * vx + vy * vy)
        return if (len == 0f) Point(1f, 0f) else Point(vx / len, vy / len)
    }

    override fun projectedPoint(point: Point): Point {
        val n = normalVectorAt(point)
        return if (contains(point)) point else Point(center.x + n.x * radius, center.y + n.y * radius)
    }

    override operator fun contains(point: Point): Boolean = center.distanceTo(point) <= radius

    override fun getBounds(): Rectangle = Rectangle(center.x - radius, center.y - radius, radius * 2f, radius * 2f)
}
