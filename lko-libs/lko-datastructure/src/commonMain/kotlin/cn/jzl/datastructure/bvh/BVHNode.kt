package cn.jzl.datastructure.bvh

/**
 * BVH节点接口
 */
// 完整接口定义
sealed interface BVHNode<T> {
    val id: Int
    val bvh: BVH<T>
    val rect: BVHRect
    var parent: BVHParentNode<T>?
    fun insertNode(leafNode: LeafNode<T>): BVHNode<T>
}