package cn.jzl.lko.geom

data class RectCorners(val topLeft: Float, val topRight: Float, val bottomRight: Float, val bottomLeft: Float)
class RoundRectangle(val rectangle: Rectangle, val corners: RectCorners)