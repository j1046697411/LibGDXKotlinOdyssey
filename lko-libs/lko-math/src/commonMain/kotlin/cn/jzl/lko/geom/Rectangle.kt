package cn.jzl.lko.geom

import cn.jzl.lko.geom.vector.Point2
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class Rectangle(val left: Float, val top: Float, val right: Float, val bottom: Float) : SimpleShape2D, Sizeable {
    init {
        require(left <= right) { "Left must be less than or equal to right" }
        require(top <= bottom) { "Top must be less than or equal to bottom" }
    }

    override val closed: Boolean get() = true
    override val area: Float get() = width * height
    override val perimeter: Float get() = 2 * (width + height)
    override val center: Point2 get() = Point2((left + right) / 2, (top + bottom) / 2)

    override val size: Size get() = Size(width, height)

    override fun distance(point: Point2): Float {
        val dx = when {
            point.x < left -> left - point.x
            point.x > right -> point.x - right
            else -> 0f
        }
        val dy = when {
            point.y < top -> top - point.y
            point.y > bottom -> point.y - bottom
            else -> 0f
        }
        return hypot(dx, dy)
    }

    override fun normalVectorAt(point: Point2): Point2 {
        val dx = point.x - center.x
        val dy = point.y - center.y
        return Point2(-dy / sqrt(dx * dx + dy * dy), dx / sqrt(dx * dx + dy * dy))
    }

    override fun projectedPoint(point: Point2): Point2 {
        return Point2(point.x.coerceIn(left, right), point.y.coerceIn(top, bottom))
    }

    override operator fun contains(point: Point2): Boolean {
        return point.x.inRange(left, right) && point.y.inRange(top, bottom)
    }

    override fun getBounds(): Rectangle = this
}

inline val Rectangle.x: Float get() = left
inline val Rectangle.y: Float get() = top
inline val Rectangle.width: Float get() = right - left
inline val Rectangle.height: Float get() = bottom - top

inline val Rectangle.topLeft: Point2 get() = Point2(left, top)
inline val Rectangle.topRight: Point2 get() = Point2(right, top)
inline val Rectangle.bottomRight: Point2 get() = Point2(right, bottom)
inline val Rectangle.bottomLeft: Point2 get() = Point2(left, bottom)

infix fun Rectangle.intersects(other: Rectangle): Boolean = left < other.right && right > other.left && top < other.bottom && bottom > other.top
operator fun Rectangle.contains(other: Rectangle): Boolean = left <= other.left && right >= other.right && top <= other.top && bottom >= other.bottom
infix fun Rectangle.union(other: Rectangle): Rectangle = Rectangle(
    left = min(left, other.left),
    top = min(top, other.top),
    right = max(right, other.right),
    bottom = max(bottom, other.bottom)
)
