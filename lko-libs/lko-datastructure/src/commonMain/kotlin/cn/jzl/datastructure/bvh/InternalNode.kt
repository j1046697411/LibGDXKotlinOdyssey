package cn.jzl.datastructure.bvh

/**
 * BVH 内部节点（父节点）实现。
 * - 插入策略：选择导致面积增量较小的子树进行插入，并维护父子关系。
 * - 边界更新：将 `rect` 更新为包含左右子节点的最小包围矩形。
 */
data class InternalNode<T>(
    override val id: Int,
    override val bvh: BVH<T>,
    override val rect: BVHRect,
    override var left: BVHNode<T>,
    override var right: BVHNode<T>,
    override var parent: BVHParentNode<T>? = null
) : BVHNode<T>, BVHParentNode<T> {
    override fun insertNode(leafNode: LeafNode<T>): BVHNode<T> {
        // 选择面积增量较小的子节点进行插入
        val leftArea = this.left.rect.combinedArea(this.bvh, leafNode.rect)
        val rightArea = this.right.rect.combinedArea(this.bvh, leafNode.rect)

        if (leftArea < rightArea) {
            this.left = this.left.insertNode(leafNode)
            this.left.parent = this
        } else {
            this.right = this.right.insertNode(leafNode)
            this.right.parent = this
        }

        // 更新边界
        this.updateBounds()
        return this
    }

    override fun updateBounds() {
        // 重置矩形边界为最大/最小值
        for (dim in 0 until this.bvh.dimensions) {
            this.rect.min(this.bvh, dim, Float.MAX_VALUE)
            this.rect.max(this.bvh, dim, -Float.MAX_VALUE)
        }

        // 更新为包含左右子节点的边界
        for (dim in 0 until this.bvh.dimensions) {
            // 取左右子节点的最小边界中的较小值
            this.rect.min(
                this.bvh, dim, minOf(
                    this.left.rect.min(this.bvh, dim),
                    this.right.rect.min(this.bvh, dim)
                )
            )
            // 取左右子节点的最大边界中的较大值
            this.rect.max(
                this.bvh, dim, maxOf(
                    this.left.rect.max(this.bvh, dim),
                    this.right.rect.max(this.bvh, dim)
                )
            )
        }
    }
}