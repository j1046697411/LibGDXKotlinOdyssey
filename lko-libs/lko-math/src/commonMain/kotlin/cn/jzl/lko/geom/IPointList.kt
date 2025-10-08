package cn.jzl.lko.geom

import cn.jzl.datastructure.FastListAdder
import cn.jzl.datastructure.MutableFastList
import cn.jzl.lko.geom.vector.Point2

interface IPointList : IVectorList<Float>, MutableFastList<Point2> {
    override val dimensions: Int get() = 2
    override val size: Int
    override val closed: Boolean

    override fun isEmpty(): Boolean = size == 0

    override fun get(index: Int, dimension: Int): Float

    override fun get(index: Int): Point2

    override fun add(e: Point2)

    override fun add(e1: Point2, e2: Point2)

    override fun add(e1: Point2, e2: Point2, e3: Point2)

    override fun add(e1: Point2, e2: Point2, e3: Point2, e4: Point2)

    override fun add(e1: Point2, e2: Point2, e3: Point2, e4: Point2, e5: Point2)

    override fun safeAdd(count: Int, callback: FastListAdder<Point2>.() -> Unit)

    override fun safeSet(index: Int, element: Point2)

    override fun set(index: Int, element: Point2)

    override fun plusAssign(elements: Iterable<Point2>)

    override fun clear()
}

