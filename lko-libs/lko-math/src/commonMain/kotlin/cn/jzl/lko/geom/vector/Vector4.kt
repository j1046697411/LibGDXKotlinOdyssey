package cn.jzl.lko.geom.vector

import cn.jzl.lko.util.Interpolable
import cn.jzl.lko.util.Ratio
import cn.jzl.lko.util.interpolate

data class Vector4(
    override val x: Float,
    override val y: Float,
    override val z: Float,
    override val w: Float
) : IVector4<Float>, Interpolable<Vector4> {
    override fun interpolateTo(end: Vector4, ratio: Ratio): Vector4 = Vector4(
        x = ratio.interpolate(x, end.x),
        y = ratio.interpolate(y, end.y),
        z = ratio.interpolate(z, end.z),
        w = ratio.interpolate(w, end.w)
    )
}

operator fun Vector4.plus(other: Vector4): Vector4 = Vector4(x + other.x, y + other.y, z + other.z, w + other.w)
operator fun Vector4.plus(other: Float): Vector4 = Vector4(x + other, y + other, z + other, w + other)

operator fun Vector4.minus(other: Vector4): Vector4 = Vector4(x - other.x, y - other.y, z - other.z, w - other.w)
operator fun Vector4.minus(other: Float): Vector4 = Vector4(x - other, y - other, z - other, w - other)

operator fun Vector4.times(other: Vector4): Vector4 = Vector4(x * other.x, y * other.y, z * other.z, w * other.w)
operator fun Vector4.times(other: Float): Vector4 = Vector4(x * other, y * other, z * other, w * other)

operator fun Vector4.div(other: Vector4): Vector4 = Vector4(x / other.x, y / other.y, z / other.z, w / other.w)
operator fun Vector4.div(other: Float): Vector4 = Vector4(x / other, y / other, z / other, w / other)

operator fun Vector4.rem(other: Vector4): Vector4 = Vector4(x % other.x, y % other.y, z % other.z, w % other.w)
operator fun Vector4.rem(other: Float): Vector4 = Vector4(x % other, y % other, z % other, w % other)

operator fun Vector4.unaryMinus(): Vector4 = Vector4(-x, -y, -z, -w)
operator fun Vector4.unaryPlus(): Vector4 = Vector4(x, y, z, w)
