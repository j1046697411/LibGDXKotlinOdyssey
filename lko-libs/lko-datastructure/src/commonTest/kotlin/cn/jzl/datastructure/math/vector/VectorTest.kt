package cn.jzl.datastructure.math.vector

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VectorTest {
    // 测试Vector2构造函数和基本属性
    @Test
    fun testVector2ConstructorAndProperties() {
        val vector = Vector2(1.5f, 2.5f)
        assertEquals(1.5f, vector.x)
        assertEquals(2.5f, vector.y)
        assertEquals(2, vector.dimensions)
        assertEquals(1.5f, vector[0])
        assertEquals(2.5f, vector[1])
    }

    // 测试Vector3构造函数和基本属性
    @Test
    fun testVector3ConstructorAndProperties() {
        val vector = Vector3(1.5f, 2.5f, 3.5f)
        assertEquals(1.5f, vector.x)
        assertEquals(2.5f, vector.y)
        assertEquals(3.5f, vector.z)
        assertEquals(3, vector.dimensions)
        assertEquals(1.5f, vector[0])
        assertEquals(2.5f, vector[1])
        assertEquals(3.5f, vector[2])
    }

    // 测试Vector4构造函数和基本属性
    @Test
    fun testVector4ConstructorAndProperties() {
        val vector = Vector4(1.5f, 2.5f, 3.5f, 4.5f)
        assertEquals(1.5f, vector.x)
        assertEquals(2.5f, vector.y)
        assertEquals(3.5f, vector.z)
        assertEquals(4.5f, vector.w)
        assertEquals(4, vector.dimensions)
        assertEquals(1.5f, vector[0])
        assertEquals(2.5f, vector[1])
        assertEquals(3.5f, vector[2])
        assertEquals(4.5f, vector[3])
    }

    // 测试IntVector2构造函数和基本属性
    @Test
    fun testIntVector2ConstructorAndProperties() {
        val vector = IntVector2(1, 2)
        assertEquals(1, vector.x)
        assertEquals(2, vector.y)
        assertEquals(2, vector.dimensions)
        assertEquals(1, vector[0])
        assertEquals(2, vector[1])
    }

    // 测试IntVector3构造函数和基本属性
    @Test
    fun testIntVector3ConstructorAndProperties() {
        val vector = IntVector3(1, 2, 3)
        assertEquals(1, vector.x)
        assertEquals(2, vector.y)
        assertEquals(3, vector.z)
        assertEquals(3, vector.dimensions)
        assertEquals(1, vector[0])
        assertEquals(2, vector[1])
        assertEquals(3, vector[2])
    }

    // 测试IntVector4构造函数和基本属性
    @Test
    fun testIntVector4ConstructorAndProperties() {
        val vector = IntVector4(1, 2, 3, 4)
        assertEquals(1, vector.x)
        assertEquals(2, vector.y)
        assertEquals(3, vector.z)
        assertEquals(4, vector.w)
        assertEquals(4, vector.dimensions)
        assertEquals(1, vector[0])
        assertEquals(2, vector[1])
        assertEquals(3, vector[2])
        assertEquals(4, vector[3])
    }

    // 测试Vector2操作符重载
    @Test
    fun testVector2Operators() {
        val v1 = Vector2(1.5f, 2.5f)
        val v2 = Vector2(3.0f, 4.0f)
        val scalar = 2.0f

        // 向量加法
        val sum = v1 + v2
        assertEquals(4.5f, sum.x)
        assertEquals(6.5f, sum.y)

        // 向量与标量加法
        val sumScalar = v1 + scalar
        assertEquals(3.5f, sumScalar.x)
        assertEquals(4.5f, sumScalar.y)

        // 标量与向量加法
        val scalarSum = scalar + v1
        assertEquals(3.5f, scalarSum.x)
        assertEquals(4.5f, scalarSum.y)

        // 向量减法
        val diff = v2 - v1
        assertEquals(1.5f, diff.x)
        assertEquals(1.5f, diff.y)

        // 向量与标量减法
        val diffScalar = v2 - scalar
        assertEquals(1.0f, diffScalar.x)
        assertEquals(2.0f, diffScalar.y)

        // 标量与向量减法
        val scalarDiff = scalar - v1
        assertEquals(0.5f, scalarDiff.x)
        assertEquals(-0.5f, scalarDiff.y)

        // 向量乘法
        val product = v1 * v2
        assertEquals(4.5f, product.x)
        assertEquals(10.0f, product.y)

        // 向量与标量乘法
        val productScalar = v1 * scalar
        assertEquals(3.0f, productScalar.x)
        assertEquals(5.0f, productScalar.y)

        // 标量与向量乘法
        val scalarProduct = scalar * v1
        assertEquals(3.0f, scalarProduct.x)
        assertEquals(5.0f, scalarProduct.y)

        // 向量除法
        val quotient = v2 / v1
        assertEquals(2.0f, quotient.x)
        assertEquals(1.6f, quotient.y, 0.0001f)

        // 向量与标量除法
        val quotientScalar = v2 / scalar
        assertEquals(1.5f, quotientScalar.x)
        assertEquals(2.0f, quotientScalar.y)

        // 标量与向量除法
        val scalarQuotient = scalar / v1
        assertEquals(1.3333f, scalarQuotient.x, 0.0001f)
        assertEquals(0.8f, scalarQuotient.y)

        // 向量求余
        val remainder = Vector2(5.5f, 7.5f) % Vector2(2.0f, 3.0f)
        assertEquals(1.5f, remainder.x)
        assertEquals(1.5f, remainder.y)

        // 向量与标量求余
        val remainderScalar = Vector2(5.5f, 7.5f) % scalar
        assertEquals(1.5f, remainderScalar.x)
        assertEquals(1.5f, remainderScalar.y)

        // 一元负号
        val neg = -v1
        assertEquals(-1.5f, neg.x)
        assertEquals(-2.5f, neg.y)

        // 一元正号
        val pos = +v1
        assertEquals(1.5f, pos.x)
        assertEquals(2.5f, pos.y)
    }

    // 测试IntVector2操作符重载
    @Test
    fun testIntVector2Operators() {
        val v1 = IntVector2(1, 2)
        val v2 = IntVector2(3, 4)
        val scalar = 2

        // 向量加法
        val sum = v1 + v2
        assertEquals(4, sum.x)
        assertEquals(6, sum.y)

        // 向量与标量加法
        val sumScalar = v1 + scalar
        assertEquals(3, sumScalar.x)
        assertEquals(4, sumScalar.y)

        // 标量与向量加法
        val scalarSum = scalar + v1
        assertEquals(3, scalarSum.x)
        assertEquals(4, scalarSum.y)

        // 向量减法
        val diff = v2 - v1
        assertEquals(2, diff.x)
        assertEquals(2, diff.y)

        // 向量与标量减法
        val diffScalar = v2 - scalar
        assertEquals(1, diffScalar.x)
        assertEquals(2, diffScalar.y)

        // 标量与向量减法
        val scalarDiff = scalar - v1
        assertEquals(1, scalarDiff.x)
        assertEquals(0, scalarDiff.y)

        // 向量乘法
        val product = v1 * v2
        assertEquals(3, product.x)
        assertEquals(8, product.y)

        // 向量与标量乘法
        val productScalar = v1 * scalar
        assertEquals(2, productScalar.x)
        assertEquals(4, productScalar.y)

        // 标量与向量乘法
        val scalarProduct = scalar * v1
        assertEquals(2, scalarProduct.x)
        assertEquals(4, scalarProduct.y)

        // 向量除法
        val quotient = IntVector2(6, 8) / IntVector2(3, 4)
        assertEquals(2, quotient.x)
        assertEquals(2, quotient.y)

        // 向量与标量除法
        val quotientScalar = IntVector2(6, 8) / scalar
        assertEquals(3, quotientScalar.x)
        assertEquals(4, quotientScalar.y)

        // 标量与向量除法
        val scalarQuotient = scalar / IntVector2(1, 2)
        assertEquals(2, scalarQuotient.x)
        assertEquals(1, scalarQuotient.y)

        // 向量求余
        val remainder = IntVector2(5, 7) % IntVector2(2, 3)
        assertEquals(1, remainder.x)
        assertEquals(1, remainder.y)

        // 向量与标量求余
        val remainderScalar = IntVector2(5, 7) % scalar
        assertEquals(1, remainderScalar.x)
        assertEquals(1, remainderScalar.y)

        // 一元负号
        val neg = -v1
        assertEquals(-1, neg.x)
        assertEquals(-2, neg.y)

        // 一元正号
        val pos = +v1
        assertEquals(1, pos.x)
        assertEquals(2, pos.y)
    }

    // 测试Vector3操作符重载
    @Test
    fun testVector3Operators() {
        val v1 = Vector3(1.5f, 2.5f, 3.5f)
        val v2 = Vector3(3.0f, 4.0f, 5.0f)
        val scalar = 2.0f

        // 向量加法
        val sum = v1 + v2
        assertEquals(4.5f, sum.x)
        assertEquals(6.5f, sum.y)
        assertEquals(8.5f, sum.z)

        // 向量与标量加法
        val sumScalar = v1 + scalar
        assertEquals(3.5f, sumScalar.x)
        assertEquals(4.5f, sumScalar.y)
        assertEquals(5.5f, sumScalar.z)

        // 标量与向量加法
        val scalarSum = scalar + v1
        assertEquals(3.5f, scalarSum.x)
        assertEquals(4.5f, scalarSum.y)
        assertEquals(5.5f, scalarSum.z)

        // 向量减法
        val diff = v2 - v1
        assertEquals(1.5f, diff.x)
        assertEquals(1.5f, diff.y)
        assertEquals(1.5f, diff.z)

        // 向量与标量减法
        val diffScalar = v2 - scalar
        assertEquals(1.0f, diffScalar.x)
        assertEquals(2.0f, diffScalar.y)
        assertEquals(3.0f, diffScalar.z)

        // 标量与向量减法
        val scalarDiff = scalar - v1
        assertEquals(0.5f, scalarDiff.x)
        assertEquals(-0.5f, scalarDiff.y)
        assertEquals(-1.5f, scalarDiff.z)

        // 向量乘法
        val product = v1 * v2
        assertEquals(4.5f, product.x)
        assertEquals(10.0f, product.y)
        assertEquals(17.5f, product.z)

        // 向量与标量乘法
        val productScalar = v1 * scalar
        assertEquals(3.0f, productScalar.x)
        assertEquals(5.0f, productScalar.y)
        assertEquals(7.0f, productScalar.z)

        // 标量与向量乘法
        val scalarProduct = scalar * v1
        assertEquals(3.0f, scalarProduct.x)
        assertEquals(5.0f, scalarProduct.y)
        assertEquals(7.0f, scalarProduct.z)

        // 向量除法
        val quotient = v2 / v1
        assertEquals(2.0f, quotient.x)
        assertEquals(1.6f, quotient.y, 0.0001f)
        assertEquals(1.4286f, quotient.z, 0.0001f)

        // 向量与标量除法
        val quotientScalar = v2 / scalar
        assertEquals(1.5f, quotientScalar.x)
        assertEquals(2.0f, quotientScalar.y)
        assertEquals(2.5f, quotientScalar.z)

        // 标量与向量除法
        val scalarQuotient = scalar / v1
        assertEquals(1.3333f, scalarQuotient.x, 0.0001f)
        assertEquals(0.8f, scalarQuotient.y)
        assertEquals(0.5714f, scalarQuotient.z, 0.0001f)

        // 向量求余
        val remainder = Vector3(5.5f, 7.5f, 9.5f) % Vector3(2.0f, 3.0f, 4.0f)
        assertEquals(1.5f, remainder.x)
        assertEquals(1.5f, remainder.y)
        assertEquals(1.5f, remainder.z)

        // 向量与标量求余
        val remainderScalar = Vector3(5.5f, 7.5f, 9.5f) % scalar
        assertEquals(1.5f, remainderScalar.x)
        assertEquals(1.5f, remainderScalar.y)
        assertEquals(1.5f, remainderScalar.z)

        // 一元负号
        val neg = -v1
        assertEquals(-1.5f, neg.x)
        assertEquals(-2.5f, neg.y)
        assertEquals(-3.5f, neg.z)

        // 一元正号
        val pos = +v1
        assertEquals(1.5f, pos.x)
        assertEquals(2.5f, pos.y)
        assertEquals(3.5f, pos.z)
    }

    // 测试Vector4操作符重载
    @Test
    fun testVector4Operators() {
        val v1 = Vector4(1.5f, 2.5f, 3.5f, 4.5f)
        val v2 = Vector4(3.0f, 4.0f, 5.0f, 6.0f)
        val scalar = 2.0f

        // 向量加法
        val sum = v1 + v2
        assertEquals(4.5f, sum.x)
        assertEquals(6.5f, sum.y)
        assertEquals(8.5f, sum.z)
        assertEquals(10.5f, sum.w)

        // 向量与标量加法
        val sumScalar = v1 + scalar
        assertEquals(3.5f, sumScalar.x)
        assertEquals(4.5f, sumScalar.y)
        assertEquals(5.5f, sumScalar.z)
        assertEquals(6.5f, sumScalar.w)

        // 标量与向量加法
        val scalarSum = scalar + v1
        assertEquals(3.5f, scalarSum.x)
        assertEquals(4.5f, scalarSum.y)
        assertEquals(5.5f, scalarSum.z)
        assertEquals(6.5f, scalarSum.w)

        // 向量减法
        val diff = v2 - v1
        assertEquals(1.5f, diff.x)
        assertEquals(1.5f, diff.y)
        assertEquals(1.5f, diff.z)
        assertEquals(1.5f, diff.w)

        // 向量与标量减法
        val diffScalar = v2 - scalar
        assertEquals(1.0f, diffScalar.x)
        assertEquals(2.0f, diffScalar.y)
        assertEquals(3.0f, diffScalar.z)
        assertEquals(4.0f, diffScalar.w)

        // 标量与向量减法
        val scalarDiff = scalar - v1
        assertEquals(0.5f, scalarDiff.x)
        assertEquals(-0.5f, scalarDiff.y)
        assertEquals(-1.5f, scalarDiff.z)
        assertEquals(-2.5f, scalarDiff.w)

        // 向量乘法
        val product = v1 * v2
        assertEquals(4.5f, product.x)
        assertEquals(10.0f, product.y)
        assertEquals(17.5f, product.z)
        assertEquals(27.0f, product.w)

        // 向量与标量乘法
        val productScalar = v1 * scalar
        assertEquals(3.0f, productScalar.x)
        assertEquals(5.0f, productScalar.y)
        assertEquals(7.0f, productScalar.z)
        assertEquals(9.0f, productScalar.w)

        // 标量与向量乘法
        val scalarProduct = scalar * v1
        assertEquals(3.0f, scalarProduct.x)
        assertEquals(5.0f, scalarProduct.y)
        assertEquals(7.0f, scalarProduct.z)
        assertEquals(9.0f, scalarProduct.w)

        // 向量除法
        val quotient = v2 / v1
        assertEquals(2.0f, quotient.x)
        assertEquals(1.6f, quotient.y, 0.0001f)
        assertEquals(1.4286f, quotient.z, 0.0001f)
        assertEquals(1.3333f, quotient.w, 0.0001f)

        // 向量与标量除法
        val quotientScalar = v2 / scalar
        assertEquals(1.5f, quotientScalar.x)
        assertEquals(2.0f, quotientScalar.y)
        assertEquals(2.5f, quotientScalar.z)
        assertEquals(3.0f, quotientScalar.w)

        // 标量与向量除法
        val scalarQuotient = scalar / v1
        assertEquals(1.3333f, scalarQuotient.x, 0.0001f)
        assertEquals(0.8f, scalarQuotient.y)
        assertEquals(0.5714f, scalarQuotient.z, 0.0001f)
        assertEquals(0.4444f, scalarQuotient.w, 0.0001f)

        // 向量求余
        val remainder = Vector4(5.5f, 7.5f, 9.5f, 11.5f) % Vector4(2.0f, 3.0f, 4.0f, 5.0f)
        assertEquals(1.5f, remainder.x)
        assertEquals(1.5f, remainder.y)
        assertEquals(1.5f, remainder.z)
        assertEquals(1.5f, remainder.w)

        // 向量与标量求余
        val remainderScalar = Vector4(5.5f, 7.5f, 9.5f, 11.5f) % scalar
        assertEquals(1.5f, remainderScalar.x)
        assertEquals(1.5f, remainderScalar.y)
        assertEquals(1.5f, remainderScalar.z)
        assertEquals(1.5f, remainderScalar.w)

        // 一元负号
        val neg = -v1
        assertEquals(-1.5f, neg.x)
        assertEquals(-2.5f, neg.y)
        assertEquals(-3.5f, neg.z)
        assertEquals(-4.5f, neg.w)

        // 一元正号
        val pos = +v1
        assertEquals(1.5f, pos.x)
        assertEquals(2.5f, pos.y)
        assertEquals(3.5f, pos.z)
        assertEquals(4.5f, pos.w)
    }

    // 测试IntVector3操作符重载
    @Test
    fun testIntVector3Operators() {
        val v1 = IntVector3(1, 2, 3)
        val v2 = IntVector3(3, 4, 5)
        val scalar = 2

        // 向量加法
        val sum = v1 + v2
        assertEquals(4, sum.x)
        assertEquals(6, sum.y)
        assertEquals(8, sum.z)

        // 向量与标量加法
        val sumScalar = v1 + scalar
        assertEquals(3, sumScalar.x)
        assertEquals(4, sumScalar.y)
        assertEquals(5, sumScalar.z)

        // 标量与向量加法
        val scalarSum = scalar + v1
        assertEquals(3, scalarSum.x)
        assertEquals(4, scalarSum.y)
        assertEquals(5, scalarSum.z)

        // 向量减法
        val diff = v2 - v1
        assertEquals(2, diff.x)
        assertEquals(2, diff.y)
        assertEquals(2, diff.z)

        // 向量与标量减法
        val diffScalar = v2 - scalar
        assertEquals(1, diffScalar.x)
        assertEquals(2, diffScalar.y)
        assertEquals(3, diffScalar.z)

        // 标量与向量减法
        val scalarDiff = scalar - v1
        assertEquals(1, scalarDiff.x)
        assertEquals(0, scalarDiff.y)
        assertEquals(-1, scalarDiff.z)

        // 向量乘法
        val product = v1 * v2
        assertEquals(3, product.x)
        assertEquals(8, product.y)
        assertEquals(15, product.z)

        // 向量与标量乘法
        val productScalar = v1 * scalar
        assertEquals(2, productScalar.x)
        assertEquals(4, productScalar.y)
        assertEquals(6, productScalar.z)

        // 标量与向量乘法
        val scalarProduct = scalar * v1
        assertEquals(2, scalarProduct.x)
        assertEquals(4, scalarProduct.y)
        assertEquals(6, scalarProduct.z)

        // 向量除法
        val quotient = IntVector3(6, 8, 10) / IntVector3(3, 4, 5)
        assertEquals(2, quotient.x)
        assertEquals(2, quotient.y)
        assertEquals(2, quotient.z)

        // 向量与标量除法
        val quotientScalar = IntVector3(6, 8, 10) / scalar
        assertEquals(3, quotientScalar.x)
        assertEquals(4, quotientScalar.y)
        assertEquals(5, quotientScalar.z)

        // 标量与向量除法
        val scalarQuotient = scalar / IntVector3(1, 2, 3)
        assertEquals(2, scalarQuotient.x)
        assertEquals(1, scalarQuotient.y)
        assertEquals(0, scalarQuotient.z)

        // 向量求余
        val remainder = IntVector3(5, 7, 9) % IntVector3(2, 3, 4)
        assertEquals(1, remainder.x)
        assertEquals(1, remainder.y)
        assertEquals(1, remainder.z)

        // 向量与标量求余
        val remainderScalar = IntVector3(5, 7, 9) % scalar
        assertEquals(1, remainderScalar.x)
        assertEquals(1, remainderScalar.y)
        assertEquals(1, remainderScalar.z)

        // 一元负号
        val neg = -v1
        assertEquals(-1, neg.x)
        assertEquals(-2, neg.y)
        assertEquals(-3, neg.z)

        // 一元正号
        val pos = +v1
        assertEquals(1, pos.x)
        assertEquals(2, pos.y)
        assertEquals(3, pos.z)
    }

    // 测试IntVector4操作符重载
    @Test
    fun testIntVector4Operators() {
        val v1 = IntVector4(1, 2, 3, 4)
        val v2 = IntVector4(3, 4, 5, 6)
        val scalar = 2

        // 向量加法
        val sum = v1 + v2
        assertEquals(4, sum.x)
        assertEquals(6, sum.y)
        assertEquals(8, sum.z)
        assertEquals(10, sum.w)

        // 向量与标量加法
        val sumScalar = v1 + scalar
        assertEquals(3, sumScalar.x)
        assertEquals(4, sumScalar.y)
        assertEquals(5, sumScalar.z)
        assertEquals(6, sumScalar.w)

        // 标量与向量加法
        val scalarSum = scalar + v1
        assertEquals(3, scalarSum.x)
        assertEquals(4, scalarSum.y)
        assertEquals(5, scalarSum.z)
        assertEquals(6, scalarSum.w)

        // 向量减法
        val diff = v2 - v1
        assertEquals(2, diff.x)
        assertEquals(2, diff.y)
        assertEquals(2, diff.z)
        assertEquals(2, diff.w)

        // 向量与标量减法
        val diffScalar = v2 - scalar
        assertEquals(1, diffScalar.x)
        assertEquals(2, diffScalar.y)
        assertEquals(3, diffScalar.z)
        assertEquals(4, diffScalar.w)

        // 标量与向量减法
        val scalarDiff = scalar - v1
        assertEquals(1, scalarDiff.x)
        assertEquals(0, scalarDiff.y)
        assertEquals(-1, scalarDiff.z)
        assertEquals(-2, scalarDiff.w)

        // 向量乘法
        val product = v1 * v2
        assertEquals(3, product.x)
        assertEquals(8, product.y)
        assertEquals(15, product.z)
        assertEquals(24, product.w)

        // 向量与标量乘法
        val productScalar = v1 * scalar
        assertEquals(2, productScalar.x)
        assertEquals(4, productScalar.y)
        assertEquals(6, productScalar.z)
        assertEquals(8, productScalar.w)

        // 标量与向量乘法
        val scalarProduct = scalar * v1
        assertEquals(2, scalarProduct.x)
        assertEquals(4, scalarProduct.y)
        assertEquals(6, scalarProduct.z)
        assertEquals(8, scalarProduct.w)

        // 向量除法
        val quotient = IntVector4(6, 8, 10, 12) / IntVector4(3, 4, 5, 6)
        assertEquals(2, quotient.x)
        assertEquals(2, quotient.y)
        assertEquals(2, quotient.z)
        assertEquals(2, quotient.w)

        // 向量与标量除法
        val quotientScalar = IntVector4(6, 8, 10, 12) / scalar
        assertEquals(3, quotientScalar.x)
        assertEquals(4, quotientScalar.y)
        assertEquals(5, quotientScalar.z)
        assertEquals(6, quotientScalar.w)

        // 标量与向量除法
        val scalarQuotient = scalar / IntVector4(1, 2, 3, 4)
        assertEquals(2, scalarQuotient.x)
        assertEquals(1, scalarQuotient.y)
        assertEquals(0, scalarQuotient.z)
        assertEquals(0, scalarQuotient.w)

        // 向量求余
        val remainder = IntVector4(5, 7, 9, 11) % IntVector4(2, 3, 4, 5)
        assertEquals(1, remainder.x)
        assertEquals(1, remainder.y)
        assertEquals(1, remainder.z)
        assertEquals(1, remainder.w)

        // 向量与标量求余
        val remainderScalar = IntVector4(5, 7, 9, 11) % scalar
        assertEquals(1, remainderScalar.x)
        assertEquals(1, remainderScalar.y)
        assertEquals(1, remainderScalar.z)
        assertEquals(1, remainderScalar.w)

        // 一元负号
        val neg = -v1
        assertEquals(-1, neg.x)
        assertEquals(-2, neg.y)
        assertEquals(-3, neg.z)
        assertEquals(-4, neg.w)

        // 一元正号
        val pos = +v1
        assertEquals(1, pos.x)
        assertEquals(2, pos.y)
        assertEquals(3, pos.z)
        assertEquals(4, pos.w)
    }

    // 测试维度越界检查
    @Test
    fun testDimensionOutOfBounds() {
        val vector2 = Vector2(1f, 2f)
        val vector3 = Vector3(1f, 2f, 3f)
        val vector4 = Vector4(1f, 2f, 3f, 4f)
        val intVector2 = IntVector2(1, 2)
        val intVector3 = IntVector3(1, 2, 3)
        val intVector4 = IntVector4(1, 2, 3, 4)

        // 测试Vector2
        assertFailsWith<IndexOutOfBoundsException> { vector2[-1] }
        assertFailsWith<IndexOutOfBoundsException> { vector2[2] }

        // 测试Vector3
        assertFailsWith<IndexOutOfBoundsException> { vector3[-1] }
        assertFailsWith<IndexOutOfBoundsException> { vector3[3] }

        // 测试Vector4
        assertFailsWith<IndexOutOfBoundsException> { vector4[-1] }
        assertFailsWith<IndexOutOfBoundsException> { vector4[4] }

        // 测试IntVector2
        assertFailsWith<IndexOutOfBoundsException> { intVector2[-1] }
        assertFailsWith<IndexOutOfBoundsException> { intVector2[2] }

        // 测试IntVector3
        assertFailsWith<IndexOutOfBoundsException> { intVector3[-1] }
        assertFailsWith<IndexOutOfBoundsException> { intVector3[3] }

        // 测试IntVector4
        assertFailsWith<IndexOutOfBoundsException> { intVector4[-1] }
        assertFailsWith<IndexOutOfBoundsException> { intVector4[4] }
    }

    // 测试Point类型别名
    @Test
    fun testPointAlias() {
        val point: Point = Vector2(1f, 2f)
        assertEquals(1f, point.x)
        assertEquals(2f, point.y)
        assertEquals(2, point.dimensions)
    }
}