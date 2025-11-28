package cn.jzl.ecs

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.measureTime

class ArchetypeServiceTest {

    data class TestComponent(val value: Float)
    data class AnotherComponent(val name: String)
    data class ThirdComponent(val count: Int)

    // 测试原型性能
    @Test
    fun testArchetypePerformance() {
        val world = world { }
        
        // 批量创建实体，测试原型创建性能
        val duration = measureTime {
            for (i in 0 until 1000) {
                world.entity {
                    it.addComponent(TestComponent(i.toFloat()))
                    if (i % 2 == 0) {
                        it.addComponent(AnotherComponent("Even$i"))
                    }
                    if (i % 3 == 0) {
                        it.addComponent(ThirdComponent(i))
                    }
                }
            }
        }
        
        // 验证性能在可接受范围内（1000实体创建应在1秒内完成）
        assertTrue(duration.inWholeMilliseconds < 1000, "1000 entity creation should take less than 1 second, took ${duration.inWholeMilliseconds} ms")
    }
}
