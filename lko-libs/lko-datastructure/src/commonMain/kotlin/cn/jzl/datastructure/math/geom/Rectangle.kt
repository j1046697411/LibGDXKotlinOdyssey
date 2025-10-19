package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.hypot

/**
 * 轴对齐矩形（左上角坐标与宽高）。
 */
data class Rectangle(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) : SimpleShape2D {
    init {
        require(width >= 0f && height >= 0f) { "width($width) and height($height) must be non-negative" }
    }

    override val closed: Boolean = true
    override val area: Float get() = width * height
    override val perimeter: Float get() = 2 * (width + height)
    override val center: Point get() = Point(x + width / 2f, y + height / 2f)

    override fun distance(point: Point): Float {
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

    override fun normalVectorAt(point: Point): Point {
        val clampedX = point.x.coerceIn(left, right)
        val clampedY = point.y.coerceIn(top, bottom)
        val onCorner = (clampedX != point.x) && (clampedY != point.y)
        val onVerticalOutside = (clampedX != point.x) && (clampedY == point.y)
        val onHorizontalOutside = (clampedX == point.x) && (clampedY != point.y)
        return when {
            onCorner -> {
                val vx = point.x - clampedX
                val vy = point.y - clampedY
                val len = hypot(vx, vy)
                if (len == 0f) Point(1f, 0f) else Point(vx / len, vy / len)
            }
            onVerticalOutside -> {
                if (point.x < left) Point(-1f, 0f) else Point(1f, 0f)
            }
            onHorizontalOutside -> {
                if (point.y < top) Point(0f, -1f) else Point(0f, 1f)
            }
            else -> {
                // 点在矩形内部或边界上：选择到最近边的外法线
                val dLeft = point.x - left
                val dRight = right - point.x
                val dTop = point.y - top
                val dBottom = bottom - point.y
                val min = minOf(dLeft, dRight, dTop, dBottom)
                when (min) {
                    dLeft -> Point(-1f, 0f)
                    dRight -> Point(1f, 0f)
                    dTop -> Point(0f, -1f)
                    else -> Point(0f, 1f)
                }
            }
        }
    }

    override fun projectedPoint(point: Point): Point = Point(
        point.x.coerceIn(left, right),
        point.y.coerceIn(top, bottom)
    )

    override fun contains(point: Point): Boolean = point.x >= left && point.x <= right && point.y >= top && point.y <= bottom

    override fun getBounds(): Rectangle = this
}

/**
 * 获取矩形的左边界坐标
 */
val Rectangle.left: Float get() = x

/**
 * 获取矩形的上边界坐标
 */
val Rectangle.top: Float get() = y

/**
 * 获取矩形的右边界坐标
 */
val Rectangle.right: Float get() = x + width

/**
 * 获取矩形的下边界坐标
 */
val Rectangle.bottom: Float get() = y + height