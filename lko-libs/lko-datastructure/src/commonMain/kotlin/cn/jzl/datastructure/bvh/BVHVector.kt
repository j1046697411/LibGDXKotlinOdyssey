package cn.jzl.datastructure.bvh
import kotlin.jvm.JvmInline

/**
 * 表示BVH中的向量
 * 优化：添加向量操作辅助方法
 */
@JvmInline
value class BVHVector(val offset: Int) {
    // 访问器方法
    fun dimension(bvh: BVH<*>, dimension: Int): Float = bvh.dimension(offset, dimension)
    fun dimension(bvh: BVH<*>, dimension: Int, value: Float): Unit = bvh.dimension(offset, dimension, value)

    // 向量加法
    fun add(bvh: BVH<*>, other: BVHVector): BVHVector {
        val result = bvh.vector()
        for (dim in 0 until bvh.dimensions) {
            result.dimension(bvh, dim, this.dimension(bvh, dim) + other.dimension(bvh, dim))
        }
        return result
    }

    // 向量减法
    fun subtract(bvh: BVH<*>, other: BVHVector): BVHVector {
        val result = bvh.vector()
        for (dim in 0 until bvh.dimensions) {
            result.dimension(bvh, dim, this.dimension(bvh, dim) - other.dimension(bvh, dim))
        }
        return result
    }
}