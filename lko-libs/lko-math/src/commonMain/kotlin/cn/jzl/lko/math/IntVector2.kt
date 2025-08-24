package cn.jzl.lko.math

import cn.jzl.lko.*

typealias IntPoint2 = IntVector2

@JvmInline
value class IntVector2(private val data: Long) {

    val x: Int get() = data.low
    val y: Int get() = data.high

    companion object {

        operator fun invoke(x: Int, y: Int): IntVector2 = IntVector2(Long.Companion.fromLowHigh(x, y))

        val Zero: IntVector2 = IntVector2(0, 0)
        val X: IntVector2 = IntVector2(1, 0)
        val Y: IntVector2 = IntVector2(0, 1)
        val One: IntVector2 = IntVector2(1, 1)
    }

    operator fun component1(): Int = x

    operator fun component2(): Int = y

    operator fun plus(other: IntVector2): IntVector2 {
        return IntVector2(x + other.x, y + other.y)
    }

    operator fun minus(other: IntVector2): IntVector2 {
        return IntVector2(x - other.x, y - other.y)
    }

    operator fun times(other: IntVector2): IntVector2 {
        return IntVector2(x * other.x, y * other.y)
    }

    operator fun div(other: IntVector2): IntVector2 {
        return IntVector2(x / other.x, y / other.y)
    }

    operator fun plus(other: Int): IntVector2 {
        return IntVector2(x + other, y + other)
    }

    operator fun minus(other: Int): IntVector2 {
        return IntVector2(x - other, y - other)
    }

    operator fun times(other: Int): IntVector2 {
        return IntVector2(x * other, y * other)
    }

    operator fun div(other: Int): IntVector2 {
        return IntVector2(x / other, y / other)
    }

    operator fun unaryMinus(): IntVector2 {
        return IntVector2(-x, -y)
    }
}

