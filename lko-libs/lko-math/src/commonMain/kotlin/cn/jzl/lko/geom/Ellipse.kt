package cn.jzl.lko.geom

import cn.jzl.lko.geom.vector.Point2
import kotlin.math.PI

data class Ellipse(override val center: Point2, val radius: Size) : SimpleShape2D {
    override val closed: Boolean = true
    override val area: Float = radius.width * radius.height * PI.toFloat()
    override val perimeter: Float = 2 * radius.width * PI.toFloat()

    override fun distance(point: Point2): Float {
        TODO("Not yet implemented")
    }

    override fun normalVectorAt(point: Point2): Point2 {
        TODO("Not yet implemented")
    }

    override fun projectedPoint(point: Point2): Point2 {
        TODO("Not yet implemented")
    }

    override fun contains(point: Point2): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBounds(): Rectangle = Rectangle(center.x - radius.width, center.y - radius.height, radius.width * 2, radius.height * 2)
}