package cn.jzl.datastructure.math.vector

import cn.jzl.datastructure.math.vector.generic.IVectorList

interface IPointList : IVectorList<Float, Point> {
    override val size: Int
    val closed: Boolean
}

