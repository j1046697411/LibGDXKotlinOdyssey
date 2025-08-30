package cn.jzl.ui

import androidx.compose.runtime.Stable
import cn.jzl.lko.math.IntPoint2
import cn.jzl.lko.math.IntSize
import kotlin.math.roundToInt

fun interface Alignment {

    fun align(size: IntSize, space: IntSize): IntPoint2
    fun interface Horizontal {
        fun align(size: Int, space: Int): Int
    }

    fun interface Vertical {
        fun align(size: Int, space: Int): Int
    }

    companion object {
        @Stable
        val Top = BiasAlignment.Vertical(-1f)

        @Stable
        val CenterVertically = BiasAlignment.Vertical(0f)

        @Stable
        val Bottom = BiasAlignment.Vertical(1f)

        @Stable
        val Start = BiasAlignment.Horizontal(-1f)

        @Stable
        val CenterHorizontally = BiasAlignment.Horizontal(0f)

        @Stable
        val End = BiasAlignment.Horizontal(1f)

        @Stable
        val TopStart = BiasAlignment(Start, Top)

        @Stable
        val TopEnd = BiasAlignment(End, Top)

        @Stable
        val BottomStart = BiasAlignment(Start, Bottom)

        @Stable
        val BottomEnd = BiasAlignment(End, Bottom)

        @Stable
        val Center = BiasAlignment(CenterHorizontally, CenterVertically)

        @Stable
        val CenterTop = BiasAlignment(CenterHorizontally, Top)

        @Stable
        val CenterBottom = BiasAlignment(CenterHorizontally, Bottom)

        @Stable
        val CenterStart = BiasAlignment(Start, CenterVertically)

        @Stable
        val CenterEnd = BiasAlignment(End, CenterVertically)
    }
}

data class BiasAlignment(
    val horizontal: Horizontal,
    val vertical: Vertical
) : Alignment {

    override fun align(size: IntSize, space: IntSize): IntPoint2 {
        val x = horizontal.align(size.width, space.width)
        val y = vertical.align(size.height, space.height)
        return IntPoint2(x, y)
    }

    @JvmInline
    value class Horizontal(private val bias: Float) : Alignment.Horizontal {
        override fun align(size: Int, space: Int): Int {
            val center = (space - size).toFloat() / 2f
            return (center * (1 + bias)).roundToInt()
        }
    }

    @JvmInline
    value class Vertical(private val bias: Float) : Alignment.Vertical {
        override fun align(size: Int, space: Int): Int {
            val center = (space - size).toFloat() / 2f
            return (center * (1 + bias)).roundToInt()
        }
    }
}