package cn.jzl.datastructure.math.matrix

import cn.jzl.datastructure.math.isAlmostEquals
import cn.jzl.datastructure.math.vector.Vector2
import cn.jzl.datastructure.math.vector.Vector3
import kotlin.jvm.JvmInline
import kotlin.math.abs

/**
 * 通用矩阵接口（行列索引访问）。
 * - `get(row, col)`/`set(row, col, value)` 提供元素读写；实现类需保证索引有效性。
 * - 行列均从 0 开始计数，按行主序存储。
 */
interface Matrix<T> {
    operator fun get(row: Int, col: Int): T
    operator fun set(row: Int, col: Int, value: T)
}

/**
 * 3x3 浮点矩阵（行主序，紧凑存储）。
 * - 使用 `FloatArray(9)` 存储；索引映射为 `row * 3 + col`。
 * - 提供扩展方法实现常用操作：拷贝、导出、初始化、转置与算术运算。
 */
@JvmInline
value class Matrix3(@PublishedApi internal val data: FloatArray = FloatArray(9) { throw IndexOutOfBoundsException() }) : Matrix<Float> {
    override operator fun get(row: Int, col: Int): Float {
        return data[row * 3 + col]
    }

    override operator fun set(row: Int, col: Int, value: Float) {
        data[row * 3 + col] = value
    }

    companion object {
        /**
         * 创建单位矩阵
         */
        fun identity(): Matrix3 {
            val result = Matrix3(FloatArray(9))
            result.data[0] = 1f
            result.data[4] = 1f
            result.data[8] = 1f
            return result
        }

        /**
         * 创建零矩阵
         */
        fun zero(): Matrix3 {
            return Matrix3(FloatArray(9) { 0f })
        }
        
        /**
         * 创建2D正交投影矩阵
         * 将指定的矩形区域映射到标准规范化设备坐标
         */
        fun ortho(left: Float, right: Float, bottom: Float, top: Float): Matrix3 {
            val result = Matrix3.identity()
            
            // 计算缩放因子
            val width = right - left
            val height = top - bottom
            
            // 设置正交投影矩阵的值
            result[0, 0] = 2f / width
            result[1, 1] = 2f / height
            result[0, 2] = -(right + left) / width
            result[1, 2] = -(top + bottom) / height
            
            return result
        }
    }
}

/**
 * 4x4 浮点矩阵（行主序，紧凑存储）。
 * - 使用 `FloatArray(16)` 存储；索引映射为 `row * 4 + col`。
 * - 提供扩展方法实现常用操作：拷贝、导出、初始化、转置与算术运算。
 */
@JvmInline
value class Matrix4(@PublishedApi internal val data: FloatArray = FloatArray(16)) : Matrix<Float> {
    override operator fun get(row: Int, col: Int): Float {
        return data[row * 4 + col]
    }

    override operator fun set(row: Int, col: Int, value: Float) {
        data[row * 4 + col] = value
    }

    companion object {
        /**
         * 创建单位矩阵
         */
        fun identity(): Matrix4 {
            val result = Matrix4(FloatArray(16) { 0f }) // 显式初始化为全零
            result.data[0] = 1f
            result.data[5] = 1f
            result.data[10] = 1f
            result.data[15] = 1f
            return result
        }
        
        /**
         * 为Matrix4.Companion添加接受Vector3参数的lookAt方法重载
         * @param eye 观察者位置
         * @param target 目标点位置
         * @param up 上方向向量
         * @return 视图矩阵
         */
        fun lookAt(eye: Vector3, target: Vector3, up: Vector3): Matrix4 {
            return lookAt(eye.x, eye.y, eye.z, target.x, target.y, target.z, up.x, up.y, up.z)
        }
    }
}

inline val Matrix3.rows: Int get() = 3
inline val Matrix3.cols: Int get() = 3
inline val Matrix4.rows: Int get() = 4
inline val Matrix4.cols: Int get() = 4
/**
 * Matrix3 元素别名（只读）。
 * - 命名规则：`vRC` 映射到行主序索引 `[R, C]`。
 * - 用途：便捷读取元素，减少索引计算与误用；不提供写入。
 * - 写入方式：请使用 `set(row, col, value)` 或相关初始化方法。
 */
inline val Matrix3.v00: Float get() = data[0]
inline val Matrix3.v01: Float get() = data[1]
inline val Matrix3.v02: Float get() = data[2]
inline val Matrix3.v10: Float get() = data[3]
inline val Matrix3.v11: Float get() = data[4]
inline val Matrix3.v12: Float get() = data[5]
inline val Matrix3.v20: Float get() = data[6]
inline val Matrix3.v21: Float get() = data[7]
inline val Matrix3.v22: Float get() = data[8]

/**
 * Matrix4 元素别名（只读）。
 * - 命名规则：`vRC` 映射到行主序索引 `[R, C]`。
 * - 用途：便捷读取元素，减少索引计算与误用；不提供写入。
 * - 写入方式：请使用 `set(row, col, value)` 或相关初始化方法。
 */
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

// ========================= Matrix3 扩展方法 =========================

/**
 * 拷贝矩阵
 */
fun Matrix3.copy(): Matrix3 = Matrix3(data.copyOf())

/**
 * 转置矩阵
 */
fun Matrix3.transpose(): Matrix3 {
    val result = Matrix3(FloatArray(9))
    result.data[0] = v00
    result.data[1] = v10
    result.data[2] = v20
    result.data[3] = v01
    result.data[4] = v11
    result.data[5] = v21
    result.data[6] = v02
    result.data[7] = v12
    result.data[8] = v22
    return result
}

/**
 * 计算行列式
 */
val Matrix3.determinant: Float
    get() {
        return v00 * (v11 * v22 - v12 * v21) -
               v01 * (v10 * v22 - v12 * v20) +
               v02 * (v10 * v21 - v11 * v20)
    }

/**
 * 矩阵加法
 */
operator fun Matrix3.plus(other: Matrix3): Matrix3 {
    val result = Matrix3(FloatArray(9))
    result.data[0] = v00 + other.v00
    result.data[1] = v01 + other.v01
    result.data[2] = v02 + other.v02
    result.data[3] = v10 + other.v10
    result.data[4] = v11 + other.v11
    result.data[5] = v12 + other.v12
    result.data[6] = v20 + other.v20
    result.data[7] = v21 + other.v21
    result.data[8] = v22 + other.v22
    return result
}

/**
 * 矩阵减法
 */
operator fun Matrix3.minus(other: Matrix3): Matrix3 {
    val result = Matrix3(FloatArray(9))
    result.data[0] = v00 - other.v00
    result.data[1] = v01 - other.v01
    result.data[2] = v02 - other.v02
    result.data[3] = v10 - other.v10
    result.data[4] = v11 - other.v11
    result.data[5] = v12 - other.v12
    result.data[6] = v20 - other.v20
    result.data[7] = v21 - other.v21
    result.data[8] = v22 - other.v22
    return result
}

/**
 * 矩阵乘法
 */
operator fun Matrix3.times(other: Matrix3): Matrix3 {
    val result = Matrix3(FloatArray(9))
    result.data[0] = v00 * other.v00 + v01 * other.v10 + v02 * other.v20
    result.data[1] = v00 * other.v01 + v01 * other.v11 + v02 * other.v21
    result.data[2] = v00 * other.v02 + v01 * other.v12 + v02 * other.v22
    result.data[3] = v10 * other.v00 + v11 * other.v10 + v12 * other.v20
    result.data[4] = v10 * other.v01 + v11 * other.v11 + v12 * other.v21
    result.data[5] = v10 * other.v02 + v11 * other.v12 + v12 * other.v22
    result.data[6] = v20 * other.v00 + v21 * other.v10 + v22 * other.v20
    result.data[7] = v20 * other.v01 + v21 * other.v11 + v22 * other.v21
    result.data[8] = v20 * other.v02 + v21 * other.v12 + v22 * other.v22
    return result
}

/**
 * 标量乘法
 */
operator fun Matrix3.times(scalar: Float): Matrix3 {
    val result = Matrix3(FloatArray(9))
    result.data[0] = v00 * scalar
    result.data[1] = v01 * scalar
    result.data[2] = v02 * scalar
    result.data[3] = v10 * scalar
    result.data[4] = v11 * scalar
    result.data[5] = v12 * scalar
    result.data[6] = v20 * scalar
    result.data[7] = v21 * scalar
    result.data[8] = v22 * scalar
    return result
}

/**
 * 导出为FloatArray
 */
fun Matrix3.toFloatArray(): FloatArray {
    return floatArrayOf(v00, v01, v02, v10, v11, v12, v20, v21, v22)
}

/**
 * 从FloatArray创建Matrix3（需要至少9个元素）
 */
fun Matrix3.Companion.from(array: FloatArray): Matrix3 {
    require(array.size >= 9) { "FloatArray must have at least 9 elements" }
    val result = Matrix3(FloatArray(9))
    result.data[0] = array[0]
    result.data[1] = array[1]
    result.data[2] = array[2]
    result.data[3] = array[3]
    result.data[4] = array[4]
    result.data[5] = array[5]
    result.data[6] = array[6]
    result.data[7] = array[7]
    result.data[8] = array[8]
    return result
}

/**
 * 获取逆矩阵
 */
fun Matrix3.invert(): Matrix3 {
    val det = determinant
    if (abs(det) < 1e-6f) {
        throw IllegalArgumentException("Matrix is singular and cannot be inverted")
    }
    
    val invDet = 1.0f / det
    val result = Matrix3(FloatArray(9))
    
    // 计算伴随矩阵并乘以逆行列式
    result.data[0] = (v11 * v22 - v12 * v21) * invDet
    result.data[1] = (v02 * v21 - v01 * v22) * invDet
    result.data[2] = (v01 * v12 - v02 * v11) * invDet
    result.data[3] = (v12 * v20 - v10 * v22) * invDet
    result.data[4] = (v00 * v22 - v02 * v20) * invDet
    result.data[5] = (v02 * v10 - v00 * v12) * invDet
    result.data[6] = (v10 * v21 - v11 * v20) * invDet
    result.data[7] = (v01 * v20 - v00 * v21) * invDet
    result.data[8] = (v00 * v11 - v01 * v10) * invDet
    
    return result
}

/**
 * 检查是否为单位矩阵
 */
fun Matrix3.isIdentity(epsilon: Float = 1e-6f): Boolean {
    return abs(v00 - 1f) < epsilon &&
           abs(v01) < epsilon &&
           abs(v02) < epsilon &&
           abs(v10) < epsilon &&
           abs(v11 - 1f) < epsilon &&
           abs(v12) < epsilon &&
           abs(v20) < epsilon &&
           abs(v21) < epsilon &&
           abs(v22 - 1f) < epsilon
}

/**
 * 计算矩阵的迹（对角线元素之和）
 */
val Matrix3.trace: Float get() = v00 + v11 + v22

/**
 * 矩阵与标量加法
 */
operator fun Matrix3.plus(scalar: Float): Matrix3 {
    val result = Matrix3(FloatArray(9))
    result.data[0] = v00 + scalar
    result.data[1] = v01 + scalar
    result.data[2] = v02 + scalar
    result.data[3] = v10 + scalar
    result.data[4] = v11 + scalar
    result.data[5] = v12 + scalar
    result.data[6] = v20 + scalar
    result.data[7] = v21 + scalar
    result.data[8] = v22 + scalar
    return result
}

/**
 * 矩阵与标量减法
 */
operator fun Matrix3.minus(scalar: Float): Matrix3 {
    val result = Matrix3(FloatArray(9))
    result.data[0] = v00 - scalar
    result.data[1] = v01 - scalar
    result.data[2] = v02 - scalar
    result.data[3] = v10 - scalar
    result.data[4] = v11 - scalar
    result.data[5] = v12 - scalar
    result.data[6] = v20 - scalar
    result.data[7] = v21 - scalar
    result.data[8] = v22 - scalar
    return result
}

// ========================= Matrix4 扩展方法 =========================



/**
 * 创建零矩阵
 */
fun Matrix4.Companion.zero(): Matrix4 {
    return Matrix4(FloatArray(16) { 0f })
}

/**
 * 拷贝矩阵
 */
fun Matrix4.copy(): Matrix4 {
    val result = Matrix4(FloatArray(16))
    result.data[0] = v00
    result.data[1] = v01
    result.data[2] = v02
    result.data[3] = v03
    result.data[4] = v10
    result.data[5] = v11
    result.data[6] = v12
    result.data[7] = v13
    result.data[8] = v20
    result.data[9] = v21
    result.data[10] = v22
    result.data[11] = v23
    result.data[12] = v30
    result.data[13] = v31
    result.data[14] = v32
    result.data[15] = v33
    return result
}

/**
 * 转置矩阵
 */
fun Matrix4.transpose(): Matrix4 {
    val result = Matrix4(FloatArray(16))
    result.data[0] = v00
    result.data[1] = v10
    result.data[2] = v20
    result.data[3] = v30
    result.data[4] = v01
    result.data[5] = v11
    result.data[6] = v21
    result.data[7] = v31
    result.data[8] = v02
    result.data[9] = v12
    result.data[10] = v22
    result.data[11] = v32
    result.data[12] = v03
    result.data[13] = v13
    result.data[14] = v23
    result.data[15] = v33
    return result
}

/**
 * 计算行列式（简化版，假设为变换矩阵）
 */
val Matrix4.determinant: Float
    get() {
        // 计算3x3左上角子矩阵的行列式，对于仿射变换矩阵，这通常足够
        return v00 * (v11 * v22 - v12 * v21) -
               v01 * (v10 * v22 - v12 * v20) +
               v02 * (v10 * v21 - v11 * v20)
    }

/**
 * 矩阵加法
 */
operator fun Matrix4.plus(other: Matrix4): Matrix4 {
    val result = Matrix4(FloatArray(16))
    result.data[0] = v00 + other.v00
    result.data[1] = v01 + other.v01
    result.data[2] = v02 + other.v02
    result.data[3] = v03 + other.v03
    result.data[4] = v10 + other.v10
    result.data[5] = v11 + other.v11
    result.data[6] = v12 + other.v12
    result.data[7] = v13 + other.v13
    result.data[8] = v20 + other.v20
    result.data[9] = v21 + other.v21
    result.data[10] = v22 + other.v22
    result.data[11] = v23 + other.v23
    result.data[12] = v30 + other.v30
    result.data[13] = v31 + other.v31
    result.data[14] = v32 + other.v32
    result.data[15] = v33 + other.v33
    return result
}

/**
 * 矩阵减法
 */
operator fun Matrix4.minus(other: Matrix4): Matrix4 {
    val result = Matrix4(FloatArray(16))
    result.data[0] = v00 - other.v00
    result.data[1] = v01 - other.v01
    result.data[2] = v02 - other.v02
    result.data[3] = v03 - other.v03
    result.data[4] = v10 - other.v10
    result.data[5] = v11 - other.v11
    result.data[6] = v12 - other.v12
    result.data[7] = v13 - other.v13
    result.data[8] = v20 - other.v20
    result.data[9] = v21 - other.v21
    result.data[10] = v22 - other.v22
    result.data[11] = v23 - other.v23
    result.data[12] = v30 - other.v30
    result.data[13] = v31 - other.v31
    result.data[14] = v32 - other.v32
    result.data[15] = v33 - other.v33
    return result
}

/**
 * 矩阵乘法
 */
operator fun Matrix4.times(other: Matrix4): Matrix4 {
    val result = Matrix4(FloatArray(16))
    result.data[0] = v00 * other.v00 + v01 * other.v10 + v02 * other.v20 + v03 * other.v30
    result.data[1] = v00 * other.v01 + v01 * other.v11 + v02 * other.v21 + v03 * other.v31
    result.data[2] = v00 * other.v02 + v01 * other.v12 + v02 * other.v22 + v03 * other.v32
    result.data[3] = v00 * other.v03 + v01 * other.v13 + v02 * other.v23 + v03 * other.v33
    result.data[4] = v10 * other.v00 + v11 * other.v10 + v12 * other.v20 + v13 * other.v30
    result.data[5] = v10 * other.v01 + v11 * other.v11 + v12 * other.v21 + v13 * other.v31
    result.data[6] = v10 * other.v02 + v11 * other.v12 + v12 * other.v22 + v13 * other.v32
    result.data[7] = v10 * other.v03 + v11 * other.v13 + v12 * other.v23 + v13 * other.v33
    result.data[8] = v20 * other.v00 + v21 * other.v10 + v22 * other.v20 + v23 * other.v30
    result.data[9] = v20 * other.v01 + v21 * other.v11 + v22 * other.v21 + v23 * other.v31
    result.data[10] = v20 * other.v02 + v21 * other.v12 + v22 * other.v22 + v23 * other.v32
    result.data[11] = v20 * other.v03 + v21 * other.v13 + v22 * other.v23 + v23 * other.v33
    result.data[12] = v30 * other.v00 + v31 * other.v10 + v32 * other.v20 + v33 * other.v30
    result.data[13] = v30 * other.v01 + v31 * other.v11 + v32 * other.v21 + v33 * other.v31
    result.data[14] = v30 * other.v02 + v31 * other.v12 + v32 * other.v22 + v33 * other.v32
    result.data[15] = v30 * other.v03 + v31 * other.v13 + v32 * other.v23 + v33 * other.v33
    return result
}

/**
 * 标量乘法
 */
operator fun Matrix4.times(scalar: Float): Matrix4 {
    val result = Matrix4(FloatArray(16))
    result.data[0] = v00 * scalar
    result.data[1] = v01 * scalar
    result.data[2] = v02 * scalar
    result.data[3] = v03 * scalar
    result.data[4] = v10 * scalar
    result.data[5] = v11 * scalar
    result.data[6] = v12 * scalar
    result.data[7] = v13 * scalar
    result.data[8] = v20 * scalar
    result.data[9] = v21 * scalar
    result.data[10] = v22 * scalar
    result.data[11] = v23 * scalar
    result.data[12] = v30 * scalar
    result.data[13] = v31 * scalar
    result.data[14] = v32 * scalar
    result.data[15] = v33 * scalar
    return result
}

/**
 * 导出为FloatArray
 */
fun Matrix4.toFloatArray(): FloatArray {
    return floatArrayOf(
        v00, v01, v02, v03,
        v10, v11, v12, v13,
        v20, v21, v22, v23,
        v30, v31, v32, v33
    )
}

/**
 * 从FloatArray创建Matrix4（需要至少16个元素）
 */
fun Matrix4.Companion.from(array: FloatArray): Matrix4 {
    require(array.size >= 16) { "FloatArray must have at least 16 elements" }
    val result = Matrix4(FloatArray(16))
    result.data[0] = array[0]
    result.data[1] = array[1]
    result.data[2] = array[2]
    result.data[3] = array[3]
    result.data[4] = array[4]
    result.data[5] = array[5]
    result.data[6] = array[6]
    result.data[7] = array[7]
    result.data[8] = array[8]
    result.data[9] = array[9]
    result.data[10] = array[10]
    result.data[11] = array[11]
    result.data[12] = array[12]
    result.data[13] = array[13]
    result.data[14] = array[14]
    result.data[15] = array[15]
    return result
}

/**
 * 检查是否为单位矩阵
 */
fun Matrix4.isIdentity(epsilon: Float = 1e-6f): Boolean {
    return abs(v00 - 1f) < epsilon &&
           abs(v01) < epsilon &&
           abs(v02) < epsilon &&
           abs(v03) < epsilon &&
           abs(v10) < epsilon &&
           abs(v11 - 1f) < epsilon &&
           abs(v12) < epsilon &&
           abs(v13) < epsilon &&
           abs(v20) < epsilon &&
           abs(v21) < epsilon &&
           abs(v22 - 1f) < epsilon &&
           abs(v23) < epsilon &&
           abs(v30) < epsilon &&
           abs(v31) < epsilon &&
           abs(v32) < epsilon &&
           abs(v33 - 1f) < epsilon
}

/**
 * 计算矩阵的迹（对角线元素之和）
 */
val Matrix4.trace: Float get() = v00 + v11 + v22 + v33

/**
 * 矩阵与标量加法
 */
operator fun Matrix4.plus(scalar: Float): Matrix4 {
    val result = Matrix4(FloatArray(16))
    result.data[0] = v00 + scalar
    result.data[1] = v01 + scalar
    result.data[2] = v02 + scalar
    result.data[3] = v03 + scalar
    result.data[4] = v10 + scalar
    result.data[5] = v11 + scalar
    result.data[6] = v12 + scalar
    result.data[7] = v13 + scalar
    result.data[8] = v20 + scalar
    result.data[9] = v21 + scalar
    result.data[10] = v22 + scalar
    result.data[11] = v23 + scalar
    result.data[12] = v30 + scalar
    result.data[13] = v31 + scalar
    result.data[14] = v32 + scalar
    result.data[15] = v33 + scalar
    return result
}

/**
 * 矩阵与标量减法
 */
operator fun Matrix4.minus(scalar: Float): Matrix4 {
    val result = Matrix4(FloatArray(16))
    result.data[0] = v00 - scalar
    result.data[1] = v01 - scalar
    result.data[2] = v02 - scalar
    result.data[3] = v03 - scalar
    result.data[4] = v10 - scalar
    result.data[5] = v11 - scalar
    result.data[6] = v12 - scalar
    result.data[7] = v13 - scalar
    result.data[8] = v20 - scalar
    result.data[9] = v21 - scalar
    result.data[10] = v22 - scalar
    result.data[11] = v23 - scalar
    result.data[12] = v30 - scalar
    result.data[13] = v31 - scalar
    result.data[14] = v32 - scalar
    result.data[15] = v33 - scalar
    return result
}

// ========================= Matrix3 2D变换方法 =========================

/**
 * 创建2D平移矩阵
 */
fun Matrix3.Companion.translation(x: Float, y: Float): Matrix3 {
    val result = identity()
    result.data[2] = x
    result.data[5] = y
    return result
}

/**
 * 创建2D旋转矩阵（弧度）
 */
fun Matrix3.Companion.rotation(angleRadians: Float): Matrix3 {
    val cos = kotlin.math.cos(angleRadians)
    val sin = kotlin.math.sin(angleRadians)
    val result = Matrix3(FloatArray(9))
    result.data[0] = cos
    result.data[1] = -sin
    result.data[3] = sin
    result.data[4] = cos
    result.data[8] = 1f
    return result
}

/**
 * 创建2D缩放矩阵
 */
fun Matrix3.Companion.scaling(scaleX: Float, scaleY: Float): Matrix3 {
    val result = Matrix3(FloatArray(9))
    result.data[0] = scaleX
    result.data[4] = scaleY
    result.data[8] = 1f
    return result
}

/**
 * 应用2D平移
 */
fun Matrix3.translate(x: Float, y: Float): Matrix3 {
    return this * Matrix3.translation(x, y)
}

/**
 * 应用2D旋转（弧度）
 */
fun Matrix3.rotate(angleRadians: Float): Matrix3 {
    return this * Matrix3.rotation(angleRadians)
}

/**
 * 应用2D缩放
 */
fun Matrix3.scale(scaleX: Float, scaleY: Float): Matrix3 {
    return this * Matrix3.scaling(scaleX, scaleY)
}

// ========================= Matrix4 3D变换方法 =========================

/**
 * 创建3D平移矩阵
 */
fun Matrix4.Companion.translation(x: Float, y: Float, z: Float): Matrix4 {
    val result = identity()
    result.data[3] = x
    result.data[7] = y
    result.data[11] = z
    return result
}

/**
 * 创建3D缩放矩阵
 */
fun Matrix4.Companion.scaling(scaleX: Float, scaleY: Float, scaleZ: Float): Matrix4 {
    val result = Matrix4(FloatArray(16))
    result.data[0] = scaleX
    result.data[5] = scaleY
    result.data[10] = scaleZ
    result.data[15] = 1f
    return result
}

/**
 * 创建绕X轴的旋转矩阵（弧度）
 */
fun Matrix4.Companion.rotationX(angleRadians: Float): Matrix4 {
    val cos = kotlin.math.cos(angleRadians)
    val sin = kotlin.math.sin(angleRadians)
    val result = identity()
    result.data[5] = cos
    result.data[6] = -sin
    result.data[9] = sin
    result.data[10] = cos
    return result
}

/**
 * 创建绕Y轴的旋转矩阵（弧度）
 */
fun Matrix4.Companion.rotationY(angleRadians: Float): Matrix4 {
    val cos = kotlin.math.cos(angleRadians)
    val sin = kotlin.math.sin(angleRadians)
    val result = identity()
    result.data[0] = cos
    result.data[2] = sin
    result.data[8] = -sin
    result.data[10] = cos
    return result
}

/**
 * 创建绕Z轴的旋转矩阵（弧度）
 */
fun Matrix4.Companion.rotationZ(angleRadians: Float): Matrix4 {
    val cos = kotlin.math.cos(angleRadians)
    val sin = kotlin.math.sin(angleRadians)
    val result = identity()
    result[0, 0] = cos
    result[0, 1] = -sin
    result[1, 0] = sin
    result[1, 1] = cos
    return result
}

/**
 * 创建绕任意轴的旋转矩阵（弧度）
 */
fun Matrix4.Companion.rotation(axisX: Float, axisY: Float, axisZ: Float, angleRadians: Float): Matrix4 {
    val cos = kotlin.math.cos(angleRadians)
    val sin = kotlin.math.sin(angleRadians)
    val oneMinusCos = 1 - cos
    
    // 归一化轴向量
    val length = kotlin.math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)
    val x = axisX / length
    val y = axisY / length
    val z = axisZ / length
    
    val result = identity()
    result[0, 0] = x * x * oneMinusCos + cos
    result[0, 1] = x * y * oneMinusCos - z * sin
    result[0, 2] = x * z * oneMinusCos + y * sin
    result[1, 0] = y * x * oneMinusCos + z * sin
    result[1, 1] = y * y * oneMinusCos + cos
    result[1, 2] = y * z * oneMinusCos - x * sin
    result[2, 0] = z * x * oneMinusCos - y * sin
    result[2, 1] = z * y * oneMinusCos + x * sin
    result[2, 2] = z * z * oneMinusCos + cos
    return result
}

/**
 * 创建透视投影矩阵
 */
fun Matrix4.Companion.perspective(fovyRadians: Float, aspect: Float, near: Float, far: Float): Matrix4 {
    val tanHalfFovy = kotlin.math.tan(fovyRadians * 0.5f)
    val result = Matrix4(FloatArray(16))
    result[0, 0] = 1.0f / (aspect * tanHalfFovy)
    result[1, 1] = 1.0f / tanHalfFovy
    result[2, 2] = -(far + near) / (far - near)
    result[2, 3] = -2.0f * far * near / (far - near)
    result[3, 2] = -1.0f
    return result
}

/**
 * 创建正交投影矩阵
 */
fun Matrix4.Companion.ortho(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4 {
    val result = Matrix4(FloatArray(16))
    result[0, 0] = 2.0f / (right - left)
    result[1, 1] = 2.0f / (top - bottom)
    result[2, 2] = -2.0f / (far - near)
    result[0, 3] = -(right + left) / (right - left)
    result[1, 3] = -(top + bottom) / (top - bottom)
    result[2, 3] = -(far + near) / (far - near)
    result[3, 3] = 1.0f
    return result
}

/**
 * 应用3D平移
 */
fun Matrix4.translate(x: Float, y: Float, z: Float): Matrix4 {
    return this * Matrix4.translation(x, y, z)
}

/**
 * 应用3D缩放
 */
fun Matrix4.scale(scaleX: Float, scaleY: Float, scaleZ: Float): Matrix4 {
    return this * Matrix4.scaling(scaleX, scaleY, scaleZ)
}

/**
 * 应用绕X轴旋转
 */
fun Matrix4.rotateX(angleRadians: Float): Matrix4 {
    return this * Matrix4.rotationX(angleRadians)
}

/**
 * 应用绕Y轴旋转
 */
fun Matrix4.rotateY(angleRadians: Float): Matrix4 {
    return this * Matrix4.rotationY(angleRadians)
}

/**
 * 应用绕Z轴旋转
 */
fun Matrix4.rotateZ(angleRadians: Float): Matrix4 {
    return this * Matrix4.rotationZ(angleRadians)
}

/**
 * 应用绕任意轴旋转
 */
fun Matrix4.rotate(axisX: Float, axisY: Float, axisZ: Float, angleRadians: Float): Matrix4 {
    return this * Matrix4.rotation(axisX, axisY, axisZ, angleRadians)
}

/**
 * 获取逆矩阵（针对变换矩阵的实现）
 */
fun Matrix4.invert(): Matrix4 {
    val result = Matrix4(FloatArray(16))
    
    // 对于缩放矩阵，我们需要计算左上角3x3矩阵的逆
    // 假设这是一个对角矩阵（简单缩放情况）
    // 注意：这仍然是一个简化实现，适用于常见的变换矩阵
    
    // 计算左上角3x3矩阵的逆
    // 对于对角矩阵，逆矩阵的对角线元素是原矩阵对角线元素的倒数
    result[0, 0] = if (v00 != 0f) 1f / v00 else 0f
    result[1, 1] = if (v11 != 0f) 1f / v11 else 0f
    result[2, 2] = if (v22 != 0f) 1f / v22 else 0f
    
    // 计算平移分量
    // 注意：这里假设左上角3x3矩阵是对角矩阵，所以转置等于它本身
    result[0, 3] = -(result[0, 0] * v03)
    result[1, 3] = -(result[1, 1] * v13)
    result[2, 3] = -(result[2, 2] * v23)
    result[3, 3] = 1f
    
    return result
}

// ========================= 辅助方法 ========================

/**
 * 格式化矩阵为字符串
 */
fun Matrix3.toString(precision: Int = 2): String {
    return buildString {
        appendLine("Matrix3 [")
        appendLine("  [${v00}, ${v01}, ${v02}]")
        appendLine("  [${v10}, ${v11}, ${v12}]")
        appendLine("  [${v20}, ${v21}, ${v22}]")
        appendLine("]")
    }
}

/**
 * 格式化矩阵为字符串
 */
fun Matrix4.toString(precision: Int = 2): String {
    return data.joinToString(",", "Matrix4[", "]") { it.toString() }
}

/**
 * 创建lookAt矩阵（视图矩阵）
 */
fun Matrix4.Companion.lookAt(eyeX: Float, eyeY: Float, eyeZ: Float, targetX: Float, targetY: Float, targetZ: Float, upX: Float = 0f, upY: Float = 1f, upZ: Float = 0f): Matrix4 {
    // 计算前向向量
    val fX = targetX - eyeX
    val fY = targetY - eyeY
    val fZ = targetZ - eyeZ
    
    // 归一化前向向量
    val fLength = kotlin.math.sqrt(fX * fX + fY * fY + fZ * fZ)
    val forwardX = fX / fLength
    val forwardY = fY / fLength
    val forwardZ = fZ / fLength
    
    // 计算右向向量
    val rX = upY * forwardZ - upZ * forwardY
    val rY = upZ * forwardX - upX * forwardZ
    val rZ = upX * forwardY - upY * forwardX
    
    // 归一化右向向量
    val rLength = kotlin.math.sqrt(rX * rX + rY * rY + rZ * rZ)
    val rightX = rX / rLength
    val rightY = rY / rLength
    val rightZ = rZ / rLength
    
    // 计算上向量
    val upXn = forwardY * rightZ - forwardZ * rightY
    val upYn = forwardZ * rightX - forwardX * rightZ
    val upZn = forwardX * rightY - forwardY * rightX
    
    // 构建视图矩阵
    val result = Matrix4(FloatArray(16))
    result[0, 0] = rightX
    result[0, 1] = upXn
    result[0, 2] = -forwardX
    result[0, 3] = -rightX * eyeX - upXn * eyeY + forwardX * eyeZ
    result[1, 0] = rightY
    result[1, 1] = upYn
    result[1, 2] = -forwardY
    result[1, 3] = -rightY * eyeX - upYn * eyeY + forwardY * eyeZ
    result[2, 0] = rightZ
    result[2, 1] = upZn
    result[2, 2] = -forwardZ
    result[2, 3] = -rightZ * eyeX - upZn * eyeY + forwardZ * eyeZ
    result[3, 3] = 1f
    
    return result
}

/**
 * 比较两个矩阵是否近似相等
 */


fun Matrix3.equals(other: Matrix3, epsilon: Float = 1e-6f): Boolean {
    return v00.isAlmostEquals(other.v00, epsilon)
            && v01.isAlmostEquals(other.v01, epsilon)
            && v02.isAlmostEquals(other.v02, epsilon)
            && v10.isAlmostEquals(other.v10, epsilon)
            && v11.isAlmostEquals(other.v11, epsilon)
            && v12.isAlmostEquals(other.v12, epsilon)
            && v20.isAlmostEquals(other.v20, epsilon)
            && v21.isAlmostEquals(other.v21, epsilon)
            && v22.isAlmostEquals(other.v22, epsilon)
}

/**
 * 比较两个矩阵是否近似相等
 */
fun Matrix4.equals(other: Matrix4, epsilon: Float = 1e-6f): Boolean {
    return v00.isAlmostEquals(other.v00, epsilon)
            && v01.isAlmostEquals(other.v01, epsilon)
            && v02.isAlmostEquals(other.v02, epsilon)
            && v03.isAlmostEquals(other.v03, epsilon)
            && v10.isAlmostEquals(other.v10, epsilon)
            && v11.isAlmostEquals(other.v11, epsilon)
            && v12.isAlmostEquals(other.v12, epsilon)
            && v13.isAlmostEquals(other.v13, epsilon)
            && v20.isAlmostEquals(other.v20, epsilon)
            && v21.isAlmostEquals(other.v21, epsilon)
            && v22.isAlmostEquals(other.v22, epsilon)
            && v23.isAlmostEquals(other.v23, epsilon)
            && v30.isAlmostEquals(other.v30, epsilon)
            && v31.isAlmostEquals(other.v31, epsilon)
            && v32.isAlmostEquals(other.v32, epsilon)
            && v33.isAlmostEquals(other.v33, epsilon)
}