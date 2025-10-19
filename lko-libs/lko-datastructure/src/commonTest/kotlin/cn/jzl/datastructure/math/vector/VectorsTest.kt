package cn.jzl.datastructure.math.vector

import cn.jzl.datastructure.math.Angle
import cn.jzl.datastructure.math.HALF_RATIO
import cn.jzl.datastructure.math.Ratio
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val EPS = 1e-5f

class VectorsTest {
    // ----- Vector2 -----
    @Test
    fun vector2_properties_and_ops() {
        val v = Vector2(3f, 4f)
        assertEquals(5f, v.length, EPS)
        assertEquals(25f, v.lengthSquared, EPS)
        val n = v.normalized
        assertTrue(n.nearEquals(Vector2(0.6f, 0.8f), EPS))

        // angle 返回 Angle 类型，比较其弧度值
        assertEquals(atan2(4f, 3f), v.angle.radians, EPS)
        assertTrue(v.perpendicular.nearEquals(Vector2(-4f, 3f), EPS))

        val u = Vector2(1f, 2f)
        assertEquals(11f, v.dot(u), EPS)
        assertEquals(2f, v.cross(u), EPS) // 2D cross scalar

        assertEquals(5f, v.distanceTo(Vector2(0f, 0f)), EPS)

        // lerp 接受 Ratio
        val lerp = v.lerp(Vector2(5f, 6f), HALF_RATIO)
        assertTrue(lerp.nearEquals(Vector2(4f, 5f), EPS))

        // rotate 接受 Angle
        val rot = Vector2(1f, 0f).rotate(Angle.HALF_PI)
        assertTrue(rot.nearEquals(Vector2(0f, 1f), 1e-4f))

        val proj = v.project(Vector2(2f, 0f)) // dot=6, denom=4 => scale=1.5 => (3,0)
        assertTrue(proj.nearEquals(Vector2(3f, 0f), EPS))

        val refl = Vector2(1f, -1f).reflect(Vector2(0f, 1f)) // reflect across Y+ normal
        assertTrue(refl.nearEquals(Vector2(1f, 1f), EPS))

        val clampedComp = v.coerceIn(Vector2(2f, 3f), Vector2(4f, 5f))
        assertTrue(clampedComp.nearEquals(Vector2(3f, 4f), EPS))
        val clamped = Vector2(10f, -10f).clamp(-2f, 2f)
        assertTrue(clamped.nearEquals(Vector2(2f, -2f), EPS))

        assertTrue(Vector2(0.000001f, -0.000001f).isZero(1e-5f))
        assertTrue(Vector2(3f, 2f).min(Vector2(5f, 1f)).nearEquals(Vector2(3f, 1f), EPS))
        assertTrue(Vector2(3f, 2f).max(Vector2(5f, 1f)).nearEquals(Vector2(5f, 2f), EPS))
    }

    @Test
    fun vector2_angleTo_and_project_zero() {
        // angleTo 返回 Angle 类型
        val a = Vector2(1f, 0f).angleTo(Vector2(0f, 1f))
        assertEquals(PI.toFloat() / 2f, a.radians, EPS)
        // project 到零向量应返回零
        val p0 = Vector2(1f, 1f).project(Vector2(0f, 0f))
        assertTrue(p0.nearEquals(Vector2(0f, 0f), EPS))
    }

    // ----- Vector3 -----
    @Test
    fun vector3_properties_and_ops() {
        val v = Vector3(1f, 2f, 2f)
        assertEquals(3f, v.length, EPS)
        assertEquals(9f, v.lengthSquared, EPS)
        assertTrue(v.normalized.nearEquals(Vector3(1f/3f, 2f/3f, 2f/3f), 1e-5f))

        val u = Vector3(0f, 1f, 0f)
        val cross = v.cross(u)
        assertTrue(cross.nearEquals(Vector3(-2f, 0f, 1f), EPS))
        assertEquals(2f, v.dot(u), EPS)

        // angleTo 返回 Angle 类型
        val angle = Vector3(1f, 0f, 0f).angleTo(Vector3(0f, 1f, 0f))
        assertEquals(PI.toFloat() / 2f, angle.radians, 1e-5f)

        val dist = Vector3(1f, 1f, 1f).distanceTo(Vector3(2f, 3f, 6f))
        assertEquals(sqrt(1f + 4f + 25f), dist, 1e-5f)

        val proj = Vector3(3f, 4f, 0f).project(Vector3(2f, 0f, 0f)) // dot=6, denom=4 => (3,0,0)
        assertTrue(proj.nearEquals(Vector3(3f, 0f, 0f), EPS))

        val refl = Vector3(0f, -1f, 0f).reflect(Vector3(0f, 1f, 0f))
        assertTrue(refl.nearEquals(Vector3(0f, 1f, 0f), EPS))

        val clampedComp = Vector3(3f, 4f, 5f).coerceIn(Vector3(2f, 2f, 2f), Vector3(4f, 4f, 4f))
        assertTrue(clampedComp.nearEquals(Vector3(3f, 4f, 4f), EPS))
        val clamped = Vector3(10f, -10f, 0f).clamp(-2f, 2f)
        assertTrue(clamped.nearEquals(Vector3(2f, -2f, 0f), EPS))

        assertTrue(Vector3(0f, 0f, 0f).isZero())
        assertTrue(Vector3(3f, 2f, 9f).min(Vector3(5f, 1f, 10f)).nearEquals(Vector3(3f, 1f, 9f), EPS))
        assertTrue(Vector3(3f, 2f, 9f).max(Vector3(5f, 1f, 10f)).nearEquals(Vector3(5f, 2f, 10f), EPS))
    }

    // ----- Vector4 -----
    @Test
    fun vector4_properties_and_ops() {
        val v = Vector4(1f, 2f, 2f, 1f)
        assertEquals(sqrt(1f+4f+4f+1f), v.length, EPS)
        assertEquals(1f+4f+4f+1f, v.lengthSquared, EPS)
        val len = v.length
        assertTrue(v.normalized.nearEquals(Vector4(1f/len, 2f/len, 2f/len, 1f/len), 1e-5f))

        val dot = v.dot(Vector4(1f, 0f, 1f, 0f))
        assertEquals(3f, dot, EPS)

        val clampedComp = v.coerceIn(Vector4(0.5f, 1.5f, 1.5f, 0.5f), Vector4(1.5f, 2.5f, 2.5f, 1.5f))
        assertTrue(clampedComp.nearEquals(Vector4(1f, 2f, 2f, 1f), EPS))
        val clamped = Vector4(10f, -10f, 0f, 3f).clamp(-2f, 2f)
        assertTrue(clamped.nearEquals(Vector4(2f, -2f, 0f, 2f), EPS))

        assertTrue(Vector4(0f, 0f, 0f, 0f).isZero())
        assertTrue(Vector4(3f, 2f, 9f, 1f).min(Vector4(5f, 1f, 10f, 0f)).nearEquals(Vector4(3f, 1f, 9f, 0f), EPS))
        assertTrue(Vector4(3f, 2f, 9f, 1f).max(Vector4(5f, 1f, 10f, 0f)).nearEquals(Vector4(5f, 2f, 10f, 1f), EPS))
    }

    @Test
    fun vector4_lerp_ratio() {
        val a = Vector4(0f, 0f, 0f, 0f)
        val b = Vector4(4f, 8f, 12f, 16f)
        val r = Ratio(0.25f)
        val m = a.lerp(b, r)
        assertTrue(m.nearEquals(Vector4(1f, 2f, 3f, 4f), EPS))
    }

    // ----- IntVector -----
    @Test
    fun int_vector_properties_and_ops() {
        val i2 = IntVector2(3,4)
        assertEquals(5f, i2.length, EPS)
        assertEquals(25, i2.lengthSquared)
        assertEquals(5f, i2.distanceTo(IntVector2(0,0)), EPS)

        val i3 = IntVector3(1,2,2)
        assertEquals(3f, i3.length, EPS)
        assertEquals(9, i3.lengthSquared)
        assertEquals(1f, i3.distanceTo(IntVector3(2,2,2)), EPS)

        val i4 = IntVector4(1,2,2,1)
        assertEquals(sqrt(1f+4f+4f+1f), i4.length, EPS)
        assertEquals(1+4+4+1, i4.lengthSquared)
        assertEquals(sqrt(2f), i4.distanceTo(IntVector4(2,2,2,2)), EPS)

        val c2 = i2.coerceIn(IntVector2(2,2), IntVector2(4,4))
        assertEquals(IntVector2(3,4), c2)
        val c3 = i3.coerceIn(IntVector3(0,1,3), IntVector3(2,3,3))
        assertEquals(IntVector3(1,2,3), c3)

        assertEquals(IntVector2(3,1), IntVector2(3,2).min(IntVector2(5,1)))
        assertEquals(IntVector2(5,2), IntVector2(3,2).max(IntVector2(5,1)))

        // convert to float vectors
        assertTrue(i2.toVector2().nearEquals(Vector2(3f,4f), EPS))
        assertTrue(i3.toVector3().nearEquals(Vector3(1f,2f,2f), EPS))
        assertTrue(i4.toVector4().nearEquals(Vector4(1f,2f,2f,1f), EPS))
    }

    @Test
    fun int_vector_lerp_ratio() {
        val a = IntVector2(0, 0)
        val b = IntVector2(10, 10)
        val r = Ratio(0.25f)
        // floor rounding on integer lerp
        assertEquals(IntVector2(2, 2), a.lerp(b, r))
    }
}