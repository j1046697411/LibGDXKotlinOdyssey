package cn.jzl.datastructure.bvh
import kotlin.jvm.JvmInline

import cn.jzl.datastructure.math.geom.*
import cn.jzl.datastructure.math.vector.Point
import cn.jzl.datastructure.math.vector.Vector2

/**
 * 2D BVH 轻量适配器。
 * - 将 2D 几何类型（Point/Vector2/Rectangle）映射到底层通用 BVH 的第 0/1 维。
 * - 提供插入、删除、更新与射线/矩形查询的便捷方法。
 *
 * 设计约定与坐标系统：
 * - 维度映射：`x` 对应维度 0，`y` 对应维度 1。
 * - 矩形表示：使用左上 (x,y) 与右下 (x+width,y+height) 形式。
 * - 所有查询操作均委托给底层 BVH.searchValues 和 BVH.intersect API。
 *
 * 使用提示：
 * - 本适配器仅支持 2D 空间；如需 3D/ND 空间，请直接使用 BVH(dimensions = N) 并自行封装。
 * - 为优化性能，查询方法支持传入可复用的 result 列表以减少内存分配。
 */
@JvmInline
value class BVH2<T> internal constructor(private val bvh: BVH<T>) : Sequence<T> {

    val rectangle: Rectangle
        get() = bvh.root?.rect?.let {
            Rectangle(
                it.min(bvh, 0),
                it.min(bvh, 1),
                it.size(bvh, 0),
                it.size(bvh, 1)
            )
        } ?: Rectangle(0f, 0f, 0f, 0f)

    private fun BVHRay.set(origin: Point, direction: Vector2) {
        origin(bvh, 0, origin.x)
        origin(bvh, 1, origin.y)
        direction(bvh, 0, direction.x)
        direction(bvh, 1, direction.y)
    }

    private fun BVHRect.set(rect: Rectangle) {
        min(bvh, 0, rect.left)
        min(bvh, 1, rect.top)
        max(bvh, 0, rect.right)
        max(bvh, 1, rect.bottom)
    }

    private fun BVHRect.set(min: Point, max: Point) {
        min(bvh, 0, min.x)
        min(bvh, 1, min.y)
        max(bvh, 0, max.x)
        max(bvh, 1, max.y)
    }

    private fun BVHRect.set(minX: Float, minY: Float, maxX: Float, maxY: Float) {
        min(bvh, 0, minX)
        min(bvh, 1, minY)
        max(bvh, 0, maxX)
        max(bvh, 1, maxY)
    }

    /**
     * 批量插入元素到BVH2中。
     * @param item 要插入的元素序列
     * @param provider 为每个元素提供边界框的函数
     */
    fun bulkInsert(item: Sequence<T>, provider: T.() -> Rectangle) = bvh.bulkInsert(item) { it.set(provider()) }

    /**
     * 插入单个元素及其矩形边界框到BVH2中。
     * @param rect 元素的边界框
     * @param value 要插入的元素
     * @return 是否插入成功
     */
    fun insert(rect: Rectangle, value: T): Boolean = bvh.insert(value) { it.set(rect) }

    /**
     * 插入单个元素及其由最小点和最大点定义的边界框到BVH2中。
     * @param min 边界框的最小点（左下）
     * @param max 边界框的最大点（右上）
     * @param value 要插入的元素
     * @return 是否插入成功
     */
    fun insert(min: Point, max: Point, value: T): Boolean = bvh.insert(value) { it.set(min, max) }

    /**
     * 插入单个元素及其由坐标定义的边界框到BVH2中。
     * @param minX 边界框的最小X坐标
     * @param minY 边界框的最小Y坐标
     * @param maxX 边界框的最大X坐标
     * @param maxY 边界框的最大Y坐标
     * @param value 要插入的元素
     * @return 是否插入成功
     */
    fun insert(minX: Float, minY: Float, maxX: Float, maxY: Float, value: T): Boolean = bvh.insert(value) { it.set(minX, minY, maxX, maxY) }

    /**
     * 从BVH2中移除指定元素。
     * @param value 要移除的元素
     * @return 是否移除成功
     */
    fun remove(value: T): Boolean = bvh.remove(value)

    /**
     * 更新BVH2中指定元素的矩形边界框。
     * @param rect 新的边界框
     * @param value 要更新的元素
     * @return 是否更新成功
     */
    fun update(rect: Rectangle, value: T): Boolean = bvh.update(value) { it.set(rect) }

    /**
     * 更新BVH2中指定元素的边界框（由最小点和最大点定义）。
     * @param min 新的边界框最小点（左下）
     * @param max 新的边界框最大点（右上）
     * @param value 要更新的元素
     * @return 是否更新成功
     */
    fun update(min: Point, max: Point, value: T): Boolean = bvh.update(value) { it.set(min, max) }

    /**
     * 更新BVH2中指定元素的边界框（由坐标定义）。
     * @param minX 新的边界框最小X坐标
     * @param minY 新的边界框最小Y坐标
     * @param maxX 新的边界框最大X坐标
     * @param maxY 新的边界框最大Y坐标
     * @param value 要更新的元素
     * @return 是否更新成功
     */
    fun update(minX: Float, minY: Float, maxX: Float, maxY: Float, value: T): Boolean = bvh.update(value) { it.set(minX, minY, maxX, maxY) }

    /**
     * 查找与指定射线相交的所有元素。
     * @param origin 射线起点
     * @param direction 射线方向（无需归一化）
     * @param result 用于存储结果的可复用列表
     * @return 与射线相交的元素列表
     */
    fun searchValuesByRay(origin: Point, direction: Vector2, result: MutableList<T>): List<T> {
        val hits = bvh.intersect { it.set(origin, direction) }
        result.clear()
        result.addAll(hits.map { it.data })
        return result
    }

    /**
     * 查找与指定矩形边界框相交的所有元素。
     * @param rect 查询边界框
     * @param result 用于存储结果的可复用列表
     * @return 与查询边界框相交的元素列表
     */
    fun searchValuesByRect(rect: Rectangle, result: MutableList<T>): List<T> {
        result.clear()
        result.addAll(bvh.searchValues { it.set(rect) })
        return result
    }

    /**
     * 查找与指定边界框（由最小点和最大点定义）相交的所有元素。
     * @param min 查询边界框的最小点（左下）
     * @param max 查询边界框的最大点（右上）
     * @param result 用于存储结果的可复用列表
     * @return 与查询边界框相交的元素列表
     */
    fun searchValuesByRect(min: Point, max: Point, result: MutableList<T>): List<T> {
        result.clear()
        result.addAll(bvh.searchValues { it.set(min, max) })
        return result
    }

    /**
     * 查找与指定边界框（由坐标定义）相交的所有元素。
     * @param minX 查询边界框的最小X坐标
     * @param minY 查询边界框的最小Y坐标
     * @param maxX 查询边界框的最大X坐标
     * @param maxY 查询边界框的最大Y坐标
     * @param result 用于存储结果的可复用列表
     * @return 与查询边界框相交的元素列表
     */
    fun searchValuesByRect(minX: Float, minY: Float, maxX: Float, maxY: Float, result: MutableList<T>): List<T> {
        result.clear()
        result.addAll(bvh.searchValues { it.set(minX, minY, maxX, maxY) })
        return result
    }

    override fun iterator(): Iterator<T> = bvh.iterator()

    companion object {
        operator fun <T> invoke(): BVH2<T> = BVH2(BVH(2))
    }
}