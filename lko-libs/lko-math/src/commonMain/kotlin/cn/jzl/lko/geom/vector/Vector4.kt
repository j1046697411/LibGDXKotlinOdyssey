package cn.jzl.lko.geom.vector

data class Vector4(
    override val x: Float,
    override val y: Float,
    override val z: Float,
    override val w: Float
) : IVector4<Float>

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
