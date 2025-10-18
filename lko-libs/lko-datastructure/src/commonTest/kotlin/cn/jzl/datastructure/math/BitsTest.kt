package cn.jzl.datastructure.math;

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitsTest {

    @Test
    fun `Short reverseBytes should work correctly`() {
        val shortValue: Short = 0x1234
        val reversed = shortValue.reverseBytes()
        assertEquals(0x3412.toShort(), reversed)
    }

    @Test
    fun `Char reverseBytes should work correctly`() {
        val charValue: Char = 0x1234.toChar()
        val reversed = charValue.reverseBytes()
        assertEquals(0x3412.toChar(), reversed)
    }

    @Test
    fun `Int reverseBytes should work correctly`() {
        val intValue: Int = 0x12345678
        val reversed = intValue.reverseBytes()
        assertEquals(0x78563412, reversed)
    }

    @Test
    fun `Long reverseBytes should work correctly`() {
        val longValue = 0x123456789ABCDEF0L
        val reversed = longValue.reverseBytes()
        assertEquals(0xF0DEBC9A78563412UL.toLong(), reversed)
    }

    @Test
    fun `Int reverseBits should work correctly`() {
        val intValue: Int = (0b10000000000000000000000000000000).toInt()
        val reversed = intValue.reverseBits()
        assertEquals(1, reversed)
    }

    @Test
    fun `Int countLeadingZeros should work correctly`() {
        val intValue = 0b1000
        assertEquals(28, intValue.countLeadingZeros())
    }

    @Test
    fun `Int countTrailingZeros should work correctly`() {
        val intValue: Int = 0b1000
        assertEquals(3, intValue.countTrailingZeros())
    }

    @Test
    fun `Int countLeadingOnes should work correctly`() {
        val intValue: Int = 0xFFFFFFF0.toInt()
        assertEquals(28, intValue.countLeadingOnes())
    }

    @Test
    fun `Int countTrailingOnes should work correctly`() {
        val intValue: Int = 0x0000000F
        assertEquals(4, intValue.countTrailingOnes())
    }

    @Test
    fun `Int signExtend should work correctly`() {
        val intValue: Int = 0b1000
        assertEquals(-8, intValue.signExtend(4))
    }

    @Test
    fun `Long signExtend should work correctly`() {
        val longValue: Long = 0b1000L
        assertEquals(-8L, longValue.signExtend(4))
    }

    @Test
    fun `Int mask should work correctly`() {
        assertEquals(0b111, 3.mask())
    }

    @Test
    fun `Long mask should work correctly`() {
        assertEquals(0b111L, 3L.mask())
    }

    @Test
    fun `Int mask with offset should work correctly`() {
        assertEquals(0b11100, 3.mask(2))
    }

    @Test
    fun `Long mask with offset should work correctly`() {
        assertEquals(0b11100L, 3L.mask(2))
    }

    @Test
    fun `IntMaskRange fromRange should work correctly`() {
        val range = IntMaskRange.fromRange(2, 3)
        assertEquals(2, range.offset)
        assertEquals(3, range.size)
    }

    @Test
    fun `IntMaskRange fromMask should work correctly`() {
        val range = IntMaskRange.fromMask(0b11100)
        assertEquals(2, range.offset)
        assertEquals(3, range.size)
    }

    @Test
    fun `IntMaskRange toMask should work correctly`() {
        val range = IntMaskRange.fromRange(2, 3)
        assertEquals(0b11100, range.toMask())
    }

    @Test
    fun `IntMaskRange extract should work correctly`() {
        val range = IntMaskRange.fromRange(2, 3)
        assertEquals(0b011, range.extract(0b101101))
    }

    @Test
    fun `Int extractMaskRange should work correctly`() {
        val range = 0b11100.extractMaskRange()
        assertEquals(2, range.offset)
        assertEquals(3, range.size)
    }

    @Test
    fun `Int extract with count should work correctly`() {
        assertEquals(0b011, 0b101101.extract(2, 3))
    }

    @Test
    fun `Int extract single bit should work correctly`() {
        assertTrue(0b101.extract(2))
        assertFalse(0b101.extract(1))
    }

    @Test
    fun `Int extractBool should work correctly`() {
        assertTrue(0b101.extractBool(2))
        assertFalse(0b101.extractBool(1))
    }

    @Test
    fun `Int extract01 should work correctly`() {
        assertEquals(1, 0b101.extract01(2))
        assertEquals(0, 0b101.extract01(1))
    }

    @Test
    fun `Int extract08 should work correctly`() {
        assertEquals(0xAB, 0x123456AB.extract08(0))
    }

    @Test
    fun `Int extractSigned should work correctly`() {
        assertEquals(-1, 0xFFFFFFFF.toInt().extractSigned(0, 8))
    }

    @Test
    fun `Int extract8Signed should work correctly`() {
        assertEquals(-1, 0xFFFFFFFF.toInt().extract8Signed(0))
    }

    @Test
    fun `Int extract16Signed should work correctly`() {
        assertEquals(-1, 0xFFFFFFFF.toInt().extract16Signed(0))
    }

    @Test
    fun `Int extractByte should work correctly`() {
        assertEquals(0xAB.toByte(), 0x123456AB.extractByte(0))
    }

    @Test
    fun `Int extractShort should work correctly`() {
        assertEquals(0x56AB.toShort(), 0x123456AB.extractShort(0))
    }

    @Test
    fun `Int extractScaled should work correctly`() {
        assertEquals(0x7F, 0xFF.extractScaled(0, 8, 0x7F))
    }

    @Test
    fun `Int extractScaledF01 should work correctly`() {
        assertEquals(128f/255f, 0x80.extractScaledF01(0, 8), 0.001f)
    }

    @Test
    fun `Int extractScaledFF should work correctly`() {
        assertEquals(0xFF, 0xFF.extractScaledFF(0, 8))
    }

    @Test
    fun `Int insert should work correctly`() {
        assertEquals(0b101111101, 0b101000101.insert(0b111, 3, 3))
    }

    @Test
    fun `Int insertNoClear should work correctly`() {
        assertEquals(0b101111101, 0b101000101.insertNoClear(0b111, 3, 3))
    }

    @Test
    fun `Int clear should work correctly`() {
        assertEquals(0b101000101, 0b101111101.clear(3, 3))
    }

    @Test
    fun `Int insert01 should work correctly`() {
        assertEquals(0b101, 0b100.insert01(1, 0))
    }

    @Test
    fun `Int insert08 should work correctly`() {
        assertEquals(0x123400AB, 0x12340000.insert08(0xAB, 0))
    }

    @Test
    fun `Int fastInsert should work correctly`() {
        assertEquals(0b1101, 0b1001.fastInsert(0b10, 1))
    }

    @Test
    fun `Int fastInsert08 should work correctly`() {
        assertEquals(0x123400AB, 0x12340000.fastInsert08(0xAB, 0))
    }

    @Test
    fun `Int insertMask should work correctly`() {
        assertEquals(0b101101, 0b100001.insertMask(0b11, 2, 0b11))
    }

    @Test
    fun `Int insert boolean should work correctly`() {
        assertEquals(0b101, 0b100.insert(true, 0))
        assertEquals(0b100, 0b101.insert(false, 0))
    }

    @Test
    fun `Int insertScaled should work correctly`() {
        assertEquals(0xFF, 0x00.insertScaled(0x7F, 0, 8, 0x7F))
    }

    @Test
    fun `Int insertScaledFF should work correctly`() {
        assertEquals(0xFF, 0x00.insertScaledFF(0xFF, 0, 8))
    }

    @Test
    fun `Int insertScaledF01 should work correctly`() {
        assertEquals(0x7F, 0x00.insertScaledF01(0.5f, 0, 8))
    }

    @Test
    fun `Int hasFlags should work correctly`() {
        assertTrue(0b1011 hasFlags 0b1010)
        assertFalse(0b1010 hasFlags 0b1011)
    }

    @Test
    fun `Int hasBits should work correctly`() {
        assertTrue(0b1011 hasBits 0b1010)
        assertFalse(0b1010 hasBits 0b1011)
    }

    @Test
    fun `Int hasBitSet should work correctly`() {
        assertTrue(0b101 hasBitSet 2)
        assertFalse(0b101 hasBitSet 1)
    }

    @Test
    fun `Long hasFlags should work correctly`() {
        assertTrue(0b1011L hasFlags 0b1010L)
        assertFalse(0b1010L hasFlags 0b1011L)
    }

    @Test
    fun `Long hasBits should work correctly`() {
        assertTrue(0b1011L hasBits 0b1010L)
        assertFalse(0b1010L hasBits 0b1011L)
    }

    @Test
    fun `bit function should work correctly`() {
        assertEquals(0b1000, bit(3))
    }

    @Test
    fun `Int unsetBits should work correctly`() {
        assertEquals(0b1001, 0b1011.unsetBits(0b10))
    }

    @Test
    fun `Int setBits should work correctly`() {
        assertEquals(0b1011, 0b1001.setBits(0b10))
    }

    @Test
    fun `Int setBits with boolean should work correctly`() {
        assertEquals(0b1011, 0b1001.setBits(0b10, true))
        assertEquals(0b1001, 0b1011.setBits(0b10, false))
    }

    @Test
    fun `Int without should work correctly`() {
        assertEquals(0b1001, 0b1011.without(0b10))
    }

    @Test
    fun `Int with should work correctly`() {
        assertEquals(0b1011, 0b1001.with(0b10))
    }

    @Test
    fun `Long without should work correctly`() {
        assertEquals(0b1001L, 0b1011L.without(0b10L))
    }

    @Test
    fun `Long with should work correctly`() {
        assertEquals(0b1011L, 0b1001L.with(0b10L))
    }

    @Test
    fun `Long high and low properties should work correctly`() {
        val longValue = Long.fromLowHigh(0x12345678, 0x87654321.toInt())
        assertEquals(0x87654321.toInt(), longValue.high)
        assertEquals(0x12345678, longValue.low)
    }

    @Test
    fun `Long fromLowHigh should work correctly`() {
        val longValue = Long.fromLowHigh(0x12345678, 0x87654321.toInt())
        assertEquals(0x8765432112345678UL.toLong(), longValue)
    }

    @Test
    fun `Int fastForEachOneBits should work correctly`() {
        val bits = 0b10101
        val positions = mutableListOf<Int>()
        bits.fastForEachOneBits {
            positions.add(it)
        }
        assertEquals(listOf(0, 2, 4), positions)
    }
}