package cn.jzl.datastructure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitSetTest {

    @Test
    fun `BitSet should be created empty by default`() {
        val bitSet = BitSet()
        assertTrue(bitSet.isEmpty())
        assertEquals(0, bitSet.countOneBits())
    }

    @Test
    fun `BitSet should handle set and get operations correctly`() {
        val bitSet = BitSet()
        
        // 测试设置位
        bitSet.set(0, true)
        bitSet.set(1, true)
        bitSet.set(63, true)
        bitSet.set(64, true)
        bitSet.set(127, true)
        
        // 测试获取位值
        assertTrue(bitSet[0])
        assertTrue(bitSet[1])
        assertTrue(bitSet[63])
        assertTrue(bitSet[64])
        assertTrue(bitSet[127])
        
        // 测试未设置的位
        assertFalse(bitSet[2])
        assertFalse(bitSet[62])
        assertFalse(bitSet[128])
    }

    @Test
    fun `BitSet should count one bits correctly`() {
        val bitSet = BitSet()
        
        // 设置5个位
        bitSet.set(0)
        bitSet.set(10)
        bitSet.set(20)
        bitSet.set(30)
        bitSet.set(40)
        
        assertEquals(5, bitSet.countOneBits())
        
        // 清除一个位
        bitSet.clear(10)
        assertEquals(4, bitSet.countOneBits())
    }

    @Test
    fun `BitSet should handle clear operation correctly`() {
        val bitSet = BitSet()
        
        // 设置一些位
        bitSet.set(0)
        bitSet.set(1)
        bitSet.set(2)
        
        // 清除所有位
        bitSet.clear()
        
        assertTrue(bitSet.isEmpty())
        assertEquals(0, bitSet.countOneBits())
        assertFalse(bitSet[0])
    }

    @Test
    fun `BitSet should handle clear specific bit operation correctly`() {
        val bitSet = BitSet()
        
        // 设置一些位
        bitSet.set(0)
        bitSet.set(1)
        bitSet.set(2)
        
        // 清除特定位
        bitSet.clear(1)
        
        assertTrue(bitSet[0])
        assertFalse(bitSet[1])
        assertTrue(bitSet[2])
        assertEquals(2, bitSet.countOneBits())
    }

    @Test
    fun `BitSet should handle flip operation correctly`() {
        val bitSet = BitSet()
        
        // 初始状态为0
        assertFalse(bitSet[5])
        
        // 翻转后应为1
        bitSet.flip(5)
        assertTrue(bitSet[5])
        
        // 再次翻转后应为0
        bitSet.flip(5)
        assertFalse(bitSet[5])
    }

    @Test
    fun `BitSet should handle isEmpty operation correctly`() {
        val bitSet = BitSet()
        assertTrue(bitSet.isEmpty())
        
        bitSet.set(0)
        assertFalse(bitSet.isEmpty())
        
        bitSet.clear()
        assertTrue(bitSet.isEmpty())
    }

    @Test
    fun `BitSet should handle isNotEmpty operation correctly`() {
        val bitSet = BitSet()
        assertFalse(bitSet.isNotEmpty())
        
        bitSet.set(0)
        assertTrue(bitSet.isNotEmpty())
        
        bitSet.clear()
        assertFalse(bitSet.isNotEmpty())
    }

    @Test
    fun `BitSet should handle size calculation correctly`() {
        val bitSet = BitSet()
        assertEquals(0, bitSet.size)
        
        bitSet.set(0)
        assertEquals(1, bitSet.size)
        
        bitSet.set(64)
        assertEquals(65, bitSet.size)
        
        bitSet.set(127)
        assertEquals(128, bitSet.size)
    }

    @Test
    fun `BitSet should handle or operation correctly`() {
        val bitSet1 = BitSet()
        val bitSet2 = BitSet()
        
        bitSet1.set(0)
        bitSet1.set(1)
        bitSet2.set(1)
        bitSet2.set(2)
        
        // 执行OR操作
        bitSet1.or(bitSet2)
        
        // 验证结果
        assertTrue(bitSet1[0])
        assertTrue(bitSet1[1])
        assertTrue(bitSet1[2])
        assertEquals(3, bitSet1.countOneBits())
    }

    @Test
    fun `BitSet should handle and operation correctly`() {
        val bitSet1 = BitSet()
        val bitSet2 = BitSet()
        
        bitSet1.set(0)
        bitSet1.set(1)
        bitSet1.set(2)
        bitSet2.set(1)
        bitSet2.set(2)
        bitSet2.set(3)
        
        // 执行AND操作
        bitSet1.and(bitSet2)
        
        // 验证结果
        assertFalse(bitSet1[0])
        assertTrue(bitSet1[1])
        assertTrue(bitSet1[2])
        assertFalse(bitSet1[3])
        assertEquals(2, bitSet1.countOneBits())
    }

    @Test
    fun `BitSet should handle xor operation correctly`() {
        val bitSet1 = BitSet()
        val bitSet2 = BitSet()
        
        bitSet1.set(0)
        bitSet1.set(1)
        bitSet2.set(1)
        bitSet2.set(2)
        
        // 执行XOR操作
        bitSet1.xor(bitSet2)
        
        // 验证结果
        assertTrue(bitSet1[0])
        assertFalse(bitSet1[1])
        assertTrue(bitSet1[2])
        assertEquals(2, bitSet1.countOneBits())
    }

    @Test
    fun `BitSet should handle not operation correctly`() {
        val bitSet = BitSet()
        bitSet.set(0)
        
        // 执行NOT操作
        bitSet.not()
        
        // 注意：not操作会翻转所有位，包括高位，这里只测试相关位
        assertFalse(bitSet[0])
    }

    @Test
    fun `BitSet should handle intersects operation correctly`() {
        val bitSet1 = BitSet()
        val bitSet2 = BitSet()
        
        // 没有交集
        assertFalse(bitSet1.intersects(bitSet2))
        
        bitSet1.set(0)
        bitSet2.set(1)
        assertFalse(bitSet1.intersects(bitSet2))
        
        // 设置一个共同的位
        bitSet2.set(0)
        assertTrue(bitSet1.intersects(bitSet2))
    }

    @Test
    fun `BitSet should handle contains operation correctly`() {
        val bitSet1 = BitSet()
        val bitSet2 = BitSet()
        
        // bitSet2为空，bitSet1包含它
        assertTrue(bitSet1.contains(bitSet2))
        
        // 设置bitSet2的位
        bitSet2.set(0)
        assertFalse(bitSet1.contains(bitSet2))
        
        // 设置bitSet1的相应位
        bitSet1.set(0)
        assertTrue(bitSet1.contains(bitSet2))
        
        // bitSet2添加新位
        bitSet2.set(1)
        assertFalse(bitSet1.contains(bitSet2))
    }

    @Test
    fun `BitSet should handle iterator correctly`() {
        val bitSet = BitSet()
        val expectedIndices = listOf(0, 5, 10, 15, 63, 64)
        
        // 设置一些位
        expectedIndices.forEach { bitSet.set(it) }
        
        // 测试迭代器
        val actualIndices = mutableListOf<Int>()
        for (index in bitSet) {
            actualIndices.add(index)
        }
        
        assertEquals(expectedIndices, actualIndices)
    }

    @Test
    fun `BitSet should handle nextSetBit correctly`() {
        val bitSet = BitSet()
        bitSet.set(5)
        bitSet.set(10)
        bitSet.set(15)
        
        assertEquals(5, bitSet.nextSetBit(0))
        assertEquals(10, bitSet.nextSetBit(6))
        assertEquals(15, bitSet.nextSetBit(11))
        assertEquals(-1, bitSet.nextSetBit(16))
    }

    @Test
    fun `BitSet should handle nextClearBit correctly`() {
        val bitSet = BitSet()
        bitSet.set(0)
        bitSet.set(1)
        bitSet.set(2)
        
        assertEquals(3, bitSet.nextClearBit(0))
        assertEquals(3, bitSet.nextClearBit(2))
        assertEquals(3, bitSet.nextClearBit(3))
    }

    @Test
    fun `BitSet should handle copy operation correctly`() {
        val original = BitSet()
        original.set(0)
        original.set(1)
        original.set(100)
        
        val copy = original.copy()
        
        // 验证副本包含相同的位
        assertEquals(original.countOneBits(), copy.countOneBits())
        assertEquals(original.isEmpty(), copy.isEmpty())
        assertTrue(copy[0])
        assertTrue(copy[1])
        assertTrue(copy[100])
        
        // 验证是深拷贝
        original.clear(0)
        assertTrue(copy[0]) // 副本不受影响
    }

    @Test
    fun `BitSet should handle equality correctly`() {
        val bitSet1 = BitSet()
        val bitSet2 = BitSet()
        
        // 两个空BitSet应该相等
        assertEquals(bitSet1, bitSet2)
        
        // 设置相同的位后应该相等
        bitSet1.set(0)
        bitSet2.set(0)
        assertEquals(bitSet1, bitSet2)
        
        // 设置不同的位后不应该相等
        bitSet2.set(1)
        assertFalse(bitSet1 == bitSet2)
    }

    @Test
    fun `BitSet should handle hashCode correctly`() {
        val bitSet1 = BitSet()
        val bitSet2 = BitSet()
        
        // 两个空BitSet的hashCode应该相同
        assertEquals(bitSet1.hashCode(), bitSet2.hashCode())
        
        // 设置相同的位后hashCode应该相同
        bitSet1.set(0)
        bitSet2.set(0)
        assertEquals(bitSet1.hashCode(), bitSet2.hashCode())
    }

    @Test
    fun `BitSet should handle large indices correctly`() {
        val bitSet = BitSet()
        val largeIndex = 1000000 // 一百万位
        
        bitSet.set(largeIndex)
        assertTrue(bitSet[largeIndex])
        assertEquals(1, bitSet.countOneBits())
        
        // 测试nextSetBit
        assertEquals(largeIndex, bitSet.nextSetBit(0))
        
        // 清除位
        bitSet.clear(largeIndex)
        assertFalse(bitSet[largeIndex])
    }

    @Test
    fun `BitSet should handle multiple word boundaries correctly`() {
        val bitSet = BitSet()
        
        // 设置跨越多个Long字的位
        bitSet.set(63) // 第一个字的最后一位
        bitSet.set(64) // 第二个字的第一位
        bitSet.set(127) // 第二个字的最后一位
        bitSet.set(128) // 第三个字的第一位
        
        // 验证设置的位
        assertTrue(bitSet[63])
        assertTrue(bitSet[64])
        assertTrue(bitSet[127])
        assertTrue(bitSet[128])
        
        // 验证位运算
        val other = BitSet()
        other.set(64)
        other.set(128)
        
        bitSet.and(other)
        assertEquals(2, bitSet.countOneBits())
        assertFalse(bitSet[63])
        assertTrue(bitSet[64])
        assertFalse(bitSet[127])
        assertTrue(bitSet[128])
    }
}
