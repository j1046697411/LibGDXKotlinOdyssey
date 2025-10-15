package cn.jzl.datastructure.array2

import cn.jzl.datastructure.math.geom.RectangleInt
import cn.jzl.datastructure.list.ObjectFastList
import kotlin.math.max

fun <T> IStackedArray2<T>.push(x: Int, y: Int, value: T) {
    set(x, y, getStackLevel(x, y), value)
}

fun IStackedArray2<*>.inside(x: Int, y: Int): Boolean {
    return range.x <= x && x < range.x + range.width && range.y <= y && y < range.y + range.height
}

fun IStackedArray2<*>.inside(x: Int, y: Int, level: Int): Boolean {
    return inside(x, y) && level >= 0 && level < getStackLevel(x, y)
}

interface IStackedArray2<T> {
    val range: RectangleInt
    val contentVersion: Int
    val maxLevel: Int

    val default: T
    fun layer(level: Int): ObservableArray2<T>
    fun getStackLevel(x: Int, y: Int): Int
    operator fun get(x: Int, y: Int, level: Int): T
    operator fun set(x: Int, y: Int, level: Int, value: T)
}

class StackedArray2<T>(
    override val range: RectangleInt,
    override val default: T,
    private val factory: (T) -> IArray2<T>
) : IStackedArray2<T> {

    override var contentVersion: Int = 0
        private set

    private val levels = IntArray2(range.width, range.height, 0)
    private val data = ObjectFastList<IArray2<T>>()
    override val maxLevel: Int get() = data.size

    private fun checkIndex(x: Int, y: Int) = check(inside(x, y)) { "index out of range: ($x, $y)" }

    private fun ensureLevel(level: Int) {
        while (level >= maxLevel) {
            data.add(factory(default))
        }
    }

    override fun layer(level: Int): ObservableArray2<T> {
        check(level < maxLevel) { "level out of range: $level" }
        return data[level].observe { contentVersion++ }
    }

    override fun set(x: Int, y: Int, level: Int, value: T) {
        checkIndex(x, y)
        ensureLevel(level)
        data[level][x, y] = value
        levels[x, y] = max(levels[x, y], level + 1)
        contentVersion++
    }

    override fun get(x: Int, y: Int, level: Int): T {
        if (!inside(x, y, level)) return default
        return data[level][x, y]
    }

    override fun getStackLevel(x: Int, y: Int): Int {
        check(inside(x, y)) { "index out of range: ($x, $y)" }
        return levels[x, y]
    }
}

class SparseChunkedStackedArray2<T> : IStackedArray2<T> {
    override val range: RectangleInt
        get() = TODO("Not yet implemented")
    override val contentVersion: Int
        get() = TODO("Not yet implemented")
    override val maxLevel: Int
        get() = TODO("Not yet implemented")
    override val default: T
        get() = TODO("Not yet implemented")

    override fun layer(level: Int): ObservableArray2<T> {
        TODO("Not yet implemented")
    }

    override fun getStackLevel(x: Int, y: Int): Int {
        TODO("Not yet implemented")
    }

    override fun get(x: Int, y: Int, level: Int): T {
        TODO("Not yet implemented")
    }

    override fun set(x: Int, y: Int, level: Int, value: T) {
        TODO("Not yet implemented")
    }
}