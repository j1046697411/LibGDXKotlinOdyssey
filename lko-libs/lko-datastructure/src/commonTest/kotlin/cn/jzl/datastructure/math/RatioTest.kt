package cn.jzl.datastructure.math

import kotlin.test.*

private const val EPS = 1e-5f

class RatioTest {
    @Test
    fun ratio_constants_and_factories() {
        assertEquals(0f, ZERO_RATIO.value, EPS)
        assertEquals(1f, ONE_RATIO.value, EPS)
        assertEquals(0.5f, HALF_RATIO.value, EPS)

        assertEquals(2f, 2f.toRatio().value, EPS)
        assertEquals(2f, 2.toRatio().value, EPS)
        assertEquals(2f, 2.0.toRatio().value, EPS)
        assertEquals(2f, 2L.toRatio().value, EPS)
    }

    @Test
    fun ratio_coerceIn() {
        // Float bounds
        assertEquals(0f, Ratio(-0.2f).coerceIn(0f, 1f).value, EPS)
        assertEquals(1f, Ratio(1.2f).coerceIn(0f, 1f).value, EPS)
        assertEquals(0.3f, Ratio(0.3f).coerceIn(0f, 1f).value, EPS)

        // Ratio bounds
        assertEquals(0f, Ratio(-0.2f).coerceIn(ZERO_RATIO, ONE_RATIO).value, EPS)
        assertEquals(1f, Ratio(1.2f).coerceIn(ZERO_RATIO, ONE_RATIO).value, EPS)

        // Range bounds
        val range = 0f..1f
        assertEquals(0f, Ratio(-0.2f).coerceIn(range).value, EPS)
        assertEquals(1f, Ratio(1.2f).coerceIn(range).value, EPS)
        assertEquals(0.3f, Ratio(0.3f).coerceIn(range).value, EPS)

        // Invalid ranges
        assertFailsWith<IllegalArgumentException> { Ratio(0.5f).coerceIn(1f, 0f) }
        assertFailsWith<IllegalArgumentException> { Ratio(0.5f).coerceIn(ONE_RATIO, ZERO_RATIO) }
        assertFailsWith<IllegalArgumentException> { Ratio(0.5f).coerceIn(1f..0f) }
    }

    @Test
    fun ratio_coerceIn_double() {
        // Double bounds
        assertEquals(0f, Ratio(-0.2f).coerceIn(0.0, 1.0).value, EPS)
        assertEquals(1f, Ratio(1.2f).coerceIn(0.0, 1.0).value, EPS)
        assertEquals(0.3f, Ratio(0.3f).coerceIn(0.0, 1.0).value, EPS)

        // Double range
        val drange = 0.0..1.0
        assertEquals(0f, Ratio(-0.2f).coerceIn(drange).value, EPS)
        assertEquals(1f, Ratio(1.2f).coerceIn(drange).value, EPS)
        assertEquals(0.3f, Ratio(0.3f).coerceIn(drange).value, EPS)

        // Invalid double ranges
        assertFailsWith<IllegalArgumentException> { Ratio(0.5f).coerceIn(1.0, 0.0) }
        assertFailsWith<IllegalArgumentException> { Ratio(0.5f).coerceIn(1.0..0.0) }
    }
 
    @Test
    fun ratio_flags() {
        assertTrue(Ratio(0f).isZero)
        assertFalse(Ratio(1e-6f).isZero)
        assertTrue(Ratio(1f).isPositive)
        assertTrue(Ratio(-1f).isNegative)
        assertFalse(Ratio(-1f).isPositive)
        assertFalse(Ratio(1f).isNegative)
    }

    @Test
    fun ratio_plus_minus() {
        val r0 = ZERO_RATIO
        val r05 = HALF_RATIO
        val r1 = ONE_RATIO

        // plus
        assertEquals(1f, (r05 + r05).value, EPS)
        assertEquals(1.5f, (r05 + 1).value, EPS)
        assertEquals(1.5f, (r05 + 1L).value, EPS)
        assertEquals(1f, (r05 + 0.5f).value, EPS)
        assertEquals(1f, (r05 + 0.5).value, EPS)
        assertEquals(1f, (r05 + r0 + r05).value, EPS)

        // minus
        assertEquals(0.5f, (r1 - r05).value, EPS)
        assertEquals(-0.5f, (r05 - r1).value, EPS)
        assertEquals(0f, (r05 - 0.5f).value, EPS)
        assertEquals(0f, (r05 - 0.5).value, EPS)
        assertEquals(-0.5f, (r0 - r05).value, EPS)
    }

    @Test
    fun ratio_times_div() {
        val r05 = HALF_RATIO
        val r1 = ONE_RATIO

        // times
        assertEquals(0.25f, (r05 * r05).value, EPS)
        assertEquals(1f, (r05 * 2f).value, EPS)
        assertEquals(1f, (r05 * 2).value, EPS)
        assertEquals(1f, (r05 * 2.0).value, EPS)
        assertEquals(1f, (r05 * 2L).value, EPS)

        // div
        assertEquals(0.5f, (r1 / 2f).value, EPS)
        assertEquals(0.5f, (r05 / r1).value, EPS)
        assertEquals(2f, (r1 / r05).value, EPS)
        assertEquals(0.25f, (r05 / 2).value, EPS)
        assertEquals(0.25f, (r05 / 2.0).value, EPS)
        assertEquals(0.25f, (r05 / 2L).value, EPS)
    }

    @Test
    fun ratio_reverse_operators() {
        val r05 = HALF_RATIO
        val r1 = ONE_RATIO

        // plus
        assertEquals(1.5f, (1 + r05).value, EPS)
        assertEquals(1.5f, (1L + r05).value, EPS)
        assertEquals(1f, (0.5f + r05).value, EPS)
        assertEquals(1f, (0.5 + r05).value, EPS)

        // minus
        assertEquals(0.5f, (1 - r05).value, EPS)
        assertEquals(-0.5f, (0 - r05).value, EPS)
        assertEquals(0f, (0.5f - r05).value, EPS)
        assertEquals(0f, (0.5 - r05).value, EPS)

        // times
        assertEquals(1f, (2 * r05).value, EPS)
        assertEquals(1f, (2L * r05).value, EPS)
        assertEquals(1f, (2.0 * r05).value, EPS)
        assertEquals(1f, (2f * r05).value, EPS)

        // div
        assertEquals(2f, (2 / r1).value, EPS)
        assertEquals(4f, (2 / r05).value, EPS)
        assertEquals(2f, (2.0 / r1).value, EPS)
        assertEquals(4f, (2.0 / r05).value, EPS)
        assertEquals(2f, (2L / r1).value, EPS)
        assertEquals(4f, (2L / r05).value, EPS)
        assertEquals(2f, (2f / r1).value, EPS)
        assertEquals(4f, (2f / r05).value, EPS)
    }

    @Test
    fun ratio_equality_and_hash() {
        assertEquals(Ratio(0.5f), HALF_RATIO)
        assertEquals(Ratio(1f), ONE_RATIO)
        assertNotEquals(Ratio(0.5f), ONE_RATIO)
        assertEquals(Ratio(0.5f).hashCode(), HALF_RATIO.hashCode())
    }
}