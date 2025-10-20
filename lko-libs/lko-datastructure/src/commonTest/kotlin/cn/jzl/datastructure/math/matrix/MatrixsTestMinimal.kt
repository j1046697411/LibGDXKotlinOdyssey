package cn.jzl.datastructure.math.matrix

import cn.jzl.datastructure.math.vector.Vector3
import kotlin.test.Test
import kotlin.test.assertEquals

private const val EPS = 1e-5f

class MatrixsTestMinimal {
    @Test
    fun testBasicMatrixOperations() {
        val id = Matrix4.identity()
        assertEquals(1f, id[0, 0], EPS)
        assertEquals(1f, id[1, 1], EPS)
        assertEquals(1f, id[2, 2], EPS)
        assertEquals(1f, id[3, 3], EPS)
        
        val t = Matrix4.identity().translation(1f, 2f, 3f)
        assertEquals(1f, t.v00, EPS)
        assertEquals(2f, t.v31, EPS)
        
        val v = Vector3(0f, 0f, 0f)
        val tv = t.transform(v)
        assertEquals(1f, tv.x, EPS)
        assertEquals(2f, tv.y, EPS)
        assertEquals(3f, tv.z, EPS)
    }
}