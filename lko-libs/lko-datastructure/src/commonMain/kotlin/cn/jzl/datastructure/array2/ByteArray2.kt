package cn.jzl.datastructure.array2

import cn.jzl.datastructure.math.geom.RectangleInt

class ByteArray2 @PublishedApi internal constructor(override val width: Int, override val height: Int, val data: ByteArray) : ByteIArray2 {

    init {
        checkArraySize(data.size)
    }

    override operator fun get(x: Int, y: Int): Byte {
        checkIndex(x, y)
        return data[index(x, y)]
    }

    override operator fun set(x: Int, y: Int, value: Byte) {
        checkIndex(x, y)
        data[index(x, y)] = value
    }

    override fun set(rect: RectangleInt, value: Byte) {
        checkRange(rect)
        for (i in 0 until rect.height) {
            val index = index(rect.x, rect.y + i)
            data.fill(value, index, index + rect.width)
        }
    }

    override fun iterator(): Iterator<Byte> = data.iterator()

    companion object {
        inline operator fun invoke(width: Int, height: Int, gen: (x: Int, y: Int) -> Byte) = ByteArray2(width, height, ByteArray(width * height) { gen(it % width, it / width) })
        operator fun invoke(width: Int, height: Int, fill: Byte) = ByteArray2(width, height, ByteArray(width * height) { fill })
    }
}