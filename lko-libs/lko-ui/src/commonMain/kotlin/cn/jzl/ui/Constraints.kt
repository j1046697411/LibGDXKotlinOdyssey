package cn.jzl.ui

data class Constraints(
    val minWidth: Int = 0,
    val maxWidth: Int = Int.MAX_VALUE,
    val minHeight: Int = 0,
    val maxHeight: Int = Int.MAX_VALUE
) {
    companion object {
        fun fixed(
            width: Int,
            height: Int
        ): Constraints = Constraints(width, width, height, height)
    }
}

fun Constraints.constrain(size: IntSize): IntSize = IntSize(constrainWidth(size.width), constrainHeight(size.height))

fun Constraints.constrainWidth(width: Int): Int = width.coerceIn(minWidth, maxWidth)

fun Constraints.constrainHeight(height: Int): Int = height.coerceIn(minHeight, maxHeight)
