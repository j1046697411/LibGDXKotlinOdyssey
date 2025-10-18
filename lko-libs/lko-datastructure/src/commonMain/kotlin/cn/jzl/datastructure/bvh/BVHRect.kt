package cn.jzl.datastructure.bvh

import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low

/**
 * 表示BVH中的矩形边界
 * 优化：减少重复计算，提高面积计算效率
 */
@JvmInline
value class BVHRect private constructor(val data: Long) {
    val minOffset: Int get() = data.low
    val maxOffset: Int get() = data.high

    // 访问器方法
    fun min(bvh: BVH<*>, dimension: Int): Float = bvh.dimension(minOffset, dimension)
    fun min(bvh: BVH<*>, dimension: Int, value: Float): Unit = bvh.dimension(minOffset, dimension, value)

    fun max(bvh: BVH<*>, dimension: Int): Float = bvh.dimension(maxOffset, dimension)
    fun max(bvh: BVH<*>, dimension: Int, value: Float): Unit = bvh.dimension(maxOffset, dimension, value)

    // 计算指定维度的大小
    fun size(bvh: BVH<*>, dimension: Int): Float = max(bvh, dimension) - min(bvh, dimension)

    // 计算中心点坐标
    fun center(bvh: BVH<*>, dimension: Int): Float = (min(bvh, dimension) + max(bvh, dimension)) * 0.5f

    fun set(bvh: BVH<*>, other: BVHRect) {
        for (dim in 0 until bvh.dimensions) {
            min(bvh, dim, other.min(bvh, dim))
            max(bvh, dim, other.max(bvh, dim))
        }
    }

    // 计算矩形面积
    fun area(bvh: BVH<*>): Float {
        var area = 0f
        for (i in 0 until bvh.dimensions) {
            val size = size(bvh, i)
            area += size * size
        }
        return area
    }

    // 计算两个矩形合并后的面积增量
    fun combinedArea(bvh: BVH<*>, other: BVHRect): Float {
        var combinedArea = 0f
        for (dim in 0 until bvh.dimensions) {
            val min = minOf(this.min(bvh, dim), other.min(bvh, dim))
            val max = maxOf(this.max(bvh, dim), other.max(bvh, dim))
            val size = max - min
            combinedArea += size * size
        }
        return combinedArea
    }

    companion object {
        operator fun invoke(minOffset: Int, maxOffset: Int): BVHRect = BVHRect(Long.Companion.fromLowHigh(minOffset, maxOffset))
    }
}