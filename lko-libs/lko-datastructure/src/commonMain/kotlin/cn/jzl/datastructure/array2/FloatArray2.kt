package cn.jzl.datastructure.array2

import cn.jzl.datastructure.math.geom.RectangleInt
import kotlin.math.abs

class FloatArray2 @PublishedApi internal constructor(override val width: Int, override val height: Int, val data: FloatArray) : FloatIArray2 {

    init {
        checkArraySize(data.size)
    }

    override operator fun get(x: Int, y: Int): Float {
        checkIndex(x, y)
        return data[index(x, y)]
    }

    override operator fun set(x: Int, y: Int, value: Float) {
        checkIndex(x, y)
        data[index(x, y)] = value
    }

    override fun set(rect: RectangleInt, value: Float) {
        checkRange(rect)
        for (i in 0 until rect.height) {
            val index = index(rect.x, rect.y + i)
            data.fill(value, index, index + rect.width)
        }
    }

    override fun iterator(): Iterator<Float> = data.iterator()

    companion object {
        inline operator fun invoke(width: Int, height: Int, gen: (x: Int, y: Int) -> Float) = FloatArray2(width, height, FloatArray(width * height) { gen(it % width, it / width) })
        operator fun invoke(width: Int, height: Int, fill: Float) = FloatArray2(width, height, FloatArray(width * height) { fill })
    }
}