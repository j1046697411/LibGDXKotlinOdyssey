package cn.jzl.datastructure.bvh

import cn.jzl.datastructure.list.MutableFastList

interface Dimension {
    val dimension: Int
}

@JvmInline
value class BVHVector(val data: FloatArray) : Dimension {

    override val dimension: Int get() = data.size

    operator fun get(index: Int): Float {
        return data[index]
    }

    operator fun set(index: Int, value: Float) {
        data[index] = value
    }
}

@JvmInline
value class BVHRay(val intervals: BVHIntervals) : Dimension {

    override val dimension: Int get() = intervals.dimension

    fun origin(dimension: Int): Float = intervals.a(dimension)
    fun origin(dimension: Int, value: Float) {
        intervals.a(dimension, value)
    }

    fun direction(dimension: Int): Float = intervals.b(dimension)
    fun direction(dimension: Int, value: Float) {
        intervals.b(dimension, value)
    }

    val origin: BVHVector get() = BVHVector(FloatArray(dimension) { intervals.a(it) })
    val direction: BVHVector get() = BVHVector(FloatArray(dimension) { intervals.b(it) })
}

@JvmInline
value class BVHRect(val intervals: BVHIntervals) : Dimension {

    override val dimension: Int get() = intervals.dimension

    val min: BVHVector get() = BVHVector(FloatArray(dimension) { intervals.a(it) })
    val size: BVHVector get() = BVHVector(FloatArray(dimension) { intervals.b(it) })
    val max: BVHVector get() = BVHVector(FloatArray(dimension) { min(it) + size(it) })

    fun min(dimension: Int): Float = intervals.a(dimension)
    fun size(dimension: Int): Float = intervals.b(dimension)
    fun max(dimension: Int): Float = min(dimension) + size(dimension)

    fun min(dimension: Int, value: Float) = intervals.a(dimension, value)
    fun size(dimension: Int, value: Float) = intervals.b(dimension, value)
}

data class BVHIntervals(val data: FloatArray, override val dimension: Int) : Dimension {

    fun a(index: Int): Float = data[index * dimension]

    fun a(index: Int, value: Float) {
        data[index * dimension] = value
    }

    fun b(index: Int): Float = data[index * dimension + 1]
    fun b(index: Int, value: Float) {
        data[index * dimension + 1] = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BVHIntervals

        if (dimension != other.dimension) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dimension
        result = 31 * result + data.contentHashCode()
        return result
    }
}

class BVH<T>(
    override val dimension: Int
) : Dimension, Sequence<BVH.Node<T>> {
    private val objectToNodes = mutableMapOf<T, Node<T>>()
    private val root: Node<T> = Node(
        BVHRect(
            BVHIntervals(
                FloatArray(dimension * 2),
                dimension
            )
        ),
        id = "root",
    )

    override fun iterator(): Iterator<Node<T>> {
        TODO("Not yet implemented")
    }

    fun insertOrUpdate(rect: BVHRect, data: T) {
        val node = objectToNodes[data]
        if (node?.rect == rect) return
        insertSubtree(this.root, node ?: Node(rect, null))
    }

    private fun insertSubtree(root: Node<T>, node: Node<T>) {

    }

    data class Node<T>(
        val rect: BVHRect,
        var id: String?,
        var nodes: MutableFastList<Node<T>>? = null,
        var data: T? = null,
    )
}