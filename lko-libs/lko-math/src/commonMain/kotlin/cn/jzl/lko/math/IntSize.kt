package cn.jzl.lko.math

import cn.jzl.lko.fromLowHigh
import cn.jzl.lko.high
import cn.jzl.lko.low

@JvmInline
value class IntSize private constructor(private val data: Long) {

    val width: Int get() = data.low
    val height: Int get() = data.high

    operator fun component1(): Int = width

    operator fun component2(): Int = height

    operator fun plus(other: IntSize): IntSize {
        return IntSize(width + other.width, height + other.height)
    }

    operator fun minus(other: IntSize): IntSize {
        return IntSize(width - other.width, height - other.height)
    }

    operator fun times(other: IntSize): IntSize {
        return IntSize(width * other.width, height * other.height)
    }

    operator fun div(other: IntSize): IntSize {
        return IntSize(width / other.width, height / other.height)
    }

    operator fun plus(other: Int): IntSize {
        return IntSize(width + other, height + other)
    }

    operator fun minus(other: Int): IntSize {
        return IntSize(width - other, height - other)
    }

    operator fun times(other: Int): IntSize {
        return IntSize(width * other, height * other)
    }

    operator fun div(other: Int): IntSize {
        return IntSize(width / other, height / other)
    }

    override fun toString(): String {
        return "IntSize(width=$width, height=$height)"
    }

    companion object {
        operator fun invoke(width: Int, height: Int): IntSize = IntSize(Long.Companion.fromLowHigh(width, height))

        val Zero: IntSize = IntSize(0, 0)
    }
}

