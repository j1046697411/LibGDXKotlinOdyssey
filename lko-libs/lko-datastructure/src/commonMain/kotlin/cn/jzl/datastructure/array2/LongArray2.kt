package cn.jzl.datastructure.array2

import cn.jzl.datastructure.geom.RectangleInt

class LongArray2 @PublishedApi internal constructor(override val width: Int, override val height: Int, val data: LongArray) : LongIArray2 {

    init {
        checkArraySize(data.size)
    }

    override operator fun get(x: Int, y: Int): Long {
        checkIndex(x, y)
        return data[index(x, y)]
    }

    override operator fun set(x: Int, y: Int, value: Long) {
        checkIndex(x, y)
        data[index(x, y)] = value
    }

    override fun set(rect: RectangleInt, value: Long) {
        checkRange(rect)
        for (i in 0 until rect.height) {
            val index = index(rect.x, rect.y + i)
            data.fill(value, index, index + rect.width)
        }
    }

    override operator fun contains(element: Long): Boolean = element in data

    override fun iterator(): Iterator<Long> = data.iterator()

    companion object {
        inline operator fun invoke(width: Int, height: Int, gen: (x: Int, y: Int) -> Long) = LongArray2(width, height, LongArray(width * height) { gen(it % width, it / width) })
        operator fun invoke(width: Int, height: Int, fill: Long) = LongArray2(width, height, LongArray(width * height) { fill })
    }
}