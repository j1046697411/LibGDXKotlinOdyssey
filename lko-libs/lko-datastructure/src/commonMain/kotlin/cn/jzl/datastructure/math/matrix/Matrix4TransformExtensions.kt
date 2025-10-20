package cn.jzl.datastructure.math.matrix

import cn.jzl.datastructure.math.vector.Vector3

/**
 * 为Matrix4添加平移变换实例方法
 * @param x X轴平移量
 * @param y Y轴平移量
 * @param z Z轴平移量
 * @return 应用平移后的新矩阵
 */
fun Matrix4.translation(x: Float, y: Float, z: Float): Matrix4 {
    val result = this.copy()
    // 在4x4变换矩阵中，平移分量存储在最后一列
    // 先获取当前矩阵的平移分量
    val currX = result[0, 3]
    val currY = result[1, 3]
    val currZ = result[2, 3]
    // 应用平移（考虑当前矩阵的旋转缩放）
    result[0, 3] = currX + x * result[0, 0] + y * result[0, 1] + z * result[0, 2]
    result[1, 3] = currY + x * result[1, 0] + y * result[1, 1] + z * result[1, 2]
    result[2, 3] = currZ + x * result[2, 0] + y * result[2, 1] + z * result[2, 2]
    return result
}

/**
 * 为Matrix4添加Z轴旋转实例方法
 * @param angleRadians 旋转角度（弧度）
 * @return 应用旋转后的新矩阵
 */
fun Matrix4.rotationZ(angleRadians: Float): Matrix4 {
    val cos = kotlin.math.cos(angleRadians)
    val sin = kotlin.math.sin(angleRadians)
    // 创建Z轴旋转矩阵
    val rotation = Matrix4(FloatArray(16))
    rotation.data[0] = cos
    rotation.data[1] = -sin
    rotation.data[4] = sin
    rotation.data[5] = cos
    rotation.data[10] = 1f
    rotation.data[15] = 1f
    // 与当前矩阵相乘
    return this * rotation
}

/**
 * 为Matrix4添加X轴旋转实例方法
 * @param angleRadians 旋转角度（弧度）
 * @return 应用旋转后的新矩阵
 */
fun Matrix4.rotationX(angleRadians: Float): Matrix4 {
    val cos = kotlin.math.cos(angleRadians)
    val sin = kotlin.math.sin(angleRadians)
    // 创建X轴旋转矩阵
    val rotation = Matrix4(FloatArray(16))
    rotation.data[0] = 1f
    rotation.data[5] = cos
    rotation.data[6] = sin
    rotation.data[9] = -sin
    rotation.data[10] = cos
    rotation.data[15] = 1f
    // 与当前矩阵相乘
    return this * rotation
}

/**
 * 为Matrix4添加Y轴旋转实例方法
 * @param angleRadians 旋转角度（弧度）
 * @return 应用旋转后的新矩阵
 */
fun Matrix4.rotationY(angleRadians: Float): Matrix4 {
    val cos = kotlin.math.cos(angleRadians)
    val sin = kotlin.math.sin(angleRadians)
    // 创建Y轴旋转矩阵
    val rotation = Matrix4(FloatArray(16))
    rotation.data[0] = cos
    rotation.data[2] = -sin
    rotation.data[8] = sin
    rotation.data[10] = cos
    rotation.data[5] = 1f
    rotation.data[15] = 1f
    // 与当前矩阵相乘
    return this * rotation
}