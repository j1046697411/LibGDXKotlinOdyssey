package cn.jzl.datastructure.array2

import cn.jzl.datastructure.math.geom.RectangleInt

class ShortArray2 @PublishedApi internal constructor(override val width: Int, override val height: Int, val data: ShortArray) : ShortIArray2 {

    init {
        checkArraySize(data.size)
    }

    override operator fun get(x: Int, y: Int): Short {
        checkIndex(x, y)
        return data[index(x, y)]
    }

    override operator fun set(x: Int, y: Int, value: Short) {
        checkIndex(x, y)
        data[index(x, y)] = value
    }

    override fun set(rect: RectangleInt, value: Short) {
        checkRange(rect)
        for (i in 0 until rect.height) {
            val index = index(rect.x, rect.y + i)
            data.fill(value, index, index + rect.width)
        }
    }

    override fun iterator(): Iterator<Short> = data.iterator()

    companion object {
        inline operator fun invoke(width: Int, height: Int, gen: (x: Int, y: Int) -> Short) = ShortArray2(width, height, ShortArray(width * height) { gen(it % width, it / width) })
        operator fun invoke(width: Int, height: Int, fill: Short) = ShortArray2(width, height, ShortArray(width * height) { fill })
    }
}