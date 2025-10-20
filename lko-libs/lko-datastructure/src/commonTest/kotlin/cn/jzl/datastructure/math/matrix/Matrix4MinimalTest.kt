package cn.jzl.datastructure.math.matrix

import kotlin.test.Test

class Matrix4MinimalTest {
    @Test
    fun testMatrix4Creation() {
        // 只测试基本创建，不做任何断言
        val matrix = Matrix4.identity()
        println("Matrix4 created successfully: ${matrix}")
    }
}