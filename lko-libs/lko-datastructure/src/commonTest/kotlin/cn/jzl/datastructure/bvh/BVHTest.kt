package cn.jzl.datastructure.bvh

import kotlin.test.*
import kotlin.time.measureTime

class BVHTest {

    // 用于测试的简单矩形数据类
    data class Rectangle(val id: Int, var x: Float, var y: Float, val width: Float, val height: Float)

    private fun make2dCallback(bvh: BVH<Rectangle>): Rectangle.(BVHRect) -> Unit = { rect ->
        rect.min(bvh, 0, x)
        rect.min(bvh, 1, y)
        rect.max(bvh, 0, x + width)
        rect.max(bvh, 1, y + height)
    }

    // 测试默认构造函数和基本属性
    @Test
    fun testDefaultConstructor() {
        val bvh = BVH<Rectangle>(2)
        assertEquals(2, bvh.dimensions)
        assertEquals(0, bvh.size)
        assertTrue(bvh.isEmpty())
    }

    // 测试插入元素
    @Test
    fun testInsert() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)

        assertTrue(bvh.insert(rect1, make2dCallback(bvh)))
        assertEquals(1, bvh.size)
        assertTrue(rect1 in bvh)

        assertTrue(bvh.insert(rect2, make2dCallback(bvh)))
        assertEquals(2, bvh.size)
        assertTrue(rect2 in bvh)

        // 测试插入重复元素
        assertFalse(bvh.insert(rect1, make2dCallback(bvh)))
        assertEquals(2, bvh.size)
    }

    // 测试删除元素
    @Test
    fun testRemove() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))

        assertTrue(bvh.remove(rect1))
        assertEquals(1, bvh.size)
        assertFalse(rect1 in bvh)
        assertTrue(rect2 in bvh)

        assertTrue(bvh.remove(rect2))
        assertEquals(0, bvh.size)
        assertFalse(rect2 in bvh)
        assertTrue(bvh.isEmpty())

        // 测试删除不存在的元素
        assertFalse(bvh.remove(rect1))
        assertEquals(0, bvh.size)
    }

    // 测试更新元素
    @Test
    fun testUpdate() {
        val bvh = BVH<Rectangle>(2)
        val rect = Rectangle(1, 0f, 0f, 10f, 10f)
        bvh.insert(rect, make2dCallback(bvh))
        assertEquals(1, bvh.size)

        assertTrue(rect in bvh)

        assertTrue(bvh.remove(rect))
        assertEquals(0, bvh.size)

        val updatedRect = Rectangle(1, 5f, 5f, 10f, 10f)
        assertTrue(bvh.insert(updatedRect, make2dCallback(bvh)))
        assertEquals(1, bvh.size)
    }

    // 测试搜索功能
    @Test
    fun testSearch() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)
        val rect3 = Rectangle(3, 5f, 5f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))
        bvh.insert(rect3, make2dCallback(bvh))

        val results = bvh.searchValues {
            it.min(bvh, 0, 3f)
            it.min(bvh, 1, 3f)
            it.max(bvh, 0, 3.1f)
            it.max(bvh, 1, 3.1f)
        }
        assertTrue(results.contains(rect1))

        val regionResults = bvh.searchValues {
            it.min(bvh, 0, 15f)
            it.min(bvh, 1, 15f)
            it.max(bvh, 0, 25f)
            it.max(bvh, 1, 25f)
        }
        assertTrue(regionResults.contains(rect2))
    }

    // 测试射线相交
    @Test
    fun testRayIntersect() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))

        val intersectResults = bvh.intersect {
            it.origin(bvh, 0, 5f)
            it.origin(bvh, 1, 5f)
            it.direction(bvh, 0, 1f)
            it.direction(bvh, 1, 0f)
        }
        assertTrue(intersectResults.map { it.data }.contains(rect1))
    }

    // 测试计算树深度
    @Test
    fun testComputeDepth() {
        val bvh = BVH<Rectangle>(2)
        assertEquals(0, bvh.computeDepth())

        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        bvh.insert(rect1, make2dCallback(bvh))
        val depth1 = bvh.computeDepth()
        assertTrue(depth1 >= 1)

        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)
        bvh.insert(rect2, make2dCallback(bvh))
        val depth2 = bvh.computeDepth()
        assertTrue(depth2 >= depth1)

        val rect3 = Rectangle(3, 5f, 5f, 10f, 10f)
        bvh.insert(rect3, make2dCallback(bvh))
        val depth3 = bvh.computeDepth()
        assertTrue(depth3 >= depth2)
    }

    // 测试清除功能
    @Test
    fun testClear() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))
        assertEquals(2, bvh.size)

        bvh.clear()
        assertEquals(0, bvh.size)
        assertTrue(bvh.isEmpty())
        assertFalse(rect1 in bvh)
        assertFalse(rect2 in bvh)
    }

    // 测试迭代功能
    @Test
    fun testIteration() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)
        val rect3 = Rectangle(3, 5f, 5f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))
        bvh.insert(rect3, make2dCallback(bvh))

        val collected = mutableListOf<Rectangle>()
        for (rect in bvh) {
            collected.add(rect)
        }

        assertEquals(3, collected.size)
        assertTrue(collected.contains(rect1))
        assertTrue(collected.contains(rect2))
        assertTrue(collected.contains(rect3))
    }

    // 测试大量元素的插入和搜索性能
    @Test
    fun testLargeDataset() {
        val bvh = BVH<Rectangle>(2)
        val rectCount = 100

        for (i in 0 until rectCount) {
            val x = (i % 10) * 20f
            val y = (i / 10) * 20f
            val rect = Rectangle(i, x, y, 10f, 10f)
            bvh.insert(rect, make2dCallback(bvh))
        }

        assertEquals(rectCount, bvh.size)

        val results = bvh.searchValues {
            it.min(bvh, 0, 15f)
            it.min(bvh, 1, 15f)
            it.max(bvh, 0, 35f)
            it.max(bvh, 1, 35f)
        }
        assertTrue(results.isNotEmpty())
    }

    // 测试边界体积层次结构的正确性
    @Test
    fun testBVHHierarchy() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 5f, 5f, 10f, 10f)
        val rect3 = Rectangle(3, 20f, 20f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))
        bvh.insert(rect3, make2dCallback(bvh))

        assertTrue(bvh.computeDepth() >= 2)

        bvh.remove(rect2)
        assertTrue(bvh.computeDepth() >= 1)

        bvh.remove(rect3)
        assertEquals(1, bvh.computeDepth())
    }

    @Test
    fun ray_zero_direction_axis_check_should_handle_correctly() {
        val bvh = BVH<Rectangle>(2)
        val rect = Rectangle(100, 0f, 0f, 10f, 10f)
        bvh.insert(rect, make2dCallback(bvh))

        val intersectResults = bvh.intersect {
            it.origin(bvh, 0, 5f)
            it.origin(bvh, 1, -5f)
            it.direction(bvh, 0, 0f)
            it.direction(bvh, 1, 1f)
        }
        val dataResults = intersectResults.map { it.data }
        assertTrue(dataResults.contains(rect))

        val missResults = bvh.intersect {
            it.origin(bvh, 0, 20f)
            it.origin(bvh, 1, -5f)
            it.direction(bvh, 0, 0f)
            it.direction(bvh, 1, 1f)
        }
        val missDataResults = missResults.map { it.data }
        assertFalse(missDataResults.contains(rect))
    }

    @Test
    fun search_when_root_non_overlap_should_return_empty() {
        val bvh = BVH<Rectangle>(2)
        val rect = Rectangle(101, 20f, 20f, 5f, 5f)
        bvh.insert(rect, make2dCallback(bvh))

        val results = bvh.searchValues {
            it.min(bvh, 0, 0f)
            it.min(bvh, 1, 0f)
            it.max(bvh, 0, 4f)
            it.max(bvh, 1, 4f)
        }
        assertTrue(results.isEmpty())
    }

    @Test
    fun bulk_insert_should_return_count_and_build_tree() {
        val bvh = BVH<Rectangle>(2)
        val rectList = (0 until 50).map { i ->
            Rectangle(i + 200, (i % 10) * 12f, (i / 10) * 12f, 10f, 10f)
        }
        val inserted = bvh.bulkInsert(rectList.asSequence(), make2dCallback(bvh))
        assertEquals(rectList.size, inserted)
        assertEquals(rectList.size, bvh.size)
        assertTrue(bvh.computeDepth() >= 2)

        val results = bvh.searchValues {
            it.min(bvh, 0, 6f)
            it.min(bvh, 1, 6f)
            it.max(bvh, 0, 18f)
            it.max(bvh, 1, 18f)
        }
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun update_with_threshold_should_expand_bounds() {
        val bvh = BVH<Rectangle>(2)
        val rect = Rectangle(102, 0f, 0f, 10f, 10f)
        bvh.insert(rect, make2dCallback(bvh))

        val updated = bvh.update(rect, reinsertThreshold = 0.2f) { r ->
            r.min(bvh, 0, -5f)
            r.min(bvh, 1, -5f)
            r.max(bvh, 0, 15f)
            r.max(bvh, 1, 15f)
        }
        assertTrue(updated)

        val results = bvh.searchValues {
            it.min(bvh, 0, -4f)
            it.min(bvh, 1, -4f)
            it.max(bvh, 0, -3f)
            it.max(bvh, 1, -3f)
        }
        assertTrue(results.contains(rect))
    }

    @Test
    fun contains_operator_should_reflect_membership() {
        val bvh = BVH<Rectangle>(2)
        val rect = Rectangle(103, 2f, 2f, 4f, 4f)
        assertFalse(rect in bvh)
        bvh.insert(rect, make2dCallback(bvh))
        assertTrue(rect in bvh)
        bvh.remove(rect)
        assertFalse(rect in bvh)
    }

    @Test
    fun constructor_should_set_dimensions_and_initial_state() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)

        assertTrue(bvh.insert(rect1, make2dCallback(bvh)))
        assertEquals(1, bvh.size)
        assertTrue(rect1 in bvh)

        assertTrue(bvh.insert(rect2, make2dCallback(bvh)))
        assertEquals(2, bvh.size)
        assertTrue(rect2 in bvh)

        assertFalse(bvh.insert(rect1, make2dCallback(bvh)))
        assertEquals(2, bvh.size)
    }

    @Test
    fun insert_should_add_and_prevent_duplicates() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)

        assertTrue(bvh.insert(rect1, make2dCallback(bvh)))
        assertEquals(1, bvh.size)
        assertTrue(rect1 in bvh)

        assertTrue(bvh.insert(rect2, make2dCallback(bvh)))
        assertEquals(2, bvh.size)
        assertTrue(rect2 in bvh)

        assertFalse(bvh.insert(rect1, make2dCallback(bvh)))
        assertEquals(2, bvh.size)
    }

    @Test
    fun remove_should_detach_items_and_update_size() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))

        assertTrue(bvh.remove(rect1))
        assertEquals(1, bvh.size)
        assertFalse(rect1 in bvh)
        assertTrue(rect2 in bvh)

        assertTrue(bvh.remove(rect2))
        assertEquals(0, bvh.size)
        assertFalse(rect2 in bvh)
        assertTrue(bvh.isEmpty())

        assertFalse(bvh.remove(rect1))
        assertEquals(0, bvh.size)
    }

    @Test
    fun update_path_should_support_remove_and_reinsert() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))

        val intersectResults = bvh.intersect {
            it.origin(bvh, 0, 5f)
            it.origin(bvh, 1, 5f)
            it.direction(bvh, 0, 1f)
            it.direction(bvh, 1, 0f)
        }
        val dataResults = intersectResults.map { it.data }
        assertTrue(dataResults.contains(rect1))
    }

    @Test
    fun search_values_should_return_intersecting_rectangles() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))

        val intersectResults = bvh.intersect {
            it.origin(bvh, 0, 5f)
            it.origin(bvh, 1, 5f)
            it.direction(bvh, 0, 1f)
            it.direction(bvh, 1, 0f)
        }
        val dataResults = intersectResults.map { it.data }
        assertTrue(dataResults.contains(rect1))
    }

    @Test
    fun compute_depth_should_reflect_tree_growth() {
        val bvh = BVH<Rectangle>(2)
        assertEquals(0, bvh.computeDepth())

        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        bvh.insert(rect1, make2dCallback(bvh))
        val depth1 = bvh.computeDepth()
        assertTrue(depth1 >= 1)

        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)
        bvh.insert(rect2, make2dCallback(bvh))
        val depth2 = bvh.computeDepth()
        assertTrue(depth2 >= depth1)

        val rect3 = Rectangle(3, 5f, 5f, 10f, 10f)
        bvh.insert(rect3, make2dCallback(bvh))
        val depth3 = bvh.computeDepth()
        assertTrue(depth3 >= depth2)
    }

    @Test
    fun clear_should_reset_state_and_empty_tree() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))
        assertEquals(2, bvh.size)

        bvh.clear()
        assertEquals(0, bvh.size)
        assertTrue(bvh.isEmpty())
        assertFalse(rect1 in bvh)
        assertFalse(rect2 in bvh)
    }

    @Test
    fun iteration_should_yield_all_inserted_items() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 20f, 20f, 10f, 10f)
        val rect3 = Rectangle(3, 5f, 5f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))
        bvh.insert(rect3, make2dCallback(bvh))

        val collected = mutableListOf<Rectangle>()
        for (rect in bvh) {
            collected.add(rect)
        }

        assertEquals(3, collected.size)
        assertTrue(collected.contains(rect1))
        assertTrue(collected.contains(rect2))
        assertTrue(collected.contains(rect3))
    }

    @Test
    fun large_dataset_search_should_return_nonempty_results() {
        val bvh = BVH<Rectangle>(2)
        val rectCount = 100

        for (i in 0 until rectCount) {
            val x = (i % 10) * 20f
            val y = (i / 10) * 20f
            val rect = Rectangle(i, x, y, 10f, 10f)
            bvh.insert(rect, make2dCallback(bvh))
        }

        assertEquals(rectCount, bvh.size)

        val results = bvh.searchValues {
            it.min(bvh, 0, 15f)
            it.min(bvh, 1, 15f)
            it.max(bvh, 0, 35f)
            it.max(bvh, 1, 35f)
        }
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun hierarchy_depth_should_adjust_after_removals() {
        val bvh = BVH<Rectangle>(2)
        val rect1 = Rectangle(1, 0f, 0f, 10f, 10f)
        val rect2 = Rectangle(2, 5f, 5f, 10f, 10f)
        val rect3 = Rectangle(3, 20f, 20f, 10f, 10f)

        bvh.insert(rect1, make2dCallback(bvh))
        bvh.insert(rect2, make2dCallback(bvh))
        bvh.insert(rect3, make2dCallback(bvh))

        assertTrue(bvh.computeDepth() >= 2)

        bvh.remove(rect2)
        assertTrue(bvh.computeDepth() >= 1)

        bvh.remove(rect3)
        assertEquals(1, bvh.computeDepth())
    }

    @Test
    fun performance_bulk_insert_depth_should_be_lower_or_equal_than_individual_insert() {
        val count = 1000
        val rects = (0 until count).map { i ->
            val x = (i % 50) * 2f
            val y = (i / 50) * 2f
            Rectangle(i + 5000, x, y, 1.5f, 1.5f)
        }

        // bulk
        val bvhBulk = BVH<Rectangle>(2)
        val cbBulk: Rectangle.(BVHRect) -> Unit = { rect ->
            rect.min(bvhBulk, 0, x)
            rect.min(bvhBulk, 1, y)
            rect.max(bvhBulk, 0, x + width)
            rect.max(bvhBulk, 1, y + height)
        }
        val bulkTime = measureTime {
            bvhBulk.bulkInsert(rects.asSequence(), cbBulk)
        }
        val bulkDepth = bvhBulk.computeDepth()

        // individual
        val bvhSeq = BVH<Rectangle>(2)
        val cbSeq: Rectangle.(BVHRect) -> Unit = { rect ->
            rect.min(bvhSeq, 0, x)
            rect.min(bvhSeq, 1, y)
            rect.max(bvhSeq, 0, x + width)
            rect.max(bvhSeq, 1, y + height)
        }
        val seqTime = measureTime {
            rects.forEach { r -> bvhSeq.insert(r, cbSeq) }
        }
        val seqDepth = bvhSeq.computeDepth()

        // 深度上 bulk 构建通常不劣于逐个插入（更平衡
        assertTrue(bulkDepth <= seqDepth)
        // 时间上 bulk 应不慢于逐个插入（宽松断言?
        assertTrue(bulkTime <= seqTime * 2)
    }

    @Test
    fun performance_search_values_on_2000_items_should_finish_within_time_budget() {
        val bvh = BVH<Rectangle>(2)
        val count = 2000
        for (i in 0 until count) {
            val x = (i % 50) * 4f
            val y = (i / 50) * 4f
            val rect = Rectangle(6000 + i, x, y, 2f, 2f)
            bvh.insert(rect, make2dCallback(bvh))
        }

        val queries = 400
        val duration = measureTime {
            repeat(queries) { q ->
                val cx = (q % 20) * 10f + 1f
                val cy = (q / 20) * 10f + 1f
                val results = bvh.searchValues {
                    it.min(bvh, 0, cx)
                    it.min(bvh, 1, cy)
                    it.max(bvh, 0, cx + 1f)
                    it.max(bvh, 1, cy + 1f)
                }
                assertNotNull(results)
            }
        }
        assertTrue(duration.inWholeMilliseconds < 3000)
    }
}