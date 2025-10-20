package cn.jzl.datastructure.array2

import cn.jzl.datastructure.math.geom.RectangleInt

data class Array2<T>(override val width: Int, override val height: Int, val data: Array<T>) : IArray2<T> {
    init {
        checkArraySize(data.size)
    }

    override operator fun get(x: Int, y: Int): T {
        checkIndex(x, y)
        return data[index(x, y)]
    }

    override operator fun set(x: Int, y: Int, value: T) {
        checkIndex(x, y)
        data[index(x, y)] = value
    }

    override fun set(rect: RectangleInt, value: T) {
        checkRange(rect)
        for (i in 0 until rect.height) {
            val index = index(rect.x, rect.y + i)
            data.fill(value, index, index + rect.width)
        }
    }

    override fun iterator(): Iterator<T> = data.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Array2<*>) return false

        if (width != other.width) return false
        if (height != other.height) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + data.contentHashCode()
        return result
    }
}