package cn.jzl.lko.geom

import cn.jzl.lko.geom.vector.Point2

interface SimpleShape2D {

    val closed: Boolean
    val area: Float
    val perimeter: Float
    val center: Point2

    fun distance(point: Point2): Float

    fun normalVectorAt(point: Point2): Point2

    fun projectedPoint(point: Point2): Point2

    operator fun contains(point: Point2): Boolean

    fun getBounds() : Rectangle
}

