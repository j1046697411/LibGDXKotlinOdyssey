package cn.jzl.datastructure.array2

import cn.jzl.datastructure.math.geom.RectangleInt
import kotlinx.atomicfu.atomic

inline val IArray2<*>.size: Int get() = width * height

fun IArray2<*>.index(x: Int, y: Int): Int = x + y * width

fun IArray2<*>.checkArraySize(size: Int) {
    check(size >= width * height) { "size < width * height" }
}

fun IArray2<*>.inside(x: Int, y: Int): Boolean = 0 <= x && x < width && 0 <= y && y < height
fun IArray2<*>.checkIndex(x: Int, y: Int) {
    check(inside(x, y)) { "index out of bounds: ($x, $y)" }
}

fun IArray2<*>.checkRange(rect: RectangleInt) {
    check(inside(rect.x, rect.y) && inside(rect.x + rect.width - 1, rect.y + rect.height - 1)) { "range out of bounds: ($rect)" }
}

/**
 * 二维数组接口（宽高与索引访问）。
 * - `width`/`height` 为数组维度；`index(x,y)` 映射为行主序索引。
 * - `get(x,y)`/`set(x,y,value)` 元素读写；`set(rect,value)` 对区域批量设置。
 * - `contains(element)` 判定是否包含指定元素。
 */
interface IArray2<T> : Sequence<T> {
    val width: Int

    val height: Int

    operator fun get(x: Int, y: Int): T

    operator fun set(x: Int, y: Int, value: T)

    operator fun set(rect: RectangleInt, value: T)
}

interface IntIArray2 : IArray2<Int>
interface LongIArray2 : IArray2<Long>
interface FloatIArray2 : IArray2<Float>
interface DoubleIArray2 : IArray2<Double>
interface CharIArray2 : IArray2<Char>
interface ShortIArray2 : IArray2<Short>
interface ByteIArray2 : IArray2<Byte>

fun <T> IArray2<T>.observe(updated: ObservableArray2<T>.(rect: RectangleInt) -> Unit): ObservableArray2<T> = ObservableArray2(this, updated)

fun <R> ObservableArray<*>.lock(block: () -> R): R {
    lock()
    return try {
        block()
    } finally {
        unlock()
    }
}

/**
 * 可观察数组的通用接口。
 * - `version`：变更版本号；在 `flush()` 后递增。
 * - `lock()`/`unlock()`：批量变更的临界区控制；`unlock()` 时若空转到 0 则触发 `flush()`。
 * - `flush()`：将累计的变更范围推送到观察者。
 */
interface ObservableArray<T> {
    val version: Int

    fun flush()

    fun lock()

    fun unlock()
}

/**
 * 二维数组的可观察包装器。
 * - 包装 `IArray2<T>` 并提供区域级变更聚合与版本控制。
 * - 在锁定期间累积最小包围矩形；解锁且锁计数归零时触发 `flush()` 回调。
 */
class ObservableArray2<T> internal constructor(
    private val base: IArray2<T>,
    private val updated: ObservableArray2<T>.(rect: RectangleInt) -> Unit
) : IArray2<T> by base, ObservableArray<T> {

    private val _version = atomic(0)

    override val version: Int get() = _version.value

    private var minX: Int = 0
    private var minY: Int = 0
    private var maxX: Int = 0
    private var maxY: Int = 0

    private val locked = atomic(0)

    override fun set(x: Int, y: Int, value: T) {
        base[x, y] = value
        modify(x, y)
    }

    override fun set(rect: RectangleInt, value: T) {
        base[rect] = value
        modify(rect)
    }

    private fun modify(x: Int, y: Int) {
        minX = minOf(minX, x).coerceIn(0, width - 1)
        minY = minOf(minY, y).coerceIn(0, height - 1)
        maxX = maxOf(maxX, x).coerceIn(0, width - 1)
        maxY = maxOf(maxY, y).coerceIn(0, height - 1)
    }

    private fun modify(rect: RectangleInt) {
        modify(rect.x, rect.y)
        modify(rect.x + rect.width - 1, rect.y + rect.height - 1)
    }

    override fun lock() {
        locked.incrementAndGet()
    }

    override fun unlock() {
        if (locked.decrementAndGet() == 0) flush()
    }

    override fun flush() {
        if (locked.value == 0 || size == 0) return
        _version.incrementAndGet()
        updated(RectangleInt(minX, minY, maxX - minX + 1, maxY - minY + 1))
    }
}
