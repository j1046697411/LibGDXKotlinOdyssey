package cn.jzl.ecs

@ConsistentCopyVisibility
data class EntityType @PublishedApi internal constructor(@PublishedApi internal val data: LongArray) : Sequence<Relation> {
    init {
        data.sort()
    }

    val size: Int get() = data.size

    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = size > 0

    operator fun get(index: Int): Relation {
        require(index in data.indices)
        return Relation(data[index])
    }

    fun getOrNull(index: Int): Relation? {
        return data.getOrNull(index)?.let { Relation(it) }
    }

    operator fun plus(relation: Relation): EntityType = EntityType(data + relation.data)
    operator fun minus(relation: Relation): EntityType = EntityType(data.filter { it != relation.data }.toLongArray())

    operator fun contains(relation: Relation): Boolean = binarySearch(relation) >= 0

    fun indexOf(relation: Relation): Int = binarySearch(relation)

    @PublishedApi
    internal fun binarySearch(relation: Relation): Int {
        var left = 0
        var right = data.size - 1
        while (left <= right) {
            val mid = (left + right) ushr 1
            val midVal = data[mid]
            when {
                midVal < relation.data -> left = mid + 1
                midVal > relation.data -> right = mid - 1
                else -> return mid // 找到元素
            }
        }
        return -1
    }

    override fun iterator(): Iterator<Relation> = iterator {
        for (i in data.indices) {
            yield(Relation(data[i]))
        }
    }

    override fun toString(): String = joinToString(", ", "EntityType[", "]")

    override fun hashCode(): Int = data.contentHashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EntityType

        return data.contentEquals(other.data)
    }

    companion object {
        val ENTITY_TYPE_EMPTY = EntityType(LongArray(0))
        operator fun invoke(relations: Sequence<Relation>): EntityType = EntityType(relations.map { it.data }.toSet().toLongArray())
    }
}