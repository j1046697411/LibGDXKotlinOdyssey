package cn.jzl.datastructure.array2

import cn.jzl.datastructure.math.geom.RectangleInt

class IntArray2 @PublishedApi internal constructor(override val width: Int, override val height: Int, val data: IntArray) : IntIArray2 {

    init {
        checkArraySize(data.size)
    }

    override operator fun get(x: Int, y: Int): Int {
        checkIndex(x, y)
        return data[index(x, y)]
    }

    override operator fun set(x: Int, y: Int, value: Int) {
        checkIndex(x, y)
        data[index(x, y)] = value
    }

    override fun set(rect: RectangleInt, value: Int) {
        checkRange(rect)
        for (i in 0 until rect.height) {
            val index = index(rect.x, rect.y + i)
            data.fill(value, index, index + rect.width)
        }
    }

    override operator fun contains(element: Int): Boolean = element in data

    override fun iterator(): Iterator<Int> = data.iterator()

    companion object {
        inline operator fun invoke(width: Int, height: Int, gen: (x: Int, y: Int) -> Int) = IntArray2(width, height, IntArray(width * height) { gen(it % width, it / width) })
        operator fun invoke(width: Int, height: Int, fill: Int) = IntArray2(width, height, IntArray(width * height) { fill })
    }
}