package cn.jzl.datastructure.bvh

/**
 * BVH 父节点（内部节点）接口。
 * - 维护左右子节点引用与父子关系。
 * - `updateBounds()` 根据左右子节点更新自身包围矩形。
 */
sealed interface BVHParentNode<T> : BVHNode<T> {
    var left: BVHNode<T>
    var right: BVHNode<T>
    fun updateBounds()
}