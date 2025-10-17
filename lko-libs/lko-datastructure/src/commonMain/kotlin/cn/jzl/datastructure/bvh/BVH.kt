package cn.jzl.datastructure.bvh

import cn.jzl.datastructure.list.FloatFastList
import cn.jzl.datastructure.list.IntFastList
import cn.jzl.datastructure.list.ListEditor
import cn.jzl.datastructure.math.vector.generic.Dimension

/**
 * 边界体积层次结构(BVH)树实现
 */
class BVH<T>(override val dimensions: Int) : Dimension {

    internal val data = FloatFastList(8 * dimensions)
    private val recycledNodeIds = IntFastList(8)

    internal fun dimension(offset: Int, dimension: Int): Float = data[offset * dimensions + dimension]

    internal fun dimension(offset: Int, dimension: Int, value: Float) {
        data[offset * dimensions + dimension] = value
    }

    // 优化的矩形创建，避免数组分配
    internal fun rect(
        block: ListEditor<Float>.() -> Unit = { (0 until (dimensions * 2)).forEach { _ -> unsafeInsert(0f) } }
    ): BVHRect<T> {
        val offset = if (recycledNodeIds.isNotEmpty()) {
            val offset = recycledNodeIds.removeLast()
            var index = offset * dimensions
            ListEditor<Float> { data[index++] = it }.apply(block)
            offset
        } else {
            val offset = data.size / (dimensions * 2)
            data.safeInsertLast(dimensions * 2, block)
            offset
        }
        return BVHRect(offset)
    }

    interface BVHNode<T> {
        val id: Int
        val bvh: BVH<T>
        val rect: BVHRect<T>
    }

    @JvmInline
    value class BVHRect<T>(val offset: Int) {
        fun min(bvh: BVH<T>, dimension: Int): Float = bvh.dimension(offset, dimension)
        fun max(bvh: BVH<T>, dimension: Int): Float = bvh.dimension(offset + 1, dimension)
        fun size(bvh: BVH<T>, dimension: Int): Float = max(bvh, dimension) - min(bvh, dimension)
    }

    @JvmInline
    value class BVHRay<T>(val offset: Int) {
        fun origin(bvh: BVH<T>, dimension: Int): Float = bvh.dimension(offset, dimension)
        fun direction(bvh: BVH<T>, dimension: Int): Float = bvh.dimension(offset + 1, dimension)
    }
}