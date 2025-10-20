package cn.jzl.datastructure.math.geom
import kotlin.jvm.JvmInline

import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low

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

@JvmInline
value class SizeInt(private val data: Long) {
    init {
        require(width >= 0 && height >= 0) { "width() and height() must be non-negative" }
    }

    val width: Int get() = data.low
    val height: Int get() = data.high

    companion object {
        operator fun invoke(width: Int, height: Int): SizeInt = SizeInt(Long.fromLowHigh(width, height))
    }
}