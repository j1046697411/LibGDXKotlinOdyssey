package cn.jzl.lko.math

data class IntRect(val left: Int, val top: Int, val right: Int, val bottom: Int) {

    init {
        require(left <= right) { "left must be less than or equal to right" }
        require(top <= bottom) { "top must be less than or equal to bottom" }
    }

    val width: Int get() = right - left
    val height: Int get() = bottom - top

    val position: IntPoint2 get() = IntPoint2(left, top)
    val size: IntSize get() = IntSize(right - left, bottom - top)

    val center: IntPoint2 get() = IntPoint2(left + width / 2, top + height / 2)
    val centerRight: IntPoint2 get() = IntPoint2(right, top + height / 2)
    val centerBottom: IntPoint2 get() = IntPoint2(left + width / 2, bottom)
    val centerTop: IntPoint2 get() = IntPoint2(left + width / 2, top)
    val centerLeft: IntPoint2 get() = IntPoint2(left, top + height / 2)

    val topRight: IntPoint2 get() = IntPoint2(right, top)
    val bottomLeft: IntPoint2 get() = IntPoint2(left, bottom)
    val topLeft: IntPoint2 get() = IntPoint2(left, top)
    val bottomRight: IntPoint2 get() = IntPoint2(right, bottom)

    fun translate(offset: IntVector2): IntRect = IntRect(position + offset, size)

    fun overlaps(other: IntRect): Boolean {
        return left < other.right && right > other.left && top < other.bottom && bottom > other.top
    }

    override fun toString(): String {
        return "IntRect($left, $top, $right, $bottom)"
    }

    companion object {
        operator fun invoke(position: IntPoint2, size: IntSize): IntRect {
            val left = position.x
            val top = position.y
            val right = position.x + size.width
            val bottom = position.y + size.height
            return IntRect(left, top, right, bottom)
        }
    }
}

