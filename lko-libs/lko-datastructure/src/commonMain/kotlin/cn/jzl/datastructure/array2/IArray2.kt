package cn.jzl.datastructure.array2

import cn.jzl.datastructure.geom.RectangleInt
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

interface IArray2<T> : Sequence<T> {
    val width: Int

    val height: Int

    operator fun get(x: Int, y: Int): T

    operator fun set(x: Int, y: Int, value: T)

    operator fun set(rect: RectangleInt, value: T)

    operator fun contains(element: T): Boolean
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

interface ObservableArray<T> {
    val version: Int

    fun flush()

    fun lock()

    fun unlock()
}

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
