package cn.jzl.lko.geom.vector

import cn.jzl.lko.util.Interpolable
import cn.jzl.lko.util.Ratio
import cn.jzl.lko.util.interpolate

data class IntVector3(override val x: Int, override val y: Int, override val z: Int) : IVector3<Int>, Interpolable<IntVector3> {
    override fun interpolateTo(end: IntVector3, ratio: Ratio): IntVector3 = IntVector3(
        x = ratio.interpolate(x, end.x),
        y = ratio.interpolate(y, end.y),
        z = ratio.interpolate(z, end.z)
    )
}

operator fun IntVector3.plus(other: IntVector3): IntVector3 = IntVector3(x + other.x, y + other.y, z + other.z)
operator fun IntVector3.plus(other: Int): IntVector3 = IntVector3(x + other, y + other, z + other)

operator fun IntVector3.minus(other: IntVector3): IntVector3 = IntVector3(x - other.x, y - other.y, z - other.z)
operator fun IntVector3.minus(other: Int): IntVector3 = IntVector3(x - other, y - other, z - other)

operator fun IntVector3.times(other: IntVector3): IntVector3 = IntVector3(x * other.x, y * other.y, z * other.z)
operator fun IntVector3.times(other: Int): IntVector3 = IntVector3(x * other, y * other, z * other)

operator fun IntVector3.div(other: IntVector3): IntVector3 = IntVector3(x / other.x, y / other.y, z / other.z)
operator fun IntVector3.div(other: Int): IntVector3 = IntVector3(x / other, y / other, z / other)

operator fun IntVector3.rem(other: IntVector3): IntVector3 = IntVector3(x % other.x, y % other.y, z % other.z)
operator fun IntVector3.rem(other: Int): IntVector3 = IntVector3(x % other, y % other, z % other)

operator fun IntVector3.unaryMinus(): IntVector3 = IntVector3(-x, -y, -z)
operator fun IntVector3.unaryPlus(): IntVector3 = IntVector3(x, y, z)
