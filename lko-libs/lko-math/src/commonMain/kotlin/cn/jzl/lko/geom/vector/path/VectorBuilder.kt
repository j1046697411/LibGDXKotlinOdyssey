package cn.jzl.lko.geom.vector.path

import cn.jzl.lko.geom.Circle
import cn.jzl.lko.geom.Ellipse
import cn.jzl.lko.geom.IPointList
import cn.jzl.lko.geom.Line
import cn.jzl.lko.geom.Polygon
import cn.jzl.lko.geom.Polyline
import cn.jzl.lko.geom.RectCorners
import cn.jzl.lko.geom.Rectangle
import cn.jzl.lko.geom.RoundRectangle
import cn.jzl.lko.geom.bottomLeft
import cn.jzl.lko.geom.bottomRight
import cn.jzl.lko.geom.height
import cn.jzl.lko.geom.topLeft
import cn.jzl.lko.geom.topRight
import cn.jzl.lko.geom.vector.Point2
import cn.jzl.lko.geom.vector.Vector2
import cn.jzl.lko.geom.width
import kotlin.math.min

const val K = 0.5522848f
const val K2 = 1 - K

interface VectorBuilder {
    val totalPoints: Int
    val lastPos: Point2
    val lastMovePos: Point2

    fun moveTo(point: Point2)
    fun lineTo(point: Point2)
    fun quadTo(control: Point2, end: Point2)
    fun cubicTo(control1: Point2, control2: Point2, end: Point2)
    fun close()
}

fun VectorBuilder.quad(control: Point2, end: Point2) {
    moveTo(control)
    quadTo(control, end)
}

fun VectorBuilder.cubic(control1: Point2, control2: Point2, end: Point2) {
    moveTo(control1)
    cubicTo(control1, control2, end)
}

fun VectorBuilder.polygon(polygon: Polygon): Unit = polygon(polygon.points, true)
fun VectorBuilder.polyline(polyline: Polyline): Unit = polygon(polyline.points, false)

fun VectorBuilder.polygon(points: IPointList, closed: Boolean = false) {
    moveTo(points[0])
    for (i in 1 until points.size) lineTo(points[i])
    if (closed) close()
}

fun VectorBuilder.rectangle(rectangle: Rectangle) {
    moveTo(rectangle.topLeft)
    lineTo(rectangle.topRight)
    lineTo(rectangle.bottomRight)
    lineTo(rectangle.bottomLeft)
    close()
}

fun VectorBuilder.roundRectangle(rectangle: Rectangle, corners: RectCorners) {
    val (left, top, right, bottom) = rectangle
    val (topLeft, topRight, bottomRight, bottomLeft) = corners

    // 计算矩形宽高
    val width = rectangle.width
    val height = rectangle.height

    // 参数验证
    require(topLeft >= 0 && topRight >= 0 && bottomRight >= 0 && bottomLeft >= 0) {
        "All corner radii must be non-negative"
    }
    val minCorner = min(width, height) / 2
    val actualTopLeft = min(topLeft, minCorner)
    val actualTopRight = min(topRight, minCorner)
    val actualBottomRight = min(bottomRight, minCorner)
    val actualBottomLeft = min(bottomLeft, minCorner)

    val controlOffsetTopLeft = actualTopLeft * K2
    val controlOffsetTopRight = actualTopRight * K2
    val controlOffsetBottomRight = actualBottomRight * K2
    val controlOffsetBottomLeft = actualBottomLeft * K2

    moveTo(Vector2(right - actualTopRight, top))
    cubicTo(
        Vector2(right - controlOffsetTopRight, top),
        Vector2(right, top + controlOffsetTopRight),
        Vector2(right, top + actualTopRight)
    )

    lineTo(Vector2(right, bottom - actualBottomRight))
    cubicTo(
        Vector2(right, bottom - controlOffsetBottomRight),
        Vector2(right - controlOffsetBottomRight, bottom),
        Vector2(right - actualBottomRight, bottom)
    )
    lineTo(Vector2(left + actualBottomLeft, bottom))
    cubicTo(
        Vector2(left + controlOffsetBottomLeft, bottom),
        Vector2(left, bottom - controlOffsetBottomLeft),
        Vector2(left, bottom - actualBottomLeft)
    )
    lineTo(Vector2(left, top + actualTopLeft))
    cubicTo(
        Vector2(left, top + controlOffsetTopLeft),
        Vector2(left + controlOffsetTopLeft, top),
        Vector2(left + actualTopLeft, top)
    )
    close()
}

fun VectorBuilder.roundRectangle(roundRectangle: RoundRectangle) = roundRectangle(roundRectangle.rectangle, roundRectangle.corners)

fun VectorBuilder.line(a: Point2, b: Point2) {
    moveTo(a)
    lineTo(b)
}

fun VectorBuilder.line(ax: Float, ay: Float, bx: Float, by: Float) = line(Point2(ax, ay), Point2(bx, by))
fun VectorBuilder.line(line: Line) = line(line.a, line.b)

fun VectorBuilder.ellipse(x: Float, y: Float, rx: Float, ry: Float) {
    moveTo(Vector2(x + rx, y))
    cubicTo(Vector2(x + rx, y - ry * K), Vector2(x + rx * K, y - ry), Vector2(x, y - ry))
    cubicTo(Vector2(x - rx * K, y - ry), Vector2(x - rx, y - ry * K), Vector2(x - rx, y))
    cubicTo(Vector2(x - rx, y + ry * K), Vector2(x - rx * K, y + ry), Vector2(x, y + ry))
    cubicTo(Vector2(x + rx * K, y + ry), Vector2(x + rx, y + ry * K), Vector2(x + rx, y))
    close()
}

fun VectorBuilder.ellipse(ellipse: Ellipse) = ellipse(ellipse.center.x, ellipse.center.y, ellipse.radius.width, ellipse.radius.height)
fun VectorBuilder.circle(circle: Circle): Unit = ellipse(circle.center.x, circle.center.y, circle.radius, circle.radius)
fun VectorBuilder.circle(x: Float, y: Float, radius: Float) = ellipse(x, y, radius, radius)
