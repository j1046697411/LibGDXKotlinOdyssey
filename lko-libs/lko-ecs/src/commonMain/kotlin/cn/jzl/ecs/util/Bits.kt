package cn.jzl.ecs.util

import kotlin.math.max
import kotlin.math.min

class Bits(initialCapacity: Int = 0) : Sequence<Int> {

    private var bits: LongArray = LongArray((initialCapacity + 63) ushr 6)

    val size: Int get() = bits.bitLength()

    fun countOneBits(): Int = bits.countOneBits()

    fun isEmpty(): Boolean = countOneBits() == 0

    fun isNotEmpty(): Boolean = countOneBits() != 0

    fun clearAll() {
        bits.fill(0)
    }

    operator fun contains(bitIndex: Int): Boolean {
        val wordIndex = bitIndex.wordIndex
        return bits.size > wordIndex && bits[wordIndex] and bitIndex.bitMask != 0L
    }

    fun setBit(bitIndex: Int) {
        val wordIndex = bitIndex.wordIndex
        ensureCapacity(bitIndex)
        bits[wordIndex] = bits[wordIndex] or bitIndex.bitMask
    }

    fun clearBit(bitIndex: Int) {
        val wordIndex = bitIndex.wordIndex
        if (wordIndex >= bits.size) return
        bits[wordIndex] = bits[wordIndex] and bitIndex.bitMask.inv()
    }

    fun intersects(other: Bits): Boolean {
        val size = min(other.bits.size, bits.size)
        for (i in size - 1 downTo 0) {
            if (other.bits[i] and bits[i] != 0L) return true
        }
        return false
    }

    operator fun contains(other: Bits): Boolean {
        if (other.bits.size > bits.size) {
            for (i in bits.size until other.bits.size) {
                if (other.bits[i] != 0L) return false
            }
        }
        val size = min(bits.size, other.bits.size)
        for (i in 0 until size) {
            if (other.bits[i] and bits[i] != other.bits[i]) return false
        }
        return true
    }

    fun logicalAnd(other: Bits) {
        if (this === other) return
        if (bits.size > other.bits.size) {
            for (i in other.bits.size until bits.size) bits[i] = 0
        }
        for (i in 0 until min(bits.size, other.bits.size)) {
            bits[i] = bits[i] and other.bits[i]
        }
    }

    fun logicalOr(other: Bits) {
        if (this === other) return
        if (bits.size < other.bits.size) ensureCapacity(other.bits.size * 64 - 1)
        for (i in bits.indices) {
            bits[i] = bits[i] or other.bits.getOrZero(i)
        }
    }

    fun logicalXor(other: Bits) {
        if (this === other) return
        if (bits.size < other.bits.size) ensureCapacity(other.bits.size * 64 - 1)
        for (i in bits.indices) {
            bits[i] = bits[i] xor other.bits.getOrZero(i)
        }
    }

    private fun ensureCapacity(bitIndex: Int) {
        val wordIndex = bitIndex.wordIndex
        if (bits.size <= wordIndex) {
            val newSize = max(wordIndex + 1, bits.size * 2)
            bits = bits.copyOf(newSize)
        }
    }

    fun nextSetBit(fromIndex: Int): Int {
        if (fromIndex < 0) return -1
        var wordIndex = fromIndex.wordIndex
        if (wordIndex >= bits.size) return -1

        val bitIndex = fromIndex.bitIndex
        var word = bits[wordIndex]
        if (bitIndex != 0) word = word and (fromIndex.bitMask - 1L).inv()

        while (true) {
            if (word != 0L) return (wordIndex shl 6) + word.countTrailingZeroBits()
            if (++wordIndex >= bits.size) return -1
            word = bits[wordIndex]
        }
    }

    // ==================== 新增实用方法 ====================
    fun nextClearBit(fromIndex: Int): Int {
        if (fromIndex < 0) return -1
        var wordIndex = fromIndex.wordIndex

        while (wordIndex < bits.size) {
            var word = bits[wordIndex].inv()
            if (wordIndex == fromIndex.wordIndex) {
                val mask = (1L shl fromIndex.bitIndex) - 1
                word = word and mask.inv()
            }

            if (word != 0L) {
                return (wordIndex shl 6) + word.countTrailingZeroBits()
            }
            wordIndex++
        }
        return bits.size * 64
    }

    fun flipBit(bitIndex: Int) {
        val wordIndex = bitIndex.wordIndex
        ensureCapacity(bitIndex)
        bits[wordIndex] = bits[wordIndex] xor bitIndex.bitMask
    }

    fun setRange(fromIndex: Int, toIndex: Int) {
        if (fromIndex > toIndex) return
        ensureCapacity(toIndex)

        val startWord = fromIndex.wordIndex
        val endWord = toIndex.wordIndex

        if (startWord == endWord) {
            val mask = createMask(fromIndex.bitIndex, toIndex.bitIndex)
            bits[startWord] = bits[startWord] or mask
        } else {
            // 首字
            bits[startWord] = bits[startWord] or (-1L shl fromIndex.bitIndex)
            // 中间字
            for (i in startWord + 1 until endWord) {
                bits[i] = -1L
            }
            // 尾字
            bits[endWord] = bits[endWord] or (1L shl (toIndex.bitIndex + 1)) - 1
        }
    }

    fun clearRange(fromIndex: Int, toIndex: Int) {
        if (fromIndex > toIndex) return
        ensureCapacity(toIndex)

        val startWord = fromIndex.wordIndex
        val endWord = toIndex.wordIndex

        if (startWord == endWord) {
            val mask = createMask(fromIndex.bitIndex, toIndex.bitIndex)
            bits[startWord] = bits[startWord] and mask.inv()
        } else {
            // 首字
            bits[startWord] = bits[startWord] and (1L shl fromIndex.bitIndex) - 1
            // 中间字
            for (i in startWord + 1 until endWord) {
                bits[i] = 0L
            }
            // 尾字
            if (endWord < bits.size) {
                bits[endWord] = bits[endWord] and (-1L shl (toIndex.bitIndex + 1))
            }
        }
    }

    fun toBitSet(): Set<Int> = iterator().asSequence().toSet()

    fun copy(): Bits {
        val newBits = Bits()
        newBits.bits = bits.copyOf()
        return newBits
    }

    // ==================== 内部工具方法 ====================
    private fun createMask(fromBit: Int, toBit: Int): Long {
        return (-1L shl fromBit) and (-1L ushr (63 - toBit))
    }

    override fun iterator(): Iterator<Int> = BitIterator(this, 0)

    private val Int.wordIndex: Int get() = this ushr 6
    private val Int.bitIndex: Int get() = this and 0b111111
    private val Int.bitMask: Long get() = 1L shl bitIndex

    private fun LongArray.getOrZero(idx: Int): Long {
        return if (idx < size) this[idx] else 0
    }

    private inner class BitIterator(
        private val bits: Bits,
        fromIndex: Int = 0
    ) : Iterator<Int> {

        private var currentIndex: Int = bits.nextSetBit(fromIndex)

        override fun hasNext(): Boolean = currentIndex != -1

        override fun next(): Int {
            val index = currentIndex
            currentIndex = bits.nextSetBit(currentIndex + 1)
            return index
        }
    }

    override fun toString(): String = toBitSet().toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Bits) return false

        for (i in 0 until max(bits.size, other.bits.size)) {
            if (bits.getOrZero(i) != other.bits.getOrZero(i)) return false
        }
        return true
    }

    override fun hashCode(): Int {
        return bits.foldRight(0L) { l, acc ->
            if (l == 0L && acc == 0L) 0 else acc * 31 + l
        }.toInt()
    }
}

fun bitsOf(vararg indices: Int): Bits {
    return Bits().apply {
        indices.forEach(this::setBit)
    }
}

// ==================== LongArray 扩展 ====================
private fun LongArray.bitLength(): Int {
    if (isEmpty()) return 0
    for (i in size - 1 downTo 0) {
        val value = this[i]
        if (value != 0L) {
            return (i shl 6) + 64 - value.countLeadingZeroBits()
        }
    }
    return 0
}

private fun LongArray.countOneBits(): Int {
    if (isEmpty()) return 0
    var countBits = 0
    for (value in this) {
        countBits += value.countOneBits()
    }
    return countBits
}