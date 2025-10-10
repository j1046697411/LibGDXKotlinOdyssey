package cn.jzl.lko.geom.vector

import cn.jzl.lko.util.Interpolable
import cn.jzl.lko.util.Ratio
import cn.jzl.lko.util.interpolate

data class Vector3(override val x: Float, override val y: Float, override val z: Float) : IVector3<Float>, Interpolable<Vector3> {

    override fun interpolateTo(end: Vector3, ratio: Ratio): Vector3  = Vector3(
        x = ratio.interpolate(x, end.x),
        y = ratio.interpolate(y, end.y),
        z = ratio.interpolate(z, end.z)
    )
}

operator fun Vector3.plus(other: Vector3): Vector3 = Vector3(x + other.x, y + other.y, z + other.z)
operator fun Vector3.plus(other: Float): Vector3 = Vector3(x + other, y + other, z + other)

operator fun Vector3.minus(other: Vector3): Vector3 = Vector3(x - other.x, y - other.y, z - other.z)
operator fun Vector3.minus(other: Float): Vector3 = Vector3(x - other, y - other, z - other)

operator fun Vector3.times(other: Vector3): Vector3 = Vector3(x * other.x, y * other.y, z * other.z)
operator fun Vector3.times(other: Float): Vector3 = Vector3(x * other, y * other, z * other)

operator fun Vector3.div(other: Vector3): Vector3 = Vector3(x / other.x, y / other.y, z / other.z)
operator fun Vector3.div(other: Float): Vector3 = Vector3(x / other, y / other, z / other)

operator fun Vector3.rem(other: Vector3): Vector3 = Vector3(x % other.x, y % other.y, z % other.z)
operator fun Vector3.rem(other: Float): Vector3 = Vector3(x % other, y % other, z % other)

operator fun Vector3.unaryMinus(): Vector3 = Vector3(-x, -y, -z)
operator fun Vector3.unaryPlus(): Vector3 = Vector3(x, y, z)
