package cn.jzl.datastructure.math.vector

import cn.jzl.datastructure.list.FloatFastList
import cn.jzl.datastructure.list.ListEditor
import cn.jzl.datastructure.math.vector.generic.AbstractVectorList

class PointList(capacity: Int, override val closed: Boolean) : AbstractVectorList<Float, Point>(2, FloatFastList(capacity * 2)), IPointList {

    override fun get(index: Int): Point {
        checkIndex(index)
        return Point(data[index * 2], data[index * 2 + 1])
    }

    override fun set(index: Int, element: Point): Point {
        checkIndex(index)
        val oldX = data.set(index * 2, element.x)
        val oldY = data.set(index * 2 + 1, element.y)
        return Point(oldX, oldY)
    }

    override fun removeAt(index: Int): Point {
        checkIndex(index)
        val oldY = data.removeAt(index * 2 + 1)
        val oldX = data.removeAt(index * 2)
        return Point(oldX, oldY)
    }


    override fun insertLast(element: Point) {
        data.insertLast(element.x, element.y)
    }

    override fun insertLast(element1: Point, element2: Point) {
        data.insertLast(element1.x, element1.y, element2.x, element2.y)
    }

    override fun insertLast(element1: Point, element2: Point, element3: Point) {
        data.insertLast(element1.x, element1.y, element2.x, element2.y, element3.x, element3.y)
    }

    private fun ListEditor<Float>.unsafe(vector: Vector2) {
        unsafeInsert(vector.x)
        unsafeInsert(vector.y)
    }

    override fun insertLast(
        element1: Point,
        element2: Point,
        element3: Point,
        element4: Point
    ) = data.safeInsertLast(8) {
        unsafeInsert(element1.x)
        unsafeInsert(element1.y)
        unsafeInsert(element2.x)
        unsafeInsert(element2.y)
        unsafeInsert(element3.x)
        unsafeInsert(element3.y)
        unsafeInsert(element4.x)
        unsafeInsert(element4.y)
    }

    override fun insertLast(
        element1: Point,
        element2: Point,
        element3: Point,
        element4: Point,
        element5: Point
    ) = data.safeInsertLast(10) {
        unsafe(element1)
        unsafe(element2)
        unsafe(element3)
        unsafe(element4)
        unsafe(element5)
    }

    override fun insertLast(
        element1: Point,
        element2: Point,
        element3: Point,
        element4: Point,
        element5: Point,
        element6: Point
    ) = data.safeInsertLast(12) {
        unsafe(element1)
        unsafe(element2)
        unsafe(element3)
        unsafe(element4)
        unsafe(element5)
        unsafe(element6)
    }

    override fun insertLastAll(elements: Iterable<Point>) {
        when (elements) {
            is Collection<Point> -> data.safeInsertLast(elements.size * 2) {
                elements.forEach { unsafe(it) }
            }

            else -> elements.forEach { data.insertLast(it.x, it.y) }
        }
    }

    override fun insert(index: Int, element: Point) {
        data.insert(index * 2, element.x, element.y)
    }

    override fun insert(index: Int, element1: Point, element2: Point) {
        data.insert(index * 2, element1.x, element1.y, element2.x, element2.y)
    }

    override fun insert(index: Int, element1: Point, element2: Point, element3: Point) {
        data.insert(index * 2, element1.x, element1.y, element2.x, element2.y, element3.x, element3.y)
    }

    override fun insert(
        index: Int,
        element1: Point,
        element2: Point,
        element3: Point,
        element4: Point
    ) = data.safeInsert(index * 2, 8) {
        unsafe(element1)
        unsafe(element2)
        unsafe(element3)
        unsafe(element4)
    }

    override fun insert(
        index: Int,
        element1: Point,
        element2: Point,
        element3: Point,
        element4: Point,
        element5: Point
    ) = data.safeInsert(index * 2, 10) {
        unsafe(element1)
        unsafe(element2)
        unsafe(element3)
        unsafe(element4)
        unsafe(element5)
    }

    override fun insert(
        index: Int,
        element1: Point,
        element2: Point,
        element3: Point,
        element4: Point,
        element5: Point,
        element6: Point
    ) = data.safeInsert(index * 2, 12) {
        unsafe(element1)
        unsafe(element2)
        unsafe(element3)
        unsafe(element4)
        unsafe(element5)
        unsafe(element6)
    }

    override fun insertAll(index: Int, elements: Iterable<Point>) {
        when (elements) {
            is Collection<Point> -> data.safeInsert(index * 2, elements.size * 2) {
                elements.forEach { unsafe(it) }
            }

            else -> elements.forEach { data.insertLast(it.x, it.y) }
        }
    }

    override fun safeInsertLast(count: Int, callback: ListEditor<Point>.() -> Unit) {
        data.safeInsertLast(count * 2) {
            ListEditor<Vector2> { unsafe(it) }.apply(callback)
        }
    }

    override fun safeInsert(index: Int, count: Int, callback: ListEditor<Point>.() -> Unit) {
        data.safeInsert(index * 2, count * 2) {
            ListEditor<Vector2> { unsafe(it) }.apply(callback)
        }
    }
}