package cn.jzl.lko.geom.vector.path

import cn.jzl.lko.geom.vector.Point2

class SvgVisitor(private val out: Appendable) : VectorPath.Visitor {
    override fun moveTo(point: Point2) {
        out.append("M ${point.x} ${point.y} ")
    }

    override fun lineTo(point: Point2) {
        out.append("L ${point.x} ${point.y} ")
    }

    override fun quadTo(control: Point2, end: Point2) {
        out.append("Q ${control.x} ${control.y} ${end.x} ${end.y} ")
    }

    override fun cubicTo(control1: Point2, control2: Point2, end: Point2) {
        out.append("C ${control1.x} ${control1.y} ${control2.x} ${control2.y} ${end.x} ${end.y} ")
    }

    override fun close() {
        out.append("Z ")
    }
}