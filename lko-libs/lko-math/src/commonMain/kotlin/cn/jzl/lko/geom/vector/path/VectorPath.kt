package cn.jzl.lko.geom.vector.path

import cn.jzl.datastructure.list.IntFastList
import cn.jzl.datastructure.math.vector.IPointList
import cn.jzl.datastructure.math.vector.PointList
import cn.jzl.lko.geom.vector.Point2
import cn.jzl.lko.geom.vector.Vector2

enum class Winding {
    EVEN_ODD, NON_ZERO
}

fun buildVectorPath(winding: Winding = Winding.NON_ZERO, block: VectorBuilder.() -> Unit): VectorPath = VectorPath(winding = winding).apply(block)

class VectorPath(
    private val commands: IntFastList = IntFastList(),
    private val points: IPointList = PointList(7, false),
    val winding: Winding = Winding.NON_ZERO
) : IVectorPath {

    override val totalPoints: Int get() = points.size shl 1
    override var lastPos: Point2 = Point2.ZERO
        private set
    override var lastMovePos: Point2 = Point2.ZERO
        private set
    var version: Int = 0
        private set

    override fun toSvgString(): String = buildString { accept(SvgVisitor(this)) }

    override fun moveTo(point: Point2) {
        if (commands.isNotEmpty() && commands.last() != COMMAND_MOVE_TO && lastPos == point) return
        commands.add(COMMAND_MOVE_TO)
        points.add(point)
        version++
        lastPos = point
        lastMovePos = point
    }

    private fun ensureMoveTo(point2: Point2) {
        if (commands.isEmpty()) moveTo(point2)
    }

    override fun lineTo(point: Point2) {
        if (lastPos == point) return
        commands.add(COMMAND_LINE_TO)
        points.add(point)
        version++
        lastPos = point
    }

    override fun quadTo(control: Point2, end: Point2) {
        ensureMoveTo(control)
        commands.add(COMMAND_QUAD_TO)
        points.add(control)
        points.add(end)
        version++
        lastPos = end
    }

    override fun cubicTo(control1: Point2, control2: Point2, end: Point2) {
        ensureMoveTo(control1)
        commands.add(COMMAND_CUBIC_TO)
        points.add(control1, control2, end)
        version++
        lastPos = end
    }

    override fun close() {
        commands.add(COMMAND_CLOSE)
    }

    fun accept(visitor: Visitor) {
        var index = 0
        var pointIndex = 0
        while (index < commands.size) {
            val command = commands[index++]
            when (command) {
                COMMAND_MOVE_TO -> {
                    val point = points[pointIndex++]
                    visitor.moveTo(Vector2(point.x, point.y))
                }
                COMMAND_LINE_TO -> {
                    val point = points[pointIndex++]
                    visitor.lineTo(Vector2(point.x, point.y))
                }
                COMMAND_QUAD_TO -> {
                    val p1 = points[pointIndex++]
                    val p2 = points[pointIndex++]
                    visitor.quadTo(Vector2(p1.x, p1.y), Vector2(p2.x, p2.y))
                }
                COMMAND_CUBIC_TO -> {
                    val p1 = points[pointIndex++]
                    val p2 = points[pointIndex++]
                    val p3 = points[pointIndex++]
                    visitor.cubicTo(Vector2(p1.x, p1.y), Vector2(p2.x, p2.y), Vector2(p3.x, p3.y))
                }
                else -> visitor.close()
            }
        }
    }

    fun clear() {
        commands.clear()
        points.clear()
        lastPos = Point2.ZERO
        lastMovePos = Point2.ZERO
        version = 0
    }

    interface Visitor {
        fun moveTo(point: Point2)
        fun lineTo(point: Point2)
        fun quadTo(control: Point2, end: Point2)
        fun cubicTo(control1: Point2, control2: Point2, end: Point2)
        fun close()
    }

    companion object {
        const val COMMAND_MOVE_TO = 1
        const val COMMAND_LINE_TO = 2
        const val COMMAND_QUAD_TO = 3
        const val COMMAND_CUBIC_TO = 4
        const val COMMAND_CLOSE = 5
    }
}