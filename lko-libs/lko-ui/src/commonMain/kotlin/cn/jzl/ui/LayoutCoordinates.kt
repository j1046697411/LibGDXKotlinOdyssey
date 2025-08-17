package cn.jzl.ui

data class IntSize(val width: Int, val height: Int) {
    companion object {
        val ZERO = IntSize(0, 0)
    }
}
data class Coordinate(val x: Int, val y: Int) {
    companion object {
        val ZERO = Coordinate(0, 0)
    }
}

interface LayoutCoordinates {

    val size: IntSize

    val parentLayoutCoordinates: LayoutCoordinates?

    val isAttached: Boolean
}