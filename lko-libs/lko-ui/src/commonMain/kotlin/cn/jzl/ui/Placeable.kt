package cn.jzl.ui

interface Placeable {
    var size: IntSize

    fun placeAt(position: Coordinate)
}