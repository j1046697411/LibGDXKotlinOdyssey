package cn.jzl.datastructure.bvh
import kotlin.jvm.JvmInline

import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low
import kotlin.math.sqrt

/**
 * 表示BVH中的射线
 * 优化：添加归一化和方向反转等辅助方法
 */
@JvmInline
value class BVHRay(val data: Long) {
    val originOffset: Int get() = data.low
    val directionOffset: Int get() = data.high

    // 访问器方法
    fun origin(bvh: BVH<*>, dimension: Int): Float = bvh.dimension(originOffset, dimension)
    fun origin(bvh: BVH<*>, dimension: Int, value: Float): Unit = bvh.dimension(originOffset, dimension, value)

    fun direction(bvh: BVH<*>, dimension: Int): Float = bvh.dimension(directionOffset, dimension)
    fun direction(bvh: BVH<*>, dimension: Int, value: Float): Unit = bvh.dimension(directionOffset, dimension, value)

    // 归一化射线方向
    fun normalize(bvh: BVH<*>): BVHRay {
        var lengthSquared = 0f
        for (dim in 0 until bvh.dimensions) {
            val dir = direction(bvh, dim)
            lengthSquared += dir * dir
        }
        val length = sqrt(lengthSquared)

        if (length > 0f) {
            val invLength = 1f / length
            for (dim in 0 until bvh.dimensions) {
                direction(bvh, dim, direction(bvh, dim) * invLength)
            }
        }
        return this
    }

    companion object {
        operator fun invoke(originOffset: Int, directionOffset: Int): BVHRay = BVHRay(Long.Companion.fromLowHigh(originOffset, directionOffset))
    }
}