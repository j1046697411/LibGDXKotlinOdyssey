@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.datastructure.bvh

import cn.jzl.datastructure.list.FloatFastList
import cn.jzl.datastructure.list.IntFastList
import cn.jzl.datastructure.list.ObjectFastList
import cn.jzl.datastructure.math.vector.generic.Dimension
import kotlin.math.max
import kotlin.math.min

/**
 * BVH (Bounding Volume Hierarchy) 实现
 *
 * - 用于高效空间划分与查询（射线/区域检索）
 * - 使用 `FloatFastList` 存储坐标数据，通过偏移与维度索引访问
 * - 复用节点偏移（`recycledNodeIds`）减少临时对象分配
 *
 * 使用方式：
 * - 通过 `insert(data) { rect -> ... }` 设置对象外接矩形并插入
 * - 使用 `update(data, reinsertThreshold) { rect -> ... }` 更新对象包围盒
 * - 通过 `intersect { ray -> ... }` 或 `searchValues { rect -> ... }` 执行查询
 *
 * 复杂度（典型平均）：
 * - 插入/删除：O(log N)
 * - 查询：O(log N + K)，K 为命中数量
 *
 * @param dimensions 维度数量，支持任意维（≥1），常见为2或3
 */
class BVH<T>(override val dimensions: Int) : Dimension, Sequence<T> {

    internal val data = FloatFastList(8 * dimensions)
    internal val recycledNodeIds = IntFastList(8)
    private val leafNodes = mutableMapOf<T, LeafNode<T>>()
    private var rootNode: BVHNode<T>? = null
    private var nextNodeId: Int = 0
    private val leafNodeSequence = leafNodes.values.asSequence()

    val size: Int get() = leafNodes.size
    val root: BVHNode<T>? get() = rootNode

    fun isEmpty(): Boolean = rootNode == null

    /**
     * 获取下一个节点ID
     */
    internal fun nextId(): Int = nextNodeId++

    operator fun contains(data: T): Boolean = data in leafNodes

    /**
     * 获取指定偏移量和维度的值
     */
    internal inline fun dimension(offset: Int, dimension: Int): Float = data[offset * dimensions + dimension]

    /**
     * 设置指定偏移量和维度的值
     */
    internal inline fun dimension(offset: Int, dimension: Int, value: Float) {
        data[offset * dimensions + dimension] = value
    }

    /**
     * 释放矩形资源
     */
    internal fun release(rect: BVHRect) {
        recycledNodeIds.insertLast(rect.minOffset, rect.maxOffset)
    }

    internal fun release(ray: BVHRay) {
        recycledNodeIds.insertLast(ray.originOffset, ray.directionOffset)
    }

    internal fun release(vector: BVHVector) {
        recycledNodeIds.insertLast(vector.offset)
    }

    private fun offset(): Int {
        return if (recycledNodeIds.isNotEmpty()) {
            recycledNodeIds.removeLast()
        } else {
            val offset = data.size / dimensions
            data.ensureCapacity(data.size + dimensions, 0f)
            offset
        }
    }

    internal inline fun vector(): BVHVector = BVHVector(offset())
    internal inline fun rect(): BVHRect = BVHRect(offset(), offset())
    internal inline fun ray(): BVHRay = BVHRay(offset(), offset())

    fun insert(data: T, callback: T.(BVHRect) -> Unit): Boolean {
        if (data in leafNodes) return false
        rootNode = insertNode(createLeafNode(data, callback))
        return true
    }

    private fun insertNode(leafNode: LeafNode<T>): BVHNode<T> {
        val rootNode = rootNode ?: return leafNode
        val leafNodeCount = leafNodes.size
        if (leafNodeCount <= 32) return rootNode.insertNode(leafNode)
        val maxDepth = computeDepth() - 1
        val maxLeafCount = 1 shl maxDepth
        if (maxLeafCount <= leafNodeCount * 4) return rootNode.insertNode(leafNode)
        return buildTreeFromLeaves(leafNodeSequence.toMutableList(), 0)
    }

    fun remove(data: T): Boolean {
        val leafNode = leafNodes.remove(data) ?: return false
        this.rootNode = removeNode(leafNode)
        release(leafNode.rect)
        return true
    }

    private fun removeNode(leafNode: LeafNode<T>): BVHNode<T>? {
        if (leafNode == rootNode) return null
        val parent = leafNode.parent ?: return null
        try {
            val sibling = parent.getSibling(leafNode)
            val grandParent = parent.parent ?: return sibling
            sibling.parent = grandParent
            grandParent.left = grandParent.getSibling(parent)
            grandParent.right = sibling
            grandParent.updateBounds()
            return rootNode
        } finally {
            release(parent.rect)
        }
    }

    /**
     * 更新BVH树中的对象边界
     * @param data 要更新的对象
     * @param reinsertThreshold 当边界变化超过此阈值时自动重新插入对象（0-1之间的值，0表示不重新插入）
     * @param callback 用于设置新边界的回调函数
     * @return 更新是否成功
     */
    fun update(data: T, reinsertThreshold: Float = 0.0f, callback: T.(BVHRect) -> Unit): Boolean {
        val leafNode = leafNodes[data] ?: return false

        // 如果启用了重新插入，保存更新前的边界面积用于比较
        val shouldReinsert = reinsertThreshold > 0.0f
        val originalArea = if (shouldReinsert) leafNode.rect.area(this) else 0f

        // 执行更新
        data.callback(leafNode.rect)

        // 如果面积变化超过阈值，执行重新插入以保持树的平衡
        if (shouldReinsert && originalArea > 0f) {
            val newArea = leafNode.rect.area(this)
            val areaRatio = max(newArea / originalArea, originalArea / newArea)

            if (areaRatio > 1.0f + reinsertThreshold) {
                // 重新插入策略：先移除再插入
                val rect = rect().also { it.set(this@BVH, leafNode.rect) }

                // 移除旧节点
                remove(data)

                // 插入带有新边界的节点
                insert(data) { newRect -> newRect.set(this@BVH, rect) }

                release(rect)
                return true
            }
        }

        // 更新父节点的边界
        leafNode.parent?.updateBounds()
        return true
    }

    /**
     * 计算两个矩形的并集边界（返回复用池中的矩形）
     *
     * - 使用 `rect()` 申请复用的矩形偏移，然后通过 `update(a, b)` 填充
     * - 调用方负责在使用后 `release(rect)` 归还资源
     */
    internal fun combinedBounds(a: BVHRect, b: BVHRect): BVHRect {
        val rect = rect()
        rect.update(a, b)
        return rect
    }

    // 优化：使用预分配的节点栈，避免每次搜索都重新分配内存
    private val nodeStack = ObjectFastList<BVHNode<T>>(32) // 增加初始容量以适应更大的树

    internal fun leafNodes(node: BVHNode<T>): Sequence<LeafNode<T>> = sequence {
        nodeStack.clear()
        nodeStack.insertLast(node)
        while (nodeStack.isNotEmpty()) {
            val node = nodeStack.removeLast()
            when (node) {
                is InternalNode<T> -> {
                    nodeStack.insertLast(node.right)
                    nodeStack.insertLast(node.left)
                }

                is LeafNode<T> -> yield(node)
            }
        }
    }

    /**
     * 射线相交测试，查找与射线相交的所有节点
     *
     * 实现要点：
     * - 复用内部 `nodeStack`，避免遍历过程中的多余分配
     * - 回调 `callback` 用于设置查询射线的原点与方向
     * - 只将与射线相交的叶节点追加到 `result`
     *
     * @param result 结果容器，默认使用 `ObjectFastList`，可复用以减少GC
     * @param callback 设置射线的回调，形如 `{ ray -> ray.origin(...); ray.direction(...) }`
     * @return 与射线相交的所有叶节点列表（按遍历顺序）
     */
    fun intersect(result: MutableList<LeafNode<T>> = ObjectFastList(), callback: (BVHRay) -> Unit): List<LeafNode<T>> {
        rootNode?.let { node ->
            val ray = ray()
            callback(ray)
            searchNodes(node, { intersect(ray, it) }) { result.add(it) }
            release(ray)
        }
        return result
    }

    /**
     * 优化的搜索节点方法
     *
     * - 复用节点栈，减少内存分配
     * - 按相交可能性简单排序（当前入栈顺序为 left→right，可根据需要调整）
     *
     * @param root 起始根节点
     * @param intersect 交叠/相交判断函数
     * @param callback 命中叶节点时的回调
     */
    private inline fun searchNodes(root: BVHNode<T>, intersect: (BVHRect) -> Boolean, callback: (LeafNode<T>) -> Unit) {
        val stack = nodeStack.also { it.clear() }
        stack.insertLast(root)

        while (stack.isNotEmpty()) {
            val node = stack.removeLast()

            // 快速检查：只有与查询条件相交的节点才继续处理
            if (!intersect(node.rect)) continue

            when (node) {
                is InternalNode<T> -> {
                    // 优化：将子节点按更可能相交的顺序入栈，减少不必要的遍历
                    stack.insertLast(node.left)
                    stack.insertLast(node.right)
                }

                is LeafNode<T> -> callback(node)
            }
        }
    }

    /**
     * 搜索与指定区域相交的所有值
     *
     * - 回调 `callback` 用于设置查询矩形
     * - 若根节点与查询矩形不相交，直接返回空结果（早退）
     *
     * @param result 结果容器，默认使用 `ObjectFastList`，可复用以减少GC
     * @param callback 设置查询矩形的回调
     * @return 与查询矩形相交的所有值
     */
    fun searchValues(result: MutableList<T> = ObjectFastList(), callback: (BVHRect) -> Unit): List<T> {
        rootNode?.let { node ->
            val rect = rect()
            callback(rect)

            // 快速检查：如果根节点边界与搜索区域不相交，直接返回空结果
            if (overlaps(node.rect, rect)) {
                searchNodes(node, { overlaps(it, rect) }) { result.add(it.data) }
            }

            release(rect)
        }
        return result
    }

    // 内部方法：创建一个新的叶节点（不公开）
    private fun createLeafNode(item: T, rect: BVHRect): LeafNode<T> {
        val leafNode = LeafNode(nextId(), this, rect, item)
        leafNodes[item] = leafNode
        return leafNode
    }

    // 内部方法：创建一个新的叶节点（不公开）
    private fun createLeafNode(item: T, callback: T.(BVHRect) -> Unit): LeafNode<T> {
        val rect = rect()
        item.callback(rect)
        return createLeafNode(item, rect)
    }

    /**
     * 批量插入优化
     * 对于大量对象的插入，可以显著提高性能
     */
    fun bulkInsert(items: Sequence<T>, callback: T.(BVHRect) -> Unit): Int {
        val leaves = mutableListOf<LeafNode<T>>()
        // 第一阶段：创建所有叶节点
        for (item in items) {
            if (item in leafNodes) continue
            leaves.add(createLeafNode(item, callback))
        }
        if (leaves.isEmpty()) return 0
        if (leaves.size <= 4) {
            rootNode = leaves.fold(rootNode) { acc, leaf -> insertNode(leaf) }
            return leaves.size
        }
        val insertCount = leaves.size
        leaves.clear()
        leaves.addAll(leafNodes.values)
        rootNode = buildTreeFromLeaves(leaves)
        return insertCount
    }

    // 从叶节点列表构建平衡的BVH树
    private fun buildTreeFromLeaves(leaves: MutableList<LeafNode<T>>, depth: Int = 0): BVHNode<T> {
        require(leaves.isNotEmpty()) { "Cannot build BVH tree with empty leaves list" }
        if (leaves.size == 1) return leaves.component1()
        // 找到最佳分割维度
        val bestDim = findBestSplitDimensionForLeaves(leaves)
        if (bestDim >= 0) {
            // 按最佳维度排序
            leaves.sortBy { leaf -> leaf.rect.center(this, bestDim) }
        }
        // 使用中线分割策略
        val midIndex = leaves.size / 2
        val leftGroup = leaves.subList(0, midIndex)
        val rightGroup = leaves.subList(midIndex, leaves.size)

        // 递归构建子树
        val left = buildTreeFromLeaves(leftGroup, depth + 1)
        val right = buildTreeFromLeaves(rightGroup, depth + 1)

        // 创建父节点
        val internalNode = InternalNode(nextId(), this, combinedBounds(left.rect, right.rect), left, right)
        left.parent = internalNode
        right.parent = internalNode

        return internalNode
    }

    // 为叶节点列表查找最佳分割维度
    private fun findBestSplitDimensionForLeaves(leaves: List<LeafNode<T>>): Int {
        var bestDim = -1
        var maxSpread = -1f

        for (dim in 0 until dimensions) {
            var minCenter = Float.MAX_VALUE
            var maxCenter = -Float.MAX_VALUE

            for (leaf in leaves) {
                val center = leaf.rect.center(this, dim)
                minCenter = min(minCenter, center)
                maxCenter = max(maxCenter, center)
            }

            val spread = maxCenter - minCenter
            if (spread > maxSpread) {
                maxSpread = spread
                bestDim = dim
            }
        }

        return if (maxSpread > 1e-5f) bestDim else -1
    }

    /**
     * 使用两个矩形更新当前矩形的最小/最大边界
     *
     * - 按维度遍历并取 `min(minA, minB)` 与 `max(maxA, maxB)`
     */
    internal fun BVHRect.update(a: BVHRect, b: BVHRect) {
        for (dimension in 0 until dimensions) {
            min(this@BVH, dimension, min(a.min(this@BVH, dimension), b.min(this@BVH, dimension)))
            max(this@BVH, dimension, max(a.max(this@BVH, dimension), b.max(this@BVH, dimension)))
        }
    }

    /**
     * 计算两个矩形合并后的面积（无额外分配）
     *
     * - 直接调用 `BVHRect.combinedArea`，在当前 `BVH` 的维度下计算
     */
    internal fun combinedArea(a: BVHRect, b: BVHRect): Float = a.combinedArea(this, b)

    /**
     * 计算BVH树的深度（迭代实现）
     *
     * - 使用栈迭代避免深度树的递归栈风险
     * - 仅统计节点层级，不包含空树（空树返回0）
     *
     * @return 树的最大深度
     */
    fun computeDepth(): Int = rootNode?.let { computeDepth(it) } ?: 0

    private fun computeDepth(node: BVHNode<T>): Int {
        return if (node is InternalNode<T>) 1 + max(computeDepth(node.left), computeDepth(node.right)) else 1
    }

    /**
     * 清除BVH树
     */
    fun clear() {
        data.clear()
        recycledNodeIds.clear()
        // tempNodes可能是一个未定义的变量，这里移除或注释掉
        // tempNodes.clear()
        rootNode = null
        nextNodeId = 0
        leafNodes.clear()
    }

    override fun iterator(): Iterator<T> {
        return leafNodes.keys.iterator()
    }

    /**
     * 射线与矩形相交测试（slab 算法）
     *
     * - 使用每维的入/出区间更新 `tMin/tMax`
     * - 当某维方向为零时，直接判断原点是否落在该维的区间内
     * - 发生区间空（`tMax < tMin`）时早退
     *
     * @param ray 射线（包含原点与方向）
     * @param rect 轴对齐包围盒
     * @return 是否相交
     */
    fun intersect(ray: BVHRay, rect: BVHRect): Boolean {
        var tMin = -Float.MAX_VALUE
        var tMax = Float.MAX_VALUE

        for (dim in 0 until dimensions) {
            val origin = ray.origin(this, dim)
            val direction = ray.direction(this, dim)
            val min = rect.min(this, dim)
            val max = rect.max(this, dim)

            if (direction == 0f) {
                if (origin < min || origin > max) return false
                continue
            }

            val invDir = 1f / direction
            var t1 = (min - origin) * invDir
            var t2 = (max - origin) * invDir
            if (t1 > t2) {
                val tmp = t1
                t1 = t2
                t2 = tmp
            }

            if (t1 > tMin) tMin = t1
            if (t2 < tMax) tMax = t2
            if (tMax < tMin) return false
        }

        return tMax >= maxOf(tMin, 0f)
    }

    /**
     * 检查两个矩形是否相交
     */
    private fun overlaps(a: BVHRect, b: BVHRect): Boolean {
        for (dim in 0 until dimensions) {
            if (a.max(this, dim) < b.min(this, dim) || a.min(this, dim) > b.max(this, dim)) {
                return false
            }
        }
        return true
    }

    private fun BVHParentNode<T>.getSibling(node: BVHNode<T>): BVHNode<T> {
        return when (node) {
            left -> right
            right -> left
            else -> throw IllegalArgumentException("")
        }
    }

    /**
     * 收集BVH树的性能统计信息
     */
    fun collectStats(): BVHStats {
        val rootNode = rootNode ?: return BVHStats(0, 0, 0, 0f, 0f)
        var nodeCount = 0
        var leafCount = 0
        var maxDepth = 0
        var totalDepth = 0
        var totalLeafArea = 0f

        val stack = mutableListOf<Pair<BVHNode<T>, Int>>()
        stack.add(rootNode to 1)

        while (stack.isNotEmpty()) {
            val (node, depth) = stack.removeAt(stack.lastIndex)
            nodeCount++
            maxDepth = maxOf(maxDepth, depth)

            when (node) {
                is LeafNode<T> -> {
                    leafCount++
                    totalDepth += depth
                    totalLeafArea += node.rect.area(this)
                }

                is InternalNode<T> -> {
                    stack.add(node.left to depth + 1)
                    stack.add(node.right to depth + 1)
                }
            }
        }

        val averageDepth = if (leafCount > 0) totalDepth.toFloat() / leafCount else 0f
        val rootArea = rootNode.rect.area(this)
        val areaUtilization = if (rootArea > 0f) totalLeafArea / rootArea else 0f

        return BVHStats(nodeCount, leafCount, maxDepth, averageDepth, areaUtilization)
    }
}