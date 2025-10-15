package cn.jzl.datastructure.math.vector

import cn.jzl.datastructure.math.vector.PointList
import cn.jzl.datastructure.math.vector.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PointListTest {

    // 测试构造函数和基本属性
    @Test
    fun testConstructorAndBasicProperties() {
        val pointList = PointList(10, true)
        assertEquals(0, pointList.size)
        assertTrue(pointList.closed)
        assertTrue(pointList.isEmpty())
    }

    // 测试插入单个点
    @Test
    fun testInsertLastSinglePoint() {
        val pointList = PointList(10, false)
        val point = Vector2(1f, 2f)
        pointList.insertLast(point)

        assertEquals(1, pointList.size)
        val retrievedPoint = pointList[0]
        assertEquals(1f, retrievedPoint.x)
        assertEquals(2f, retrievedPoint.y)
    }

    // 测试插入多个点
    @Test
    fun testInsertLastMultiplePoints() {
        val pointList = PointList(10, false)
        val point1 = Vector2(1f, 2f)
        val point2 = Vector2(3f, 4f)
        val point3 = Vector2(5f, 6f)

        pointList.insertLast(point1, point2)
        assertEquals(2, pointList.size)
        assertEquals(1f, pointList[0].x)
        assertEquals(4f, pointList[1].y)

        pointList.insertLast(point3)
        assertEquals(3, pointList.size)
        assertEquals(5f, pointList[2].x)
        assertEquals(6f, pointList[2].y)
    }

    // 测试设置点
    @Test
    fun testSetPoint() {
        val pointList = PointList(10, false)
        val point1 = Vector2(1f, 2f)
        val point2 = Vector2(3f, 4f)

        pointList.insertLast(point1)
        val oldPoint = pointList.set(0, point2)

        assertEquals(1f, oldPoint.x)
        assertEquals(2f, oldPoint.y)
        assertEquals(3f, pointList[0].x)
        assertEquals(4f, pointList[0].y)
    }

    // 测试移除点
    @Test
    fun testRemoveAt() {
        val pointList = PointList(10, false)
        val point1 = Vector2(1f, 2f)
        val point2 = Vector2(3f, 4f)

        pointList.insertLast(point1, point2)
        val removedPoint = pointList.removeAt(0)

        assertEquals(1f, removedPoint.x)
        assertEquals(2f, removedPoint.y)
        assertEquals(1, pointList.size)
        assertEquals(3f, pointList[0].x)
        assertEquals(4f, pointList[0].y)
    }

    // 测试在指定位置插入点
    @Test
    fun testInsertAtPosition() {
        val pointList = PointList(10, false)
        val point1 = Vector2(1f, 2f)
        val point2 = Vector2(3f, 4f)
        val point3 = Vector2(5f, 6f)

        pointList.insertLast(point1, point3)
        pointList.insert(1, point2)

        assertEquals(3, pointList.size)
        assertEquals(1f, pointList[0].x)
        assertEquals(3f, pointList[1].x)
        assertEquals(5f, pointList[2].x)
    }

    // 测试插入所有元素
    @Test
    fun testInsertAll() {
        val pointList = PointList(10, false)
        val point1 = Vector2(1f, 2f)
        val pointsToInsert = listOf(Vector2(3f, 4f), Vector2(5f, 6f))

        pointList.insertLast(point1)
        pointList.insertAll(1, pointsToInsert)

        assertEquals(3, pointList.size)
        assertEquals(1f, pointList[0].x)
        assertEquals(3f, pointList[1].x)
        assertEquals(5f, pointList[2].x)
    }

    // 测试边界检查
    @Test
    fun testIndexOutOfBounds() {
        val pointList = PointList(10, false)
        assertFailsWith<IndexOutOfBoundsException> {
            pointList[0]
        }

        pointList.insertLast(Vector2(1f, 2f))
        assertFailsWith<IndexOutOfBoundsException> {
            pointList[1]
        }
    }

    // 测试关闭属性
    @Test
    fun testClosedProperty() {
        val closedList = PointList(10, true)
        val openList = PointList(10, false)

        assertTrue(closedList.closed)
        assertFalse(openList.closed)
    }

    // 测试批量插入多个点的方法
    @Test
    fun testInsertLastMultiplePointsMethods() {
        val pointList = PointList(10, false)
        val p1 = Vector2(1f, 2f)
        val p2 = Vector2(3f, 4f)
        val p3 = Vector2(5f, 6f)
        val p4 = Vector2(7f, 8f)
        val p5 = Vector2(9f, 10f)
        val p6 = Vector2(11f, 12f)

        pointList.insertLast(p1, p2, p3)
        assertEquals(3, pointList.size)
        assertEquals(5f, pointList[2].x)

        pointList.insertLast(p4, p5, p6)
        assertEquals(6, pointList.size)
        assertEquals(11f, pointList[5].x)
    }

    // 测试safeInsertLast方法
    @Test
    fun testSafeInsertLast() {
        val pointList = PointList(10, false)
        pointList.safeInsertLast(2) {
            unsafeInsert(Vector2(1f, 2f))
            unsafeInsert(Vector2(3f, 4f))
        }

        assertEquals(2, pointList.size)
        assertEquals(1f, pointList[0].x)
        assertEquals(3f, pointList[1].x)
    }

    // 测试safeInsert方法
    @Test
    fun testSafeInsert() {
        val pointList = PointList(10, false)
        pointList.insertLast(Vector2(5f, 6f))

        pointList.safeInsert(0, 2) {
            unsafeInsert(Vector2(1f, 2f))
            unsafeInsert(Vector2(3f, 4f))
        }

        assertEquals(3, pointList.size)
        assertEquals(1f, pointList[0].x)
        assertEquals(3f, pointList[1].x)
        assertEquals(5f, pointList[2].x)
    }
}
