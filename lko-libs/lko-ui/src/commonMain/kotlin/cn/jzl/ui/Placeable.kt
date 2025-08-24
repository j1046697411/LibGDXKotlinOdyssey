package cn.jzl.ui

import cn.jzl.lko.math.IntPoint2
import cn.jzl.lko.math.IntSize

interface Placeable {
    val size: IntSize
    fun place(position: IntPoint2)
}