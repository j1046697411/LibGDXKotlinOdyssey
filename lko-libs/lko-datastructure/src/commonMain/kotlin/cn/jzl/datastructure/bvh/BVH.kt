package cn.jzl.datastructure.bvh

import cn.jzl.datastructure.list.FloatFastList
import cn.jzl.datastructure.list.IntFastList
import cn.jzl.datastructure.list.ListEditor
import cn.jzl.datastructure.math.vector.generic.Dimension

/**
 * 边界体积层次结构(BVH)树实* 用于高效地进行空间划分和查询
 */
class BVH<T>(override val dimensions: Int) : Dimension {

    internal val data = FloatFastList(8 * dimensions)
    private val recycledNodeIds = IntFastList(8)

    /**
     * 获取指定偏移量和维度的值
     */
    internal fun dimension(offset: Int, dimension: Int): Float = data[offset * dimensions + dimension]

    /**
     * 设置指定偏移量和维度的值
     */
    internal fun dimension(offset: Int, dimension: Int, value: Float) {
        data[offset * dimensions + dimension] = value
    }

    /**
     * 优化的矩形创建，避免数组分配
     */
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

    /**
     * BVH节点接口
     */
    interface BVHNode<T> {
        val id: Int
        val bvh: BVH<T>
        val rect: BVHRect<T>
    }

    /**
     * 叶子节点实现
     */
    data class LeafNode<T>(
        override val id: Int,
        override val bvh: BVH<T>,
        override val rect: BVHRect<T>,
        val data: T
    ) : BVHNode<T>

    /**
     * 内部节点实现
     */
    data class InternalNode<T>(
        override val id: Int,
        override val bvh: BVH<T>,
        override val rect: BVHRect<T>,
        val left: BVHNode<T>,
        val right: BVHNode<T>
    ) : BVHNode<T>

    /**
     * 矩形表示，用于边界体积
     */
    @JvmInline
    value class BVHRect<T>(val offset: Int) {
        fun min(bvh: BVH<T>, dimension: Int): Float = bvh.dimension(offset, dimension)
        fun max(bvh: BVH<T>, dimension: Int): Float = bvh.dimension(offset + 1, dimension)
        fun size(bvh: BVH<T>, dimension: Int): Float = max(bvh, dimension) - min(bvh, dimension)
    }

    /**
     * 光线表示，用于射线相交测试
     */
    @JvmInline
    value class BVHRay<T>(val offset: Int) {
        fun origin(bvh: BVH<T>, dimension: Int): Float = bvh.dimension(offset, dimension)
        fun direction(bvh: BVH<T>, dimension: Int): Float = bvh.dimension(offset + 1, dimension)
    }

    /**
     * 构建BVH树
     */
    fun build(elements: List<Pair<T, (Int) -> FloatArray>>): BVHNode<T>? {
        if (elements.isEmpty()) {
            return null
        }

        val leaves = elements.mapIndexed { index, (data, boundsProvider) ->
            val bounds = boundsProvider(dimensions)
            val rect = rect {
                bounds.forEach { unsafeInsert(it) }
            }
            LeafNode(index, this, rect, data)
        }

        return buildTree(leaves)
    }

    /**
     * 递归构建BVH树
     */
    private fun buildTree(leaves: List<LeafNode<T>>): BVHNode<T> {
        if (leaves.size == 1) {
            return leaves[0]
        }

        // 找到最佳分割维度
        val splitDimension = findBestSplitDimension(leaves)
        
        // 按最佳维度排序
        val sortedLeaves = leaves.sortedBy { node ->
            (node.rect.min(this, splitDimension) + node.rect.max(this, splitDimension)) / 2f
        }

        // 分割为左右两部分
        val mid = sortedLeaves.size / 2
        val leftNodes = sortedLeaves.subList(0, mid)
        val rightNodes = sortedLeaves.subList(mid, sortedLeaves.size)

        // 递归构建左右子树
        val left = buildTree(leftNodes)
        val right = buildTree(rightNodes)

        // 创建父节点的包围盒
        val parentRect = createParentRect(left, right)
        
        // 创建内部节点
        return InternalNode(leaves.hashCode(), this, parentRect, left, right)
    }

    /**
     * 找到最佳分割维度
     */
    private fun findBestSplitDimension(leaves: List<LeafNode<T>>): Int {
        var bestDimension = 0
        var maxSpread = 0f

        for (dim in 0 until dimensions) {
            var min = Float.MAX_VALUE
            var max = -Float.MAX_VALUE

            for (leaf in leaves) {
                min = minOf(min, leaf.rect.min(this, dim))
                max = maxOf(max, leaf.rect.max(this, dim))
            }

            val spread = max - min
            if (spread > maxSpread) {
                maxSpread = spread
                bestDimension = dim
            }
        }

        return bestDimension
    }

    /**
     * 创建父节点的包围盒
     */
    private fun createParentRect(left: BVHNode<T>, right: BVHNode<T>): BVHRect<T> {
        return rect {
            for (dim in 0 until dimensions) {
                unsafeInsert(minOf(left.rect.min(this@BVH, dim), right.rect.min(this@BVH, dim)))
            }
            for (dim in 0 until dimensions) {
                unsafeInsert(maxOf(left.rect.max(this@BVH, dim), right.rect.max(this@BVH, dim)))
            }
        }
    }

    /**
     * 释放矩形资源
     */
    fun release(rect: BVHRect<T>) {
        recycledNodeIds.insertLast(rect.offset)
    }

    /**
     * 清除BVH树
     */
    fun clear() {
        data.clear()
        recycledNodeIds.clear()
    }

    /**
     * 射线与矩形相交测试
     */
    fun intersect(ray: BVHRay<T>, rect: BVHRect<T>): Boolean {
        var tMin = -Float.MAX_VALUE
        var tMax = Float.MAX_VALUE

        for (dim in 0 until dimensions) {
            val origin = ray.origin(this, dim)
            val direction = ray.direction(this, dim)
            val min = rect.min(this, dim)
            val max = rect.max(this, dim)

            if (direction == 0f) {
                if (origin < min || origin > max) {
                    return false
                }
            } else {
                val t1 = (min - origin) / direction
                val t2 = (max - origin) / direction

                tMin = maxOf(tMin, minOf(t1, t2))
                tMax = minOf(tMax, maxOf(t1, t2))
            }
        }

        return tMax >= maxOf(tMin, 0f)
    }

    /**
     * 查找与指定矩形相交的所有节点
     */
    fun query(rect: BVHRect<T>, root: BVHNode<T>): List<T> {
        val result = mutableListOf<T>()
        queryRecursive(rect, root, result)
        return result
    }

    /**
     * 递归查询相交节点
     */
    private fun queryRecursive(rect: BVHRect<T>, node: BVHNode<T>, result: MutableList<T>) {
        if (!overlaps(rect, node.rect)) {
            return
        }

        when (node) {
            is LeafNode -> {
                result.add(node.data)
            }
            is InternalNode -> {
                queryRecursive(rect, node.left, result)
                queryRecursive(rect, node.right, result)
            }
        }
    }

    /**
     * 检查两个矩形是否相交
     */
    private fun overlaps(a: BVHRect<T>, b: BVHRect<T>): Boolean {
        for (dim in 0 until dimensions) {
            if (a.max(this, dim) < b.min(this, dim) || a.min(this, dim) > b.max(this, dim)) {
                return false
            }
        }
        return true
    }

    /**
     * 计算BVH树的深度
     */
    fun computeDepth(root: BVHNode<T>?): Int {
        if (root == null) {
            return 0
        }

        return when (root) {
            is LeafNode -> 1
            is InternalNode -> 1 + maxOf(computeDepth(root.left), computeDepth(root.right))
            else -> 0
        }
    }

    /**
     * 获取所有叶子节点
     */
    fun getAllLeaves(root: BVHNode<T>?): List<LeafNode<T>> {
        val leaves = mutableListOf<LeafNode<T>>()
        collectLeaves(root, leaves)
        return leaves
    }

    /**
     * 递归收集叶子节点
     */
    private fun collectLeaves(node: BVHNode<T>?, leaves: MutableList<LeafNode<T>>) {
        if (node == null) {
            return
        }

        when (node) {
            is LeafNode -> leaves.add(node)
            is InternalNode -> {
                collectLeaves(node.left, leaves)
                collectLeaves(node.right, leaves)
            }
        }
    }

    /**
     * 插入新元素到BVH树
     */
    fun insert(root: BVHNode<T>?, data: T, boundsProvider: (Int) -> FloatArray): BVHNode<T> {
        val bounds = boundsProvider(dimensions)
        val rect = rect {
            bounds.forEach { unsafeInsert(it) }
        }
        val newLeaf = LeafNode(root.hashCode(), this, rect, data)

        return if (root == null) {
            newLeaf
        } else {
            merge(root, newLeaf)
        }
    }

    /**
     * 合并两个节点
     */
    private fun merge(node1: BVHNode<T>, node2: BVHNode<T>): BVHNode<T> {
        val parentRect = createParentRect(node1, node2)
        return InternalNode(node1.hashCode() + node2.hashCode(), this, parentRect, node1, node2)
    }

    /**
     * 从BVH树中移除指定元素
     */
    fun remove(root: BVHNode<T>?, data: T): BVHNode<T>? {
        if (root == null) {
            return null
        }

        val leaves = getAllLeaves(root)
        val filteredLeaves = leaves.filter { it.data != data }

        return if (filteredLeaves.isEmpty()) {
            null
        } else {
            buildTree(filteredLeaves)
        }
    }

    /**
     * 重建BVH树
     */
    fun rebuild(root: BVHNode<T>?): BVHNode<T>? {
        if (root == null) {
            return null
        }

        val leaves = getAllLeaves(root)
        return buildTree(leaves)
    }
}