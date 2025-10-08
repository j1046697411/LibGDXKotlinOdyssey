package cn.jzl.lko.geom

import cn.jzl.lko.geom.vector.*
import kotlin.math.PI
import kotlin.math.max

data class Circle(override val center: Point2, val radius: Float) : SimpleShape2D {
    override val closed: Boolean = true
    override val area: Float = radius * radius * PI.toFloat()
    override val perimeter: Float = 2 * radius * PI.toFloat()

    override fun distance(point: Point2): Float = max(0f, center.distance(point) - radius)

    override fun normalVectorAt(point: Point2): Point2 = (point - center).normalized

    override fun projectedPoint(point: Point2): Point2 = if (point in this) point else center + normalVectorAt(point) * radius

    override fun contains(point: Point2): Boolean = center.distance(point) <= radius

    override fun getBounds(): Rectangle = Rectangle(center.x - radius, center.y - radius, radius * 2, radius * 2)
}
