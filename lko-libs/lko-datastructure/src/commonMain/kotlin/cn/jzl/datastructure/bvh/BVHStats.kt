package cn.jzl.datastructure.bvh

/**
 * BVH性能统计信息
 * 可用于监控和调优BVH的性能
 */
data class BVHStats(
    val nodeCount: Int,
    val leafCount: Int,
    val maxDepth: Int,
    val averageDepth: Float,
    val areaUtilization: Float
)