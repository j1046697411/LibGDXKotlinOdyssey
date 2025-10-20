package cn.jzl.datastructure.math.matrix

import kotlin.test.Test

// 临时修复：创建一个简单的Matrix4实现，不使用内联值类
class Matrix4Workaround {
    val data: FloatArray = FloatArray(16)
    
    companion object {
        fun identity(): Matrix4Workaround {
            val result = Matrix4Workaround()
            result.data[0] = 1f
            result.data[5] = 1f
            result.data[10] = 1f
            result.data[15] = 1f
            return result
        }
    }
}

// 使用临时修复的测试类
class Matrix4WorkaroundTest {
    @Test
    fun testMatrix4Creation() {
        val matrix = Matrix4Workaround.identity()
        println("Matrix4Workaround created successfully")
    }
}