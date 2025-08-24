package cn.jzl.lko.math

import cn.jzl.lko.fromLowHigh
import cn.jzl.lko.high
import cn.jzl.lko.low

@JvmInline
value class CornerRadius(private val data: Long) {
    val x: Int get() = data.low
    val y: Int get() = data.high

    operator fun component1(): Int = x

    operator fun component2(): Int = y

    companion object {
        val Zero: CornerRadius = CornerRadius(0, 0)

        operator fun invoke(x: Int, y: Int): CornerRadius = CornerRadius(Long.Companion.fromLowHigh(x, y))
    }
}
