@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs.query

import cn.jzl.datastructure.list.ObjectFastList
import cn.jzl.ecs.Archetype
import cn.jzl.ecs.Components
import cn.jzl.ecs.Entities
import cn.jzl.ecs.Entity
import cn.jzl.ecs.Family
import cn.jzl.ecs.World
import cn.jzl.ecs.observers.Observer
import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.query.QueryGroupedBy.QueryGroup
import kotlinx.coroutines.CancellationException

@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION,
)
@DslMarker
annotation class ECSDsl

fun interface QueryCollector<T> {
    fun emit(value: T)
}

@PublishedApi
internal data class AbortQueryException(val queryStream: QueryStream<*>) : CancellationException(null)

@PublishedApi
internal fun QueryStream<*>.abort(): Nothing = throw AbortQueryException(this)

interface QueryStream<T> : AutoCloseable {

    val world: World

    fun collect(collector: QueryCollector<T>)
}

@PublishedApi
internal inline fun <T> QueryStream<*>.unsafeFlow(crossinline block: QueryCollector<T>.() -> Unit): QueryStream<T> = object : QueryStream<T> {
    override val world: World get() = this@unsafeFlow.world
    override fun collect(collector: QueryCollector<T>) = collector.block()
    override fun close(): Unit = this@unsafeFlow.close()
}

@PublishedApi
internal inline fun <T, R> QueryStream<T>.transform(crossinline transform: QueryCollector<R>.(T) -> Unit): QueryStream<R> = unsafeFlow {
    collect { transform(it) }
}

inline fun <T, R> QueryStream<T>.map(crossinline transform: T.() -> R): QueryStream<R> = transform { emit(transform(it)) }

inline fun <T, R : Any> QueryStream<T>.mapNotNull(
    crossinline transform: T.() -> R?
): QueryStream<R> = transform { emit(transform(it) ?: return@transform) }

inline fun <T> QueryStream<T>.onEach(crossinline action: T.() -> Unit): QueryStream<T> = transform {
    action(it)
    emit(it)
}

inline fun <T> QueryStream<T>.forEach(crossinline block: T.() -> Unit) = collect { it.block() }

inline fun <T> QueryStream<T>.collectWhile(crossinline predicate: T.() -> Boolean) = collect {
    if (it.predicate()) abort()
}

inline fun <T> QueryStream<T>.any(crossinline predicate: T.() -> Boolean): Boolean {
    var found = false
    collect {
        if (it.predicate()) {
            found = true
            abort()
        }
    }
    return found
}

inline fun <T> QueryStream<T>.all(crossinline predicate: T.() -> Boolean): Boolean {
    var found = true
    collect {
        if (!it.predicate()) {
            found = false
            abort()
        }
    }
    return found
}

inline fun <T> QueryStream<T>.none(crossinline predicate: T.() -> Boolean): Boolean {
    var found = false
    collect {
        if (it.predicate()) {
            found = true
            abort()
        }
    }
    return !found
}

inline fun <T> QueryStream<T>.filter(crossinline predicate: T.() -> Boolean): QueryStream<T> = transform {
    if (it.predicate()) emit(it)
}

inline fun <T> QueryStream<T>.filterNot(crossinline predicate: T.() -> Boolean): QueryStream<T> = transform {
    if (!it.predicate()) emit(it)
}

inline fun <T> QueryStream<T>.first(): T {
    var result: T? = null
    collect {
        result = it
        abort()
    }
    return result ?: throw NoSuchElementException("QueryStream is empty")
}

inline fun <T> QueryStream<T>.firstOrNull(): T? {
    var result: T? = null
    collect {
        result = it
        abort()
    }
    return result
}

inline fun <T> QueryStream<T>.first(crossinline predicate: T.() -> Boolean): T {
    var result: T? = null
    collect {
        if (it.predicate()) {
            result = it
            abort()
        }
    }
    return result ?: throw NoSuchElementException("No element matching predicate")
}

inline fun <T> QueryStream<T>.firstOrNull(crossinline predicate: T.() -> Boolean): T? {
    var result: T? = null
    collect {
        if (it.predicate()) {
            result = it
            abort()
        }
    }
    return result
}

inline fun <T> QueryStream<T>.last(): T {
    var result: T? = null
    collect { result = it }
    return result ?: throw NoSuchElementException("QueryStream is empty")
}

inline fun <T> QueryStream<T>.lastOrNull(): T? {
    var result: T? = null
    collect { result = it }
    return result
}

inline fun <T> QueryStream<T>.last(crossinline predicate: T.() -> Boolean): T {
    var result: T? = null
    collect {
        if (it.predicate()) {
            result = it
        }
    }
    return result ?: throw NoSuchElementException("No element matching predicate")
}

inline fun <T> QueryStream<T>.lastOrNull(crossinline predicate: T.() -> Boolean): T? {
    var result: T? = null
    collect {
        if (it.predicate()) {
            result = it
        }
    }
    return result
}

inline fun <T> QueryStream<T>.find(crossinline predicate: T.() -> Boolean): T? = firstOrNull(predicate)

inline fun <T> QueryStream<T>.findLast(crossinline predicate: T.() -> Boolean): T? = lastOrNull(predicate)

inline fun <T> QueryStream<T>.single(): T {
    var result: T? = null
    var count = 0
    collect {
        count++
        if (count > 1) throw IllegalArgumentException("QueryStream has more than one element")
        result = it
    }
    return result ?: throw NoSuchElementException("QueryStream is empty")
}

inline fun <T> QueryStream<T>.singleOrNull(): T? {
    var result: T? = null
    var count = 0
    collect {
        count++
        if (count > 1) abort()
        result = it
    }
    return result
}

inline fun <T> QueryStream<T>.single(crossinline predicate: T.() -> Boolean): T {
    var result: T? = null
    var count = 0
    collect {
        if (it.predicate()) {
            count++
            if (count > 1) throw IllegalArgumentException("QueryStream has more than one matching element")
            result = it
        }
    }
    return result ?: throw NoSuchElementException("No element matching predicate")
}

inline fun <T> QueryStream<T>.singleOrNull(crossinline predicate: T.() -> Boolean): T? {
    var result: T? = null
    var count = 0
    collect {
        if (it.predicate()) {
            count++
            if (count > 1) abort()
            result = it
        }
    }
    return result
}

fun <T> QueryStream<T>.count(): Int {
    var count = 0
    collect { count++ }
    return count
}

inline fun <T> QueryStream<T>.count(crossinline predicate: T.() -> Boolean): Int {
    var count = 0
    collect {
        if (it.predicate()) count++
    }
    return count
}

inline fun <T> QueryStream<T>.take(n: Int): QueryStream<T> = unsafeFlow {
    var count = 0
    collect {
        if (count < n) {
            emit(it)
            count++
        } else {
            abort()
        }
    }
}

inline fun <T> QueryStream<T>.takeWhile(crossinline predicate: T.() -> Boolean): QueryStream<T> = transform {
    if (it.predicate()) {
        emit(it)
    } else {
        abort()
    }
}

inline fun <T> QueryStream<T>.drop(n: Int): QueryStream<T> = unsafeFlow {
    var count = 0
    collect {
        if (count >= n) {
            emit(it)
        } else {
            count++
        }
    }
}

inline fun <T> QueryStream<T>.dropWhile(crossinline predicate: T.() -> Boolean): QueryStream<T> = unsafeFlow {
    var dropping = true
    collect {
        if (dropping) {
            if (!it.predicate()) {
                dropping = false
                emit(it)
            }
        } else {
            emit(it)
        }
    }
}

inline fun <T, C : MutableCollection<in T>> QueryStream<T>.toCollection(destination: C): C {
    collect { destination.add(it) }
    return destination
}

inline fun <T> QueryStream<T>.toList(destination: MutableList<T> = mutableListOf()): List<T> = toCollection(destination)

inline fun <T> QueryStream<T>.toSet(destination: MutableSet<T> = mutableSetOf()): Set<T> = toCollection(destination)

inline fun <T, K> QueryStream<T>.distinctBy(crossinline selector: T.() -> K): QueryStream<T> = unsafeFlow {
    val seen = mutableSetOf<K>()
    collect {
        val key = it.selector()
        if (seen.add(key)) {
            emit(it)
        }
    }
}

fun <T> QueryStream<T>.distinct(): QueryStream<T> = distinctBy { this }

inline fun <T, K> QueryStream<T>.groupBy(crossinline keySelector: T.() -> K): Map<K, List<T>> {
    val map = mutableMapOf<K, MutableList<T>>()
    collect {
        val key = it.keySelector()
        map.getOrPut(key) { mutableListOf() }.add(it)
    }
    return map
}

inline fun <T, K, V> QueryStream<T>.groupBy(
    crossinline keySelector: T.() -> K,
    crossinline valueTransform: T.() -> V
): Map<K, List<V>> {
    val map = mutableMapOf<K, MutableList<V>>()
    collect {
        val key = it.keySelector()
        val value = it.valueTransform()
        map.getOrPut(key) { mutableListOf() }.add(value)
    }
    return map
}

inline fun <T, R> QueryStream<T>.fold(initial: R, crossinline operation: R.(T) -> R): R {
    var accumulator = initial
    collect { accumulator = accumulator.operation(it) }
    return accumulator
}

inline fun <T, R> QueryStream<T>.foldIndexed(initial: R, crossinline operation: R.(index: Int, T) -> R): R {
    var accumulator = initial
    var index = 0
    collect {
        accumulator = accumulator.operation(index, it)
        index++
    }
    return accumulator
}

inline fun <T, R> QueryStream<T>.reduce(crossinline operation: T.(T) -> T): T {
    var accumulator: T? = null
    collect {
        accumulator = if (accumulator == null) it else accumulator!!.operation(it)
    }
    return accumulator ?: throw UnsupportedOperationException("Empty QueryStream can't be reduced")
}

inline fun <T> QueryStream<T>.reduceIndexed(crossinline operation: T.(index: Int, T) -> T): T {
    var accumulator: T? = null
    var index = 0
    collect {
        accumulator = if (accumulator == null) {
            index++
            it
        } else {
            val result = accumulator!!.operation(index, it)
            index++
            result
        }
    }
    return accumulator ?: throw UnsupportedOperationException("Empty QueryStream can't be reduced")
}

inline fun <T> QueryStream<T>.partition(crossinline predicate: T.() -> Boolean): Pair<List<T>, List<T>> {
    val first = mutableListOf<T>()
    val second = mutableListOf<T>()
    collect {
        if (it.predicate()) {
            first.add(it)
        } else {
            second.add(it)
        }
    }
    return Pair(first, second)
}

inline fun <T, R> QueryStream<T>.flatMap(crossinline transform: T.() -> Iterable<R>): QueryStream<R> = transform {
    it.transform().forEach { item -> emit(item) }
}

inline fun <T, R> QueryStream<T>.flatMapIndexed(crossinline transform: T.(index: Int) -> Iterable<R>): QueryStream<R> = unsafeFlow {
    var index = 0
    collect {
        it.transform(index).forEach { item -> emit(item) }
        index++
    }
}

inline fun <T, R : Comparable<R>> QueryStream<T>.minBy(crossinline selector: T.() -> R): T? {
    var minValue: R? = null
    var minElement: T? = null
    collect {
        val value = it.selector()
        if (minValue == null || value < minValue!!) {
            minValue = value
            minElement = it
        }
    }
    return minElement
}

inline fun <T, R : Comparable<R>> QueryStream<T>.maxBy(crossinline selector: T.() -> R): T? {
    var maxValue: R? = null
    var maxElement: T? = null
    collect {
        val value = it.selector()
        if (maxValue == null || value > maxValue!!) {
            maxValue = value
            maxElement = it
        }
    }
    return maxElement
}

inline fun <T, R : Comparable<R>> QueryStream<T>.minByOrNull(crossinline selector: T.() -> R): T? = minBy(selector)

inline fun <T, R : Comparable<R>> QueryStream<T>.maxByOrNull(crossinline selector: T.() -> R): T? = maxBy(selector)

inline fun <T : Comparable<T>> QueryStream<T>.min(): T? {
    var minValue: T? = null
    collect {
        if (minValue == null || it < minValue!!) {
            minValue = it
        }
    }
    return minValue
}

inline fun <T : Comparable<T>> QueryStream<T>.max(): T? {
    var maxValue: T? = null
    collect {
        if (maxValue == null || it > maxValue!!) {
            maxValue = it
        }
    }
    return maxValue
}

inline fun <T : Comparable<T>> QueryStream<T>.minOrNull(): T? = min()

inline fun <T : Comparable<T>> QueryStream<T>.maxOrNull(): T? = max()

inline fun <T> QueryStream<T>.sumBy(crossinline selector: T.() -> Int): Int {
    var sum = 0
    collect { sum += it.selector() }
    return sum
}

inline fun <T> QueryStream<T>.sumByDouble(crossinline selector: T.() -> Double): Double {
    var sum = 0.0
    collect { sum += it.selector() }
    return sum
}

inline fun <T> QueryStream<T>.sumByLong(crossinline selector: T.() -> Long): Long {
    var sum = 0L
    collect { sum += it.selector() }
    return sum
}

inline fun <T> QueryStream<T>.sumByFloat(crossinline selector: T.() -> Float): Float {
    var sum = 0f
    collect { sum += it.selector() }
    return sum
}

inline fun <T> QueryStream<T>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    crossinline transform: T.() -> CharSequence = { toString() }
): String {
    val builder = StringBuilder()
    builder.append(prefix)

    var count = 0
    var hasMore = false

    collect {
        count++
        if (limit < 0 || count <= limit) {
            if (count > 1) builder.append(separator)
            val element = transform(it)
            builder.append(element)
        } else if (count == limit + 1) {
            hasMore = true
            abort()
        }
    }

    if (hasMore) {
        if (count > 1) builder.append(separator)
        builder.append(truncated)
    }

    builder.append(postfix)
    return builder.toString()
}

abstract class QueryAssociatedBy<K, E : EntityQueryContext>(val query: Query<E>) : QueryStream<E> by query {

    private val map = mutableMapOf<K, Entity>()

    val keys: Sequence<K> get() = map.keys.asSequence()

    private val insertedObserver: Observer = query.world.observe<Components.OnInserted>().filter(query).exec {
        map[query.context.associateBy()] = entity
    }
    private val removedObserver: Observer = query.world.observe<Components.OnRemoved>().filter(query).exec {
        map.remove(query.context.associateBy())
    }

    protected abstract fun E.associateBy(): K

    init {
        query.collect { map[it.associateBy()] = it.entity }
    }

    operator fun get(key: K): Entity? = map[key]

    operator fun contains(key: K): Boolean = key in map

    fun associate(key: K, collector: QueryCollector<E>) {
        associateEntity(map[key] ?: return, collector)
    }

    private fun associateEntity(entity: Entity, collector: QueryCollector<E>) {
        world.entityService.runOn(entity) {
            query.context.updateCache(this)
            query.context.entityIndex = it
            query.context.batchEntityEditor.entity = entity
            val result = runCatching { collector.emit(query.context) }
            query.context.batchEntityEditor.apply(world)
            if (result.exceptionOrNull() is AbortQueryException) {
                return
            }
        }
    }

    override fun collect(collector: QueryCollector<E>) {
        map.values.forEach { entity: Entity -> associateEntity(entity, collector) }
    }

    override fun close() {
        query.close()
        insertedObserver.close()
        removedObserver.close()
        map.clear()
    }
}

abstract class QueryGroupedBy<K, E : EntityQueryContext>(val query: Query<E>) : QueryStream<QueryGroup<K, E>> {

    private val queryGroups = mutableMapOf<K, QueryGroup<K, E>>()
    override val world: World get() = query.world
    private val insertedObserver: Observer = query.world.observe<Components.OnInserted>().filter(query).exec {
        addEntity(query.context.keySelector(), entity)
    }
    private val removedObserver: Observer = query.world.observe<Components.OnRemoved>().filter(query).exec {
        removeEntity(query.context.keySelector(), entity)
    }

    init {
        query.collect { addEntity(it.keySelector(), it.entity) }
    }

    protected abstract fun E.keySelector(): K

    private fun addEntity(key: K, entity: Entity) {
        queryGroups.getOrPut(key) { QueryGroup(query, key) }.addEntity(entity)
    }

    private fun removeEntity(key: K, entity: Entity) {
        val group = queryGroups[key] ?: return
        group.removeEntity(entity)
        if (group.isEmpty()) {
            queryGroups.remove(key)?.close()
        }
    }

    operator fun get(key: K): QueryGroup<K, E>? = queryGroups[key]

    override fun collect(collector: QueryCollector<QueryGroup<K, E>>) {
        queryGroups.values.forEach { collector.emit(it) }
    }

    override fun close() {
        query.close()
        insertedObserver.close()
        removedObserver.close()
        queryGroups.values.forEach { it.close() }
        queryGroups.clear()
    }

    class QueryGroup<K, E : EntityQueryContext> internal constructor(val query: Query<E>, val key: K) : QueryStream<E> {
        private val entities = Entities()
        override val world: World get() = query.world

        internal fun isEmpty(): Boolean = entities.size == 0

        internal fun addEntity(entity: Entity) {
            entities.add(entity)
        }

        internal fun removeEntity(entity: Entity) {
            entities.remove(entity)
        }

        override fun collect(collector: QueryCollector<E>) {
            entities.forEach { entity ->
                world.entityService.runOn(entity) {
                    query.context.updateCache(this)
                    query.context.entityIndex = it
                    query.context.batchEntityEditor.entity = entity
                    val result = runCatching { collector.emit(query.context) }
                    query.context.batchEntityEditor.apply(world)
                    if (result.exceptionOrNull() is AbortQueryException) {
                        return
                    }
                }
            }
        }

        override fun close() {
        }
    }
}

inline fun <K, E : EntityQueryContext> Query<E>.associatedBy(crossinline associateBy: E.() -> K): QueryAssociatedBy<K, E> {
    return object : QueryAssociatedBy<K, E>(this) {
        override fun E.associateBy(): K = associateBy()
    }
}

fun <K, E : EntityQueryContext> Query<E>.groupedBy(keySelector: E.() -> K): QueryGroupedBy<K, E> {
    return object : QueryGroupedBy<K, E>(this) {
        override fun E.keySelector(): K = keySelector()
    }
}

class SingleQuery<E : EntityQueryContext>(private val context: E) : QueryStream<E> {

    private val archetypes = ObjectFastList<Archetype>()

    override val world: World get() = context.world

    init {
        context.buildArchetype(archetypes::insertLast)
    }

    override fun collect(collector: QueryCollector<E>) {
        if (archetypes.isEmpty()) return
        archetypes.forEach { archetype ->
            context.updateCache(archetype)
            for (entityIndex in 0 until archetype.table.entities.size) {
                context.entityIndex = entityIndex
                context.batchEntityEditor.entity = context.entity
                val result = runCatching { collector.emit(context) }
                check(entityIndex == context.entityIndex)
                context.batchEntityEditor.apply(context.world)
                if (result.exceptionOrNull() is AbortQueryException) {
                    return
                }
                result.getOrThrow()
            }
        }
    }

    override fun close() {
    }
}

class Query<E : EntityQueryContext>(@PublishedApi internal val context: E) : QueryStream<E> {

    override val world: World get() = context.world

    private val family: Family = context.build()

    val size: Int get() = family.size

    internal operator fun contains(archetype: Archetype): Boolean {
        return family.familyMatcher.match(archetype)
    }

    override fun collect(collector: QueryCollector<E>) {
        if (family.archetypes.isEmpty()) return
        family.archetypes.forEach { archetype ->
            context.updateCache(archetype)
            for (entityIndex in 0 until archetype.table.entities.size) {
                context.entityIndex = entityIndex
                context.batchEntityEditor.entity = context.entity
                val result = runCatching { collector.emit(context) }
                check(entityIndex == context.entityIndex)
                context.batchEntityEditor.apply(context.world)
                if (result.exceptionOrNull() is AbortQueryException) {
                    return
                }
                result.getOrThrow()
            }
        }
    }

    override fun close() {
    }
}
