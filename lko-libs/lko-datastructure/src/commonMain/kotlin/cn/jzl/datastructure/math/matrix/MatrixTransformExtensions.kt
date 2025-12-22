package cn.jzl.datastructure.math.matrix

import cn.jzl.datastructure.math.vector.Vector2
import cn.jzl.datastructure.math.vector.Vector3

/**
 * 使用Matrix3变换2D点向量
 * 将Vector2视为点(x, y)，通过矩阵乘法变换
 */
fun Matrix3.transform(vector: Vector2): Vector2 {
    val x = vector.x
    val y = vector.y
    
    // 矩阵乘法计算：M * [x, y, 1]^T
    val nx = this[0, 0] * x + this[0, 1] * y + this[0, 2] * 1f
    val ny = this[1, 0] * x + this[1, 1] * y + this[1, 2] * 1f
    
    return Vector2(nx, ny)
}

/**
 * 使用Matrix4变换3D点向量
 * 将Vector3视为点(x, y, z)，通过矩阵乘法变换
 */
fun Matrix4.transform(vector: Vector3): Vector3 {
    val x = vector.x
    val y = vector.y
    val z = vector.z

    // Matrix multiplication: M * [x, y, z, 1]^T
    val nx = this[0, 0] * x + this[0, 1] * y + this[0, 2] * z + this[0, 3]
    val ny = this[1, 0] * x + this[1, 1] * y + this[1, 2] * z + this[1, 3]
    val nz = this[2, 0] * x + this[2, 1] * y + this[2, 2] * z + this[2, 3]

    return Vector3(nx, ny, nz)
}

/**
 * 使用Matrix4变换3D方向向量
 * 忽略矩阵的平移部分，只应用旋转和缩放
 */
fun Matrix4.transformDirection(vector: Vector3): Vector3 {
    val x = vector.x
    val y = vector.y
    val z = vector.z
    
    // 矩阵乘法计算：M * [x, y, z, 0]^T（忽略平移）
    val nx = this[0, 0] * x + this[0, 1] * y + this[0, 2] * z
    val ny = this[1, 0] * x + this[1, 1] * y + this[1, 2] * z
    val nz = this[2, 0] * x + this[2, 1] * y + this[2, 2] * z
    
    return Vector3(nx, ny, nz)
}