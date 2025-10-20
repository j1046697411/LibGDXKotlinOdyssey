package cn.jzl.datastructure.bvh
import kotlin.jvm.JvmInline

import cn.jzl.datastructure.math.geom.*
import cn.jzl.datastructure.math.vector.*

/**
 * 3D BVH 轻量适配器。
 * - 将 3D 几何类型（Vector3）映射到底层通用 BVH 的第 0/1/2 维。
 * - 提供插入、删除、更新与射线/立方体查询的便捷方法。
 *
 * 设计约定与坐标系统：
 * - 维度映射：x 对应维度 0，y 对应维度 1，z 对应维度 2。
 * - 立方体表示：使用最小点（x_min,y_min,z_min）与最大点（x_max,y_max,z_max）形式，均使用Vector3表示。
 * - 所有查询操作均委托给底层 BVH.searchValues 和 BVH.intersect API。
 *
 * 使用提示：
 * - 本适配器专为 3D 空间设计，提供完整的三维包围盒层次结构支持。
 * - 为优化性能，查询方法支持传入可复用的 result 列表以减少内存分配。
 */
@JvmInline
value class BVH3<T>(private val bvh: BVH<T>) {

    private fun BVHRay.set(origin: Vector3, direction: Vector3) {
        origin(bvh, 0, origin.x)
        origin(bvh, 1, origin.y)
        origin(bvh, 2, origin.z)
        direction(bvh, 0, direction.x)
        direction(bvh, 1, direction.y)
        direction(bvh, 2, direction.z)
    }

    private fun BVHRect.set(min: Vector3, max: Vector3) {
        min(bvh, 0, min.x)
        min(bvh, 1, min.y)
        min(bvh, 2, min.z)
        max(bvh, 0, max.x)
        max(bvh, 1, max.y)
        max(bvh, 2, max.z)
    }

    private fun BVHRect.set(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float) {
        min(bvh, 0, minX)
        min(bvh, 1, minY)
        min(bvh, 2, minZ)
        max(bvh, 0, maxX)
        max(bvh, 1, maxY)
        max(bvh, 2, maxZ)
    }

    /**
     * 批量插入多个对象。
     * @param item 对象序列。
     * @param provider 回调负责为每个对象给出其 3D 外接立方体的最小和最大点。
     * @return 成功插入的数量。
     */
    fun bulkInsert(item: Sequence<T>, provider: T.() -> Pair<Vector3, Vector3>) = bvh.bulkInsert(item) { val (min, max) = provider(); it.set(min, max) }

    /**
     * 使用最小/最大点插入对象。
     * @param min 最小点。
     * @param max 最大点。
     * @param value 待插入对象。
     * @return 若对象尚未存在于 BVH 中返回 true，否则返回 false。
     */
    fun insert(min: Vector3, max: Vector3, value: T): Boolean = bvh.insert(value) { it.set(min, max) }

    /**
     * 使用标量边界插入对象。
     * @param minX 最小 X 坐标。
     * @param minY 最小 Y 坐标。
     * @param minZ 最小 Z 坐标。
     * @param maxX 最大 X 坐标。
     * @param maxY 最大 Y 坐标。
     * @param maxZ 最大 Z 坐标。
     * @param value 待插入对象。
     * @return 若对象尚未存在于 BVH 中返回 true，否则返回 false。
     */
    fun insert(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float, value: T): Boolean = 
        bvh.insert(value) { it.set(minX, minY, minZ, maxX, maxY, maxZ) }

    /**
     * 从 BVH 中移除对象。
     * @param value 待移除对象。
     * @return 如果对象存在并成功移除返回 true，否则返回 false。
     */
    fun remove(value: T): Boolean = bvh.remove(value)

    /**
     * 使用最小/最大点更新对象的边界。
     * @param min 新的最小点。
     * @param max 新的最大点。
     * @param value 待更新对象。
     * @return 如果对象存在并成功更新返回 true，否则返回 false。
     */
    fun update(min: Vector3, max: Vector3, value: T): Boolean = bvh.update(value) { it.set(min, max) }

    /**
     * 使用标量边界更新对象的边界。
     * @param minX 新的最小 X 坐标。
     * @param minY 新的最小 Y 坐标。
     * @param minZ 新的最小 Z 坐标。
     * @param maxX 新的最大 X 坐标。
     * @param maxY 新的最大 Y 坐标。
     * @param maxZ 新的最大 Z 坐标。
     * @param value 待更新对象。
     * @return 如果对象存在并成功更新返回 true，否则返回 false。
     */
    fun update(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float, value: T): Boolean = 
        bvh.update(value) { it.set(minX, minY, minZ, maxX, maxY, maxZ) }

    /**
     * 以射线检索所有与之相交的对象。
     * @param origin 射线原点。
     * @param direction 射线方向（无需单位化）。
     * @param result 结果容器（可复用传入的列表以减少分配）。
     * @return 与射线相交的对象列表（同 result）。
     */
    fun searchValuesByRay(origin: Vector3, direction: Vector3, result: MutableList<T>): List<T> {
        val hits = bvh.intersect { it.set(origin, direction) }
        result.clear()
        result.addAll(hits.map { it.data })
        return result
    }

    /**
     * 在由最小/最大点定义的立方体内检索对象。
     * @param min 最小点。
     * @param max 最大点。
     * @param result 结果容器（可复用传入的列表以减少分配）。
     * @return 与立方体相交的对象列表（同 result）。
     */
    fun searchValuesByBox(min: Vector3, max: Vector3, result: MutableList<T>): List<T> {
        result.clear()
        result.addAll(bvh.searchValues { it.set(min, max) })
        return result
    }

    /**
     * 在由标量边界定义的立方体内检索对象。
     * @param minX 最小 X 坐标。
     * @param minY 最小 Y 坐标。
     * @param minZ 最小 Z 坐标。
     * @param maxX 最大 X 坐标。
     * @param maxY 最大 Y 坐标。
     * @param maxZ 最大 Z 坐标。
     * @param result 结果容器（可复用传入的列表以减少分配）。
     * @return 与立方体相交的对象列表（同 result）。
     */
    fun searchValuesByBox(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float, result: MutableList<T>): List<T> {
        result.clear()
        result.addAll(bvh.searchValues { it.set(minX, minY, minZ, maxX, maxY, maxZ) })
        return result
    }
}