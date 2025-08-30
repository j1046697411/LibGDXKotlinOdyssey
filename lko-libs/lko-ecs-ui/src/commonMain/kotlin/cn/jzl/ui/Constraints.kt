@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import cn.jzl.lko.*
import cn.jzl.lko.math.IntSize

@JvmInline
@Immutable
value class Constraints @PublishedApi internal constructor(private val data: Long) {
    val minWidth: Int get() = data.low.extract16(0)
    val maxWidth: Int get() = data.low.extract16(16)
    val minHeight: Int get() = data.high.extract16(0)
    val maxHeight: Int get() = data.high.extract16(16)

    fun copy(
        minWidth: Int = this.minWidth,
        maxWidth: Int = this.maxWidth,
        minHeight: Int = this.minHeight,
        maxHeight: Int = this.maxHeight
    ): Constraints = Constraints(minWidth, maxWidth, minHeight, maxHeight)

    override fun toString(): String {
        return "Constraints(minWidth=$minWidth, maxWidth=$maxWidth, minHeight=$minHeight, maxHeight=$maxHeight)"
    }

    operator fun component1(): Int = minWidth
    operator fun component2(): Int = maxWidth
    operator fun component3(): Int = minHeight
    operator fun component4(): Int = maxHeight

    companion object {
        operator fun invoke(minWidth: Int, maxWidth: Int, minHeight: Int, maxHeight: Int): Constraints {
            require(minWidth >= 0) { "minWidth must be >= 0" }
            require(maxWidth >= 0) { "maxWidth must be >= 0" }
            require(minHeight >= 0) { "minHeight must be >= 0" }
            require(maxHeight >= 0) { "maxHeight must be >= 0" }
            val low = minWidth.fastInsert16(maxWidth, 16)
            val high = minHeight.fastInsert16(maxHeight, 16)
            return Constraints(Long.fromLowHigh(low, high))
        }

        fun fixed(width: Int, height: Int) : Constraints {
            return Constraints(width, width, height, height)
        }
    }
}

inline val Constraints.isZero: Boolean get() = maxWidth == 0 && maxHeight == 0
inline val Constraints.hasFixedWidth: Boolean get() = maxWidth == minWidth
inline val Constraints.hasFixedHeight: Boolean get() = maxHeight == minHeight

@Stable
inline fun Constraints.constrainWidth(width: Int): Int = width.coerceIn(minWidth, maxWidth)

@Stable
inline fun Constraints.constrainHeight(height: Int): Int = height.coerceIn(minHeight, maxHeight)

@Stable
inline fun Constraints.constrain(size: IntSize): IntSize = IntSize(
    size.width.coerceIn(minWidth, maxWidth),
    size.height.coerceIn(minHeight, maxHeight)
)

@Stable
inline fun Constraints.constrain(other: Constraints): Constraints = Constraints(
    minWidth = minWidth.coerceIn(other.minWidth, other.maxWidth),
    maxWidth = maxWidth.coerceIn(other.minWidth, other.maxWidth),
    minHeight = minHeight.coerceIn(other.minHeight, other.maxHeight),
    maxHeight = maxHeight.coerceIn(other.minHeight, other.maxHeight)
)

@Stable
inline fun Constraints.isSatisfiedBy(size: IntSize) : Boolean {
    return size.width in minWidth..maxWidth && size.height in minHeight..maxHeight
}

fun main() {
    val constraints = Constraints(100, 200, 300, 400)
    println("constrain ${constraints.constrain(IntSize(100, 300))}")
    println("constrainWidth ${constraints.constrainWidth(100)}")
    println("constrainHeight ${constraints.constrainHeight(100)}")
    println("constrain ${constraints.constrain(Constraints(100, 200, 300, 400))}")
    println("isSatisfiedBy ${constraints.isSatisfiedBy(IntSize(100, 400))}")
    println(constraints)
}