package cn.jzl.datastructure.array2

import cn.jzl.datastructure.math.geom.RectangleInt
import kotlin.math.abs

class DoubleArray2 @PublishedApi internal constructor(override val width: Int, override val height: Int, val data: DoubleArray) : DoubleIArray2 {

    init {
        checkArraySize(data.size)
    }

    override operator fun get(x: Int, y: Int): Double {
        checkIndex(x, y)
        return data[index(x, y)]
    }

    override operator fun set(x: Int, y: Int, value: Double) {
        checkIndex(x, y)
        data[index(x, y)] = value
    }

    override fun set(rect: RectangleInt, value: Double) {
        checkRange(rect)
        for (i in 0 until rect.height) {
            val index = index(rect.x, rect.y + i)
            data.fill(value, index, index + rect.width)
        }
    }

    override fun iterator(): Iterator<Double> = data.iterator()

    companion object {
        inline operator fun invoke(width: Int, height: Int, gen: (x: Int, y: Int) -> Double) = DoubleArray2(width, height, DoubleArray(width * height) { gen(it % width, it / width) })
        operator fun invoke(width: Int, height: Int, fill: Double) = DoubleArray2(width, height, DoubleArray(width * height) { fill })
    }
}