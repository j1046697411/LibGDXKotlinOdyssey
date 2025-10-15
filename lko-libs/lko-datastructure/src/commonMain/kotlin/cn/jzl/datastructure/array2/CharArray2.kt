package cn.jzl.datastructure.array2

import cn.jzl.datastructure.math.geom.RectangleInt

class CharArray2 @PublishedApi internal constructor(override val width: Int, override val height: Int, val data: CharArray) : CharIArray2 {

    init {
        checkArraySize(data.size)
    }

    override operator fun get(x: Int, y: Int): Char {
        checkIndex(x, y)
        return data[index(x, y)]
    }

    override operator fun set(x: Int, y: Int, value: Char) {
        checkIndex(x, y)
        data[index(x, y)] = value
    }

    override fun set(rect: RectangleInt, value: Char) {
        checkRange(rect)
        for (i in 0 until rect.height) {
            val index = index(rect.x, rect.y + i)
            data.fill(value, index, index + rect.width)
        }
    }

    override operator fun contains(element: Char): Boolean = element in data

    override fun iterator(): Iterator<Char> = data.iterator()

    companion object {
        inline operator fun invoke(width: Int, height: Int, gen: (x: Int, y: Int) -> Char) = CharArray2(width, height, CharArray(width * height) { gen(it % width, it / width) })
        operator fun invoke(width: Int, height: Int, fill: Char) = CharArray2(width, height, CharArray(width * height) { fill })
    }
}