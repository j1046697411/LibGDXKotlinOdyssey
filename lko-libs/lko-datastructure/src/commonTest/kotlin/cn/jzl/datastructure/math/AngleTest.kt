package cn.jzl.datastructure.math

import kotlin.test.*
import kotlin.math.PI

private const val EPS = 1e-5f

class AngleTest {
    @Test
    fun angle_constants() {
        assertEquals(0f, Angle.ZERO.radians, EPS)
        assertEquals(PI.toFloat(), Angle.PI.radians, EPS)
        assertEquals((PI / 2).toFloat(), Angle.HALF_PI.radians, EPS)
        assertEquals((2 * PI).toFloat(), Angle.TWO_PI.radians, EPS)
    }

    @Test
    fun angle_degree_conversion() {
        assertEquals(180f, Angle.PI.degrees, EPS)
        assertEquals(90f, Angle.HALF_PI.degrees, EPS)
        assertEquals(360f, Angle.TWO_PI.degrees, EPS)

        assertEquals(PI.toFloat(), 180f.toAngleDegrees().radians, EPS)
        assertEquals((PI / 2).toFloat(), 90.0.toAngleDegrees().radians, EPS)
        assertEquals((2 * PI).toFloat(), 360.toAngleDegrees().radians, EPS)
        assertEquals((2 * PI).toFloat(), 360L.toAngleDegrees().radians, EPS)
    }

    @Test
    fun angle_trig() {
        assertEquals(0f, Angle.ZERO.sin, EPS)
        assertEquals(1f, Angle.HALF_PI.sin, EPS)
        assertEquals(-1f, Angle.PI.cos, EPS)
        assertTrue(Angle.PI.tan.isFinite())
    }

    @Test
    fun angle_normalization() {
        val negHalfPi = Angle(-(PI / 2).toFloat())
        assertEquals((3 * PI / 2).toFloat(), negHalfPi.normalizedPositive().radians, EPS)

        val threeHalfPi = Angle((3 * PI / 2).toFloat())
        assertEquals(-(PI / 2).toFloat(), threeHalfPi.normalizedSigned().radians, EPS)

        val minusThreeHalfPi = Angle(-(3 * PI / 2).toFloat())
        assertEquals((PI / 2).toFloat(), minusThreeHalfPi.normalizedSigned().radians, EPS)
    }

    @Test
    fun angle_delta_wrap_coerce() {
        val a = 10f.toAngleDegrees()
        val b = 350f.toAngleDegrees()
        // shortest delta from a to b is -20 deg
        assertEquals((-20f * PI.toFloat() / 180f), a.deltaTo(b).radians, EPS)

        // wrap to [0, 2π)
        val wrapped = (-90f).toAngleDegrees().wrap(Angle.ZERO, Angle.TWO_PI)
        assertEquals((270f * PI.toFloat() / 180f), wrapped.radians, EPS)

        // coerce
        val coercedLow = (-30f).toAngleDegrees().coerceIn(0f.toAngleDegrees(), 180f.toAngleDegrees())
        val coercedHigh = (200f).toAngleDegrees().coerceIn(0f.toAngleDegrees(), 180f.toAngleDegrees())
        assertEquals(0f.toAngleDegrees().radians, coercedLow.radians, EPS)
        assertEquals(180f.toAngleDegrees().radians, coercedHigh.radians, EPS)
    }

    @Test
    fun angle_ops_basic() {
        val a = 60f.toAngleDegrees()
        val b = 45f.toAngleDegrees()
        assertEquals(105f.toAngleDegrees().radians, (a + b).radians, EPS)
        assertEquals(15f.toAngleDegrees().radians, (a - b).radians, EPS)
        assertEquals(120f.toAngleDegrees().radians, (a * 2).radians, EPS)
        assertEquals(30f.toAngleDegrees().radians, (a / 2).radians, EPS)
        assertEquals(15f.toAngleDegrees().radians, (a % b).radians, EPS)
    }
}