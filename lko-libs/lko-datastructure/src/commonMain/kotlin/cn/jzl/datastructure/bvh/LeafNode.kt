package cn.jzl.datastructure.bvh

// 实现类
/**
 * BVH 叶子节点。
 * - 保存实际数据项 `data` 与其包围盒 `rect`；参与树形结构的插入与边界更新。
 * - `insertNode` 时与新叶子组合为一个 `InternalNode` 并维护父子关系。
 */
data class LeafNode<T>(
    override val id: Int,
    override val bvh: BVH<T>,
    override val rect: BVHRect,
    val data: T,
    override var parent: BVHParentNode<T>? = null
) : BVHNode<T> {
    override fun insertNode(leafNode: LeafNode<T>): BVHNode<T> {
        // 创建一个新的内部节点，将当前节点和新的叶子节点作为子节点
        val internalNode = InternalNode(
            id = bvh.nextId(), // 使用方法获取下一个ID
            bvh = this.bvh,
            rect = bvh.rect(), // 使用BVH的rect方法创建矩形
            left = this,
            right = leafNode,
            parent = this.parent
        )
        // 维护父子关系
        this.parent = internalNode
        leafNode.parent = internalNode
        // 更新边界
        internalNode.updateBounds()
        return internalNode
    }
}