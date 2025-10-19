package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point

interface SimpleShape2D {
    val closed: Boolean
    val area: Float
    val perimeter: Float
    val center: Point

    fun distance(point: Point): Float

    fun normalVectorAt(point: Point): Point

    fun projectedPoint(point: Point): Point

    operator fun contains(point: Point): Boolean

    fun getBounds() : Rectangle
}

