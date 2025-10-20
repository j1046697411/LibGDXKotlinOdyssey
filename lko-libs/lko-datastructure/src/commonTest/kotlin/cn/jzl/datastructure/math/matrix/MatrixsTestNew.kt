package cn.jzl.datastructure.math.matrix

import kotlin.test.Test
import kotlin.test.assertEquals

private const val EPS = 1e-5f

class MatrixsTestNew {
    @Test
    fun testMatrix4BasicOps() {
        val id = Matrix4.identity()
        val z = Matrix4.zero()
        
        // 使用属性访问器而不是get方法
        assertEquals(1f, id.v00, EPS)
        assertEquals(1f, id.v11, EPS)
        assertEquals(1f, id.v22, EPS)
        assertEquals(1f, id.v33, EPS)
        assertEquals(0f, z.v00, EPS)
        assertEquals(0f, z.v32, EPS)
        
        // 测试平移和缩放
        val t = Matrix4.identity().translation(1f, 2f, 3f)
        assertEquals(1f, t.v03, EPS)
        assertEquals(2f, t.v13, EPS)
        assertEquals(3f, t.v23, EPS)
        
        val s = Matrix4.scaling(2f, 3f, 4f)  // 使用静态方法
        assertEquals(2f, s.v00, EPS)
        assertEquals(3f, s.v11, EPS)
        assertEquals(4f, s.v22, EPS)
        
        // 测试乘法
        val ts = t * s
        // 平移乘以缩放应该保持平移不变
        assertEquals(1f, ts.v03, EPS)
        assertEquals(2f, ts.v13, EPS)
        assertEquals(3f, ts.v23, EPS)
        
        // 缩放应该被应用
        assertEquals(2f, ts.v00, EPS)
        assertEquals(3f, ts.v11, EPS)
        assertEquals(4f, ts.v22, EPS)
        
        // 测试矩阵求逆
        val back = s.invert()
        assertEquals(0.5f, back.v00, EPS)
        assertEquals(1/3f, back.v11, EPS)
        assertEquals(0.25f, back.v22, EPS)
        
        // 测试加减法恒等
        val tp = (ts + id) - id
        assertEquals(ts.v22, tp.v22, EPS)
        
        // 测试转置
        val tt = t.transpose()
        assertEquals(t.v03, tt.v30, EPS)
    }
}