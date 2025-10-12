package cn.jzl.lko.geom.shape

import cn.jzl.lko.geom.*
import cn.jzl.lko.geom.vector.path.*

interface Shape : SimpleShape2D {
    fun toVectorPath(): VectorPath
}

abstract class AbstractShape : Shape

abstract class BaseShape<T : SimpleShape2D>(private val base: T, genVector: VectorBuilder.(T) -> Unit) : AbstractShape(), SimpleShape2D by base {
    private val vectorPath: VectorPath by lazy { buildVectorPath { genVector(base) } }
    override fun toVectorPath(): VectorPath = vectorPath

    override fun toString(): String = "Shape($base)"
}

abstract class ExtraAbstractShape<T>(private val base: T, genVector: VectorBuilder.(T) -> Unit) : AbstractShape() {
    private val vectorPath: VectorPath by lazy { buildVectorPath { genVector(base) } }
    override fun toVectorPath(): VectorPath = vectorPath
    override fun toString(): String = "EShape($base)"
}

data class LineShape(val line: Line) : BaseShape<Line>(line, { line(it) })
data class CircleShape(val circle: Circle) : BaseShape<Circle>(circle, { circle(it) })
data class RectangleShape(val rectangle: Rectangle) : BaseShape<Rectangle>(rectangle, { rectangle(it) })
data class EllipseShape(val ellipse: Ellipse) : BaseShape<Ellipse>(ellipse, { ellipse(it) })

//data class PolygonShape(val polygon: Polygon) : ExtraAbstractShape<Polygon>(polygon, { polygon(it) })
//data class PolylineShape(val polyline: Polyline) : ExtraAbstractShape<Polyline>(polyline, { polyline(it) })
//data class RoundRectangleShape(val roundRectangle: RoundRectangle) : ExtraAbstractShape<RoundRectangle>(roundRectangle, { roundRectangle(it) })
