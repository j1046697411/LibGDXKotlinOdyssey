package cn.jzl.lko.geom.vector

import cn.jzl.lko.util.Interpolable
import cn.jzl.lko.util.Ratio
import cn.jzl.lko.util.interpolate

data class IntVector4(override val x: Int, override val y: Int, override val z: Int, override val w: Int) : IVector4<Int>, Interpolable<IntVector4> {
    override fun interpolateTo(end: IntVector4, ratio: Ratio): IntVector4 = IntVector4(
        x = ratio.interpolate(x, end.x),
        y = ratio.interpolate(y, end.y),
        z = ratio.interpolate(z, end.z),
        w = ratio.interpolate(w, end.w)
    )
}

operator fun IntVector4.plus(other: IntVector4): IntVector4 = IntVector4(x + other.x, y + other.y, z + other.z, w + other.w)
operator fun IntVector4.plus(other: Int): IntVector4 = IntVector4(x + other, y + other, z + other, w + other)

operator fun IntVector4.minus(other: IntVector4): IntVector4 = IntVector4(x - other.x, y - other.y, z - other.z, w - other.w)
operator fun IntVector4.minus(other: Int): IntVector4 = IntVector4(x - other, y - other, z - other, w - other)

operator fun IntVector4.times(other: IntVector4): IntVector4 = IntVector4(x * other.x, y * other.y, z * other.z, w * other.w)
operator fun IntVector4.times(other: Int): IntVector4 = IntVector4(x * other, y * other, z * other, w * other)

operator fun IntVector4.div(other: IntVector4): IntVector4 = IntVector4(x / other.x, y / other.y, z / other.z, w / other.w)
operator fun IntVector4.div(other: Int): IntVector4 = IntVector4(x / other, y / other, z / other, w / other)

operator fun IntVector4.rem(other: IntVector4): IntVector4 = IntVector4(x % other.x, y % other.y, z % other.z, w % other.w)
operator fun IntVector4.rem(other: Int): IntVector4 = IntVector4(x % other, y % other, z % other, w % other)

operator fun IntVector4.unaryMinus(): IntVector4 = IntVector4(-x, -y, -z, -w)
operator fun IntVector4.unaryPlus(): IntVector4 = IntVector4(x, y, z, w)