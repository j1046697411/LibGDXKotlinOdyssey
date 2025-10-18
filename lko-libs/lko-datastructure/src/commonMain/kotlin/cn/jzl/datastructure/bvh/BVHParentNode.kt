package cn.jzl.datastructure.bvh

sealed interface BVHParentNode<T> : BVHNode<T> {
    var left: BVHNode<T>
    var right: BVHNode<T>
    fun updateBounds()
}