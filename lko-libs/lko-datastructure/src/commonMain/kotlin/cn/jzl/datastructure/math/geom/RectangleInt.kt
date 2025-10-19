package cn.jzl.datastructure.math.geom

/**
 * 整型轴对齐矩形。
 */
data class RectangleInt(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {
    init {
        require(width >= 0 && height >= 0) { "width() and height() must be non-negative" }
    }
}
