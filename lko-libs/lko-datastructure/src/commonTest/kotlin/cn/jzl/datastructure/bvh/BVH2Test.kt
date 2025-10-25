package cn.jzl.datastructure.bvh

import cn.jzl.datastructure.math.geom.Rectangle
import cn.jzl.datastructure.math.vector.Point
import cn.jzl.datastructure.math.vector.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BVH2Test {

    @Test
    // 场景：向 BVH2 插入同一个值两次（边界一致）。
    // 预期：第一次插入成功（true），第二次作为重复插入失败（false）。
    fun testInsertAndDuplicate() {
        val bvh2 = BVH2<Int>(BVH(2))
        val ok1 = bvh2.insert(Rectangle(0f, 0f, 1f, 1f), 1)
        val ok2 = bvh2.insert(Rectangle(0f, 0f, 1f, 1f), 1)
        assertTrue(ok1)
        assertFalse(ok2) // duplicate should fail
    }

    @Test
    // 场景：批量插入 3 个值（2、3、4），每个值生成一个靠近对角线的单位矩形
    // 预期：查询结果应包含所有插入的值集合 {2,3,4}。
    fun testBulkInsertAndRectSearch() {
        val bvh2 = BVH2<Int>(BVH(2))
        val items = sequenceOf(2, 3, 4)
        val count = bvh2.bulkInsert(items) { 
            val v = this.toFloat()
            Rectangle(v, v, v + 1f, v + 1f)
        }
        assertEquals(3, count)
        val result = mutableListOf<Int>()
        bvh2.searchValuesByRect(Rectangle(0f, 0f, 10f, 10f), result)
        assertEquals(setOf(2,3,4), result.toSet())
    }

    @Test
    // 场景：插入值 5 的近邻矩形；第一次在近邻范围查询可命中；然后更新到远离位置；再次近邻查询不命中；最后删除该值并在远处范围查询不命中。
    // 预期：更新与删除返回 true；近邻查询不包含 5；远处查询也不包含 5。
    fun testUpdateAndRemoveAndRectSearch() {
        val bvh2 = BVH2<Int>(BVH(2))
        assertTrue(bvh2.insert(Rectangle(0f, 0f, 2f, 2f), 5))
        val near = mutableListOf<Int>()
        bvh2.searchValuesByRect(Rectangle(0f, 0f, 3f, 3f), near)
        assertTrue(near.contains(5))
        // move far away
        val updated = bvh2.update(Rectangle(100f, 100f, 110f, 110f), 5)
        assertTrue(updated)
        val near2 = mutableListOf<Int>()
        bvh2.searchValuesByRect(Rectangle(0f, 0f, 3f, 3f), near2)
        assertFalse(near2.contains(5))
        // remove
        val removed = bvh2.remove(5)
        assertTrue(removed)
        val far = mutableListOf<Int>()
        bvh2.searchValuesByRect(Rectangle(90f, 90f, 140f, 140f), far)
        assertFalse(far.contains(5))
    }

    @Test
    // 场景：沿 x 轴依次放置 3 个单位方块；一条从左到右的水平射线穿过它们。
    // 预期：射线与三个矩形均相交，返回的命中集合为 {10,20,30}。
    fun testRaySearch() {
        val bvh2 = BVH2<Int>(BVH(2))
        // three unit squares along x-axis
        bvh2.insert(Rectangle(0f, 0f, 1f, 1f), 10)
        bvh2.insert(Rectangle(2f, 0f, 1f, 1f), 20)
        bvh2.insert(Rectangle(4f, 0f, 1f, 1f), 30)
        val hits = mutableListOf<Int>()
        bvh2.searchValuesByRay(origin = Point(-1f, 0.5f), direction = Vector2(1f, 0f), result = hits)
        assertEquals(setOf(10, 20, 30), hits.toSet())
    }

    @Test
    // 场景：分别使用“最小/最大点”与“标量边界”两个重载插入值；在包含两者范围的查询矩形内检索。
    // 预期：查询结果包含两个插入的值集合 {1,2}。
    fun testInsertOverloads() {
        val bvh2 = BVH2<Int>(BVH(2))
        assertTrue(bvh2.insert(min = Point(0f, 0f), max = Point(1f, 1f), value = 1))
        assertTrue(bvh2.insert(minX = 2f, minY = 2f, maxX = 3f, maxY = 3f, value = 2))
        val res = mutableListOf<Int>()
        bvh2.searchValuesByRect(Rectangle(0f, 0f, 5f, 5f), res)
        assertEquals(setOf(1,2), res.toSet())
    }
}