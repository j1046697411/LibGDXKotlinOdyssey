package cn.jzl.datastructure.bvh

/**
 * BVH 节点类型（密封接口）。
 * - 统一抽象：包含节点标识、所属 BVH、包围矩形与父节点引用。
 * - 行为：`insertNode(leafNode)` 将新叶子合并到当前节点，返回新的父节点（必要时创建内部节点）。
 */
// 完整接口定义
sealed interface BVHNode<T> {
    val id: Int
    val bvh: BVH<T>
    val rect: BVHRect
    var parent: BVHParentNode<T>?
    fun insertNode(leafNode: LeafNode<T>): BVHNode<T>
}