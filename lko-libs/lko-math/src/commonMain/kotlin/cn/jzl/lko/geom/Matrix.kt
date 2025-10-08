package cn.jzl.lko.geom

import cn.jzl.lko.geom.vector.Dimension

interface Matrix<T> : Dimension {

    operator fun get(row: Int, column: Int): T

    operator fun set(row: Int, column: Int, value: T)
}

