package cn.jzl.datastructure.array2

import cn.jzl.datastructure.bvh.BVH2
import cn.jzl.datastructure.list.ObjectFastList
import cn.jzl.datastructure.math.geom.RectangleInt
import cn.jzl.datastructure.math.geom.SizeInt
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * 稀疏分块堆叠二维数组实现
 *
 * 特点:
 * - 采用稀疏存储策略，只在实际需要时创建数据块
 * - 使用BVH(Bounding Volume Hierarchy)高效管理数据块空间分布
 * - 支持多层级数据存储，每层可独立访问
 * - 适合处理大规模、稀疏分布的数据
 *
 * @param T 存储的数据类型
 * @param grid 每个数据块的大小，默认为16x16
 * @param default 默认值，用于未初始化的位置
 * @param factory 创建底层数组的工厂函数
 */
class SparseChunkedStackedArray2<T>(
    private val grid: SizeInt = SizeInt.Companion(16, 16),
    override val default: T,
    private val factory: (T) -> IArray2<T>
) : IStackedArray2<T> {

    // BVH结构，用于高效管理和查询数据块
    private val bvh = BVH2.Companion<StackedArray2<T>>()
    // 复用的查询结果列表，减少内存分配
    private val result = ObjectFastList<StackedArray2<T>>()

    // 当前BVH覆盖的矩形范围
    private var bvhRange = RectangleInt(0, 0, 0, 0)
    // 缓存最后访问的数据块，优化连续访问性能
    private var lastChunk: StackedArray2<T>? = null

    /**
     * 数组覆盖的矩形范围
     * 当数据发生变化时会自动更新
     */
    override val range: RectangleInt
        get() {
            if (dirtyData) {
                bvhRange = RectangleInt(
                    bvh.rectangle.x.roundToInt(),
                    bvh.rectangle.y.roundToInt(),
                    bvh.rectangle.width.roundToInt(),
                    bvh.rectangle.height.roundToInt()
                )
                dirtyData = false
            }
            return bvhRange
        }
    // 标记数据是否发生变化，用于延迟更新range
    private var dirtyData: Boolean = false

    /**
     * 内容版本号，数据变化时递增
     */
    override var contentVersion: Int = 0
        private set

    /**
     * 当前数组中的最大层级数
     */
    override var maxLevel: Int = 0
        private set

    /**
     * 获取指定层级的可观察二维数组视图
     *
     * @param level 层级索引
     * @return 对应层级的可观察二维数组
     */
    override fun layer(level: Int): ObservableArray2<T> {
        return object : IArray2<T> {
            private val indexSequence = (0 until (width * height)).asSequence()
            override val width: Int get() = range.width
            override val height: Int get() = range.height
            override fun get(x: Int, y: Int): T = this@SparseChunkedStackedArray2[x, y, level]
            override fun set(x: Int, y: Int, value: T) {
                this@SparseChunkedStackedArray2[x, y, level] = value
            }

            override fun set(rect: RectangleInt, value: T) {
                for (x in rect.x until rect.x + rect.width) {
                    for (y in rect.y until rect.y + rect.height) {
                        this@SparseChunkedStackedArray2[x, y, level] = value
                    }
                }
            }

            override fun iterator(): Iterator<T> {
                return indexSequence.map { this[it % width, it / width] }.iterator()
            }
        }.observe {
            contentVersion++
            dirtyData = true
        }
    }

    /**
     * 获取指定位置的堆叠层级数
     *
     * @param x X坐标
     * @param y Y坐标
     * @return 该位置的堆叠层级数，如果位置不存在数据则返回0
     */
    override fun getStackLevel(x: Int, y: Int): Int {
        return getChunkOrNull(x, y)?.getStackLevel(x, y) ?: 0
    }

    /**
     * 获取指定坐标所在的数据块，如果不存在则返回null
     *
     * @param x X坐标
     * @param y Y坐标
     * @return 包含该坐标的数据块，如果不存在则返回null
     */
    private fun getChunkOrNull(x: Int, y: Int): StackedArray2<T>? {
        // 检查缓存的最后访问块
        if (lastChunk?.inside(x, y) == true) return lastChunk
        result.clear()
        // 使用BVH搜索包含该点的数据块
        bvh.searchValuesByRect(
            x.toFloat(),
            y.toFloat(),
            x + 1f,
            y + 1f,
            result
        )
        return result.firstOrNull { it.inside(x, y) }?.also { lastChunk = it }
    }

    /**
     * 获取指定坐标所在的数据块，如果不存在则创建新的数据块
     *
     * @param x X坐标
     * @param y Y坐标
     * @return 包含该坐标的数据块
     */
    private fun getChunk(x: Int, y: Int): StackedArray2<T> {
        return getChunkOrNull(x, y) ?: run {
            // 计算坐标所在块的起始位置
            val x = x / grid.width * grid.width
            val y = y / grid.height * grid.height
            // 创建新数据块并插入BVH
            val chunk = StackedArray2(RectangleInt(x, y, grid.width, grid.height), default, factory)
            bvh.insert(x.toFloat(), y.toFloat(), (x + grid.width).toFloat(), (y + grid.height).toFloat(), chunk)
            chunk
        }
    }

    /**
     * 获取指定位置和层级的值
     *
     * @param x X坐标
     * @param y Y坐标
     * @param level 层级索引
     * @return 指定位置和层级的值，如果不存在则返回默认值
     */
    override fun get(x: Int, y: Int, level: Int): T {
        return getChunkOrNull(x, y)?.get(x, y, level) ?: default
    }

    /**
     * 设置指定位置和层级的值
     *
     * @param x X坐标
     * @param y Y坐标
     * @param level 层级索引
     * @param value 要设置的值
     */
    override fun set(x: Int, y: Int, level: Int, value: T) {
        dirtyData = true
        getChunk(x, y)[x, y, level] = value
        // 更新最大层级
        this.maxLevel = max(this.maxLevel, level + 1)
        contentVersion++
    }
}