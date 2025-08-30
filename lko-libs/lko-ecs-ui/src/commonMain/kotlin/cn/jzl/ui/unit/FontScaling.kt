package cn.jzl.ui.unit

import kotlin.math.roundToInt

interface FontScaling {
    val fontScale: Float
}

interface Density : FontScaling {

    val density: Float

    companion object Default : Density {
        override val density: Float = 1f
        override val fontScale: Float = 1f
    }
}

interface UIUnitScope {

    val density: Density

    fun UIUnit.toPixel(constraints: Int): Int = pixel(constraints)
}

interface UIUnit {

    fun UIUnitScope.pixel(constraints: Int): Int

    data object Auto : UIUnit {
        override fun UIUnitScope.pixel(constraints: Int): Int = 0
    }

    @JvmInline
    value class Pixel(private val value: Int) : UIUnit {
        override fun UIUnitScope.pixel(constraints: Int): Int = value
    }

    @JvmInline
    value class Dp(private val value: Float) : UIUnit {
        override fun UIUnitScope.pixel(constraints: Int): Int = (value * density.density).roundToInt()
    }
}

val Int.dp: UIUnit.Dp get() = UIUnit.Dp(this.toFloat())
val Float.dp: UIUnit.Dp get() = UIUnit.Dp(this)
val Double.dp: UIUnit.Dp get() = UIUnit.Dp(this.toFloat())

val Int.px: UIUnit.Pixel get() = UIUnit.Pixel(this)
val Float.px: UIUnit.Pixel get() = UIUnit.Pixel(this.roundToInt())
val Double.px: UIUnit.Pixel get() = UIUnit.Pixel(this.roundToInt())
