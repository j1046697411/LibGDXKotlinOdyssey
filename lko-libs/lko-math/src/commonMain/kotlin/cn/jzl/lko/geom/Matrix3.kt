package cn.jzl.lko.geom

import cn.jzl.lko.geom.vector.Vector3

@JvmInline
value class Matrix3(@PublishedApi internal val data: FloatArray) : Matrix<Float>, IsAlmostEquals<Matrix3> {

    init {
        check(data.size == dimensions) { "Matrix3 data size must be 9" }
    }

    override val dimensions: Int get() = 9

    override fun get(row: Int, column: Int): Float = data[row * 3 + column]

    override fun set(row: Int, column: Int, value: Float) {
        data[row * 3 + column] = value
    }

    override fun isAlmostEquals(other: Matrix3, epsilon: Float): Boolean {
        for (i in 0 until dimensions) {
            if (!data[i].isAlmostEquals(other.data[i], epsilon)) return false
        }
        return true
    }

    companion object {
        val zero: Matrix3 = Matrix3(floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))
        val identity: Matrix3 = Matrix3(floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f))
    }
}

inline val Matrix3.v00: Float get() = data[0]
inline val Matrix3.v01: Float get() = data[1]
inline val Matrix3.v02: Float get() = data[2]
inline val Matrix3.v10: Float get() = data[3]
inline val Matrix3.v11: Float get() = data[4]
inline val Matrix3.v12: Float get() = data[5]
inline val Matrix3.v20: Float get() = data[6]
inline val Matrix3.v21: Float get() = data[7]
inline val Matrix3.v22: Float get() = data[8]

inline val Matrix3.transpose: Matrix3 get() = Matrix3(floatArrayOf(v00, v10, v20, v01, v11, v21, v02, v12, v22))

inline val Matrix3.column1: Vector3 get() = Vector3(v00, v10, v20)
inline val Matrix3.column2: Vector3 get() = Vector3(v01, v11, v21)
inline val Matrix3.column3: Vector3 get() = Vector3(v02, v12, v22)
inline val Matrix3.columns: Sequence<Vector3>
    get() = sequence {
        yield(column1)
        yield(column2)
        yield(column3)
    }


inline val Matrix3.row1: Vector3 get() = Vector3(v00, v01, v02)
inline val Matrix3.row2: Vector3 get() = Vector3(v10, v11, v12)
inline val Matrix3.row3: Vector3 get() = Vector3(v20, v21, v22)
inline val Matrix3.rows: Sequence<Vector3>
    get() = sequence {
        yield(row1)
        yield(row2)
        yield(row3)
    }


operator fun Matrix3.plus(other: Matrix3): Matrix3 = Matrix3(
    floatArrayOf(
        v00 + other.v00, v01 + other.v01, v02 + other.v02,
        v10 + other.v10, v11 + other.v11, v12 + other.v12,
        v20 + other.v20, v21 + other.v21, v22 + other.v22
    )
)

operator fun Matrix3.minus(other: Matrix3): Matrix3 = Matrix3(
    floatArrayOf(
        v00 - other.v00, v01 - other.v01, v02 - other.v02,
        v10 - other.v10, v11 - other.v11, v12 - other.v12,
        v20 - other.v20, v21 - other.v21, v22 - other.v22
    )
)

operator fun Matrix3.times(other: Matrix3): Matrix3 = Matrix3(
    floatArrayOf(
        v00 * other.v00, v01 * other.v01, v02 * other.v02,
        v10 * other.v10, v11 * other.v11, v12 * other.v12,
        v20 * other.v20, v21 * other.v21, v22 * other.v22
    )
)

operator fun Matrix3.div(other: Matrix3): Matrix3 = Matrix3(
    floatArrayOf(
        v00 / other.v00, v01 / other.v01, v02 / other.v02,
        v10 / other.v10, v11 / other.v11, v12 / other.v12,
        v20 / other.v20, v21 / other.v21, v22 / other.v22
    )
)

fun Matrix3.dot(other: Matrix3): Matrix3 = Matrix3(
    floatArrayOf(
        v00 * other.v00, v01 * other.v01, v02 * other.v02,
        v10 * other.v10, v11 * other.v11, v12 * other.v12,
        v20 * other.v20, v21 * other.v21, v22 * other.v22
    )
)


