package cn.jzl.lko.geom

import cn.jzl.lko.geom.vector.Vector4

@JvmInline
value class Matrix4(@PublishedApi internal val data: FloatArray) : Matrix<Float>, IsAlmostEquals<Matrix4> {

    init {
        check(data.size == dimensions) { "Matrix4 data size must be 16" }
    }

    override val dimensions: Int get() = 16

    override fun get(row: Int, column: Int): Float = data[row * 4 + column]

    override fun set(row: Int, column: Int, value: Float) {
        data[row * 4 + column] = value
    }

    override fun isAlmostEquals(other: Matrix4, epsilon: Float): Boolean {
        for (i in 0 until dimensions) {
            if (!data[i].isAlmostEquals(other.data[i], epsilon)) return false
        }
        return true
    }

    companion object {
        val zero: Matrix4 = Matrix4(floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))
        val identity: Matrix4 = Matrix4(floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f))
    }
}

inline val Matrix4.v00: Float get() = data[0]
inline val Matrix4.v01: Float get() = data[1]
inline val Matrix4.v02: Float get() = data[2]
inline val Matrix4.v03: Float get() = data[3]

inline val Matrix4.v10: Float get() = data[4]
inline val Matrix4.v11: Float get() = data[5]
inline val Matrix4.v12: Float get() = data[6]
inline val Matrix4.v13: Float get() = data[7]

inline val Matrix4.v20: Float get() = data[8]
inline val Matrix4.v21: Float get() = data[9]
inline val Matrix4.v22: Float get() = data[10]
inline val Matrix4.v23: Float get() = data[11]

inline val Matrix4.v30: Float get() = data[12]
inline val Matrix4.v31: Float get() = data[13]
inline val Matrix4.v32: Float get() = data[14]
inline val Matrix4.v33: Float get() = data[15]

inline val Matrix4.transpose: Matrix4 get() = Matrix4(floatArrayOf(v00, v10, v20, v30, v01, v11, v21, v31, v02, v12, v22, v32, v03, v13, v23, v33))

inline val Matrix4.column1: Vector4 get() = Vector4(v00, v10, v20, v30)
inline val Matrix4.column2: Vector4 get() = Vector4(v01, v11, v21, v31)
inline val Matrix4.column3: Vector4 get() = Vector4(v02, v12, v22, v32)
inline val Matrix4.column4: Vector4 get() = Vector4(v03, v13, v23, v33)
inline val Matrix4.columns: Sequence<Vector4>
    get() = sequence {
        yield(column1)
        yield(column2)
        yield(column3)
        yield(column4)
    }

inline val Matrix4.row1: Vector4 get() = Vector4(v00, v01, v02, v03)
inline val Matrix4.row2: Vector4 get() = Vector4(v10, v11, v12, v13)
inline val Matrix4.row3: Vector4 get() = Vector4(v20, v21, v22, v23)
inline val Matrix4.row4: Vector4 get() = Vector4(v30, v31, v32, v33)
inline val Matrix4.rows: Sequence<Vector4>
    get() = sequence {
        yield(row1)
        yield(row2)
        yield(row3)
        yield(row4)
    }


operator fun Matrix4.plus(other: Matrix4): Matrix4 = Matrix4(
    floatArrayOf(
        v00 + other.v00, v01 + other.v01, v02 + other.v02, v03 + other.v03,
        v10 + other.v10, v11 + other.v11, v12 + other.v12, v13 + other.v13,
        v20 + other.v20, v21 + other.v21, v22 + other.v22, v23 + other.v23,
        v30 + other.v30, v31 + other.v31, v32 + other.v32, v33 + other.v33
    )
)

operator fun Matrix4.minus(other: Matrix4): Matrix4 = Matrix4(
    floatArrayOf(
        v00 - other.v00, v01 - other.v01, v02 - other.v02, v03 - other.v03,
        v10 - other.v10, v11 - other.v11, v12 - other.v12, v13 - other.v13,
        v20 - other.v20, v21 - other.v21, v22 - other.v22, v23 - other.v23,
        v30 - other.v30, v31 - other.v31, v32 - other.v32, v33 - other.v33
    )
)

operator fun Matrix4.times(other: Matrix4): Matrix4 = Matrix4(
    floatArrayOf(
        v00 * other.v00, v01 * other.v01, v02 * other.v02, v03 * other.v03,
        v10 * other.v10, v11 * other.v11, v12 * other.v12, v13 * other.v13,
        v20 * other.v20, v21 * other.v21, v22 * other.v22, v23 * other.v23,
        v30 * other.v30, v31 * other.v31, v32 * other.v32, v33 * other.v33
    )
)

operator fun Matrix4.times(other: Vector4): Vector4 = Vector4(
    v00 * other.x + v01 * other.y + v02 * other.z + v03 * other.w,
    v10 * other.x + v11 * other.y + v12 * other.z + v13 * other.w,
    v20 * other.x + v21 * other.y + v22 * other.z + v23 * other.w,
    v30 * other.x + v31 * other.y + v32 * other.z + v33 * other.w
)

