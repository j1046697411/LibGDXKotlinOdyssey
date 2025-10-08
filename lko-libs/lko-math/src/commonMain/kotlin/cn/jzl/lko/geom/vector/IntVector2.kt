package cn.jzl.lko.geom.vector

data class IntVector2(override val x: Int, override val y: Int) : IVector2<Int>

operator fun IntVector2.plus(other: IntVector2): IntVector2 = IntVector2(x + other.x, y + other.y)
operator fun IntVector2.plus(other: Int): IntVector2 = IntVector2(x + other, y + other)

operator fun IntVector2.minus(other: IntVector2): IntVector2 = IntVector2(x - other.x, y - other.y)
operator fun IntVector2.minus(other: Int): IntVector2 = IntVector2(x - other, y - other)

operator fun IntVector2.times(other: IntVector2): IntVector2 = IntVector2(x * other.x, y * other.y)
operator fun IntVector2.times(other: Int): IntVector2 = IntVector2(x * other, y * other)

operator fun IntVector2.div(other: IntVector2): IntVector2 = IntVector2(x / other.x, y / other.y)
operator fun IntVector2.div(other: Int): IntVector2 = IntVector2(x / other, y / other)

operator fun IntVector2.rem(other: IntVector2): IntVector2 = IntVector2(x % other.x, y % other.y)
operator fun IntVector2.rem(other: Int): IntVector2 = IntVector2(x % other, y % other)

operator fun IntVector2.unaryMinus(): IntVector2 = IntVector2(-x, -y)
operator fun IntVector2.unaryPlus(): IntVector2 = IntVector2(x, y)
