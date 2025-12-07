package cn.jzl.ecs.query

import cn.jzl.ecs.*
import kotlin.reflect.KProperty

abstract class EntityQueryContext(override val world: World, private val involvePrefab: Boolean = false) : AccessorOperations(), WorldOwner {

    @PublishedApi
    internal val properties = mutableMapOf<String, Accessor>()

    private var archetype: Archetype = world.archetypeService.rootArchetype

    @PublishedApi
    internal val batchEntityEditor = BatchEntityEditor(world, Entity.ENTITY_INVALID)

    @PublishedApi
    internal var entityIndex: Int = -1

    val entity: Entity get() = archetype.table.entities[entityIndex]
    val entityType: EntityType get() = archetype.entityType

    fun getRelationUp(kind: ComponentId): Entity? = world.relationService.getRelationUp(entity, kind)
    inline fun <reified K> getRelationUp(): Entity? = getRelationUp(relations.id<K>())
    fun getRelationDown(kind: ComponentId): SingleQuery<EntityQueryContext>? = world.relationService.getRelationDown(entity, kind)
    inline fun <reified K> getRelationDown(): SingleQuery<Shorthand1Query<K>>? = world.relationService.getRelationDown(entity)

    inline fun <reified K> getRelations(): Sequence<RelationWithData<K>> {
        val kind = relations.id<K>()
        return entityType.filter { it.kind == kind }.map { RelationWithData(it, world.relationService.getRelation(entity, it) as K) }
    }

    internal fun build(): Family = world.familyService.family { build() }

    internal fun buildArchetype(receiver: (Archetype) -> Unit): Unit = world.familyService.buildArchetype(receiver) { build() }

    private fun FamilyMatcher.FamilyBuilder.build() {
        val families = mutableListOf<FamilyMatching>()
        if (!involvePrefab) {
            not { component<Components.Prefab>() }
        }
        accessors.asSequence().filterIsInstance<FamilyMatching>().forEach {
            if (!it.isMarkedNullable) {
                it.run { matching() }
                return@forEach
            }
            if (it.optionalGroup == OptionalGroup.One) {
                families.add(it)
            }
        }
        if (families.isNotEmpty()) or { families.forEach { it.run { matching() } } }
        configure()
    }

    protected open fun FamilyMatcher.FamilyBuilder.configure(): Unit = Unit

    internal fun updateCache(archetype: Archetype) {
        this.archetype = archetype
        accessors.forEach { if (it is CachedAccessor) it.updateCache(archetype) }
    }

    operator fun <A : Accessor> A.provideDelegate(thisRef: EntityQueryContext, property: KProperty<*>): A {
        properties[property.name] = this
        return this
    }

    fun removeRelation(relation: Relation) {
        batchEntityEditor.removeRelation(entity, relation)
    }
}

abstract class ShorthandQuery(world: World) : EntityQueryContext(world)
abstract class Shorthand1Query<C>(world: World) : ShorthandQuery(world) {
    abstract val component1: C
    operator fun component1(): C = component1
}

abstract class Shorthand2Query<C1, C2>(world: World) : Shorthand1Query<C1>(world) {
    abstract val component2: C2
    operator fun component2(): C2 = component2
}

abstract class Shorthand3Query<C1, C2, C3>(world: World) : Shorthand2Query<C1, C2>(world) {
    abstract val component3: C3
    operator fun component3(): C3 = component3
}

abstract class Shorthand4Query<C1, C2, C3, C4>(world: World) : Shorthand3Query<C1, C2, C3>(world) {
    abstract val component4: C4
    operator fun component4(): C4 = component4
}

abstract class Shorthand5Query<C1, C2, C3, C4, C5>(world: World) : Shorthand4Query<C1, C2, C3, C4>(world) {
    abstract val component5: C5
    operator fun component5(): C5 = component5
}

abstract class Shorthand6Query<C1, C2, C3, C4, C5, C6>(world: World) : Shorthand5Query<C1, C2, C3, C4, C5>(world) {
    abstract val component6: C6
    operator fun component6(): C6 = component6
}

// 普通查询
inline fun <reified E : EntityQueryContext> World.query(noinline factory: World.() -> E): Query<E> = queryService.query(factory)

// 单次使用的查询
inline fun <reified E : EntityQueryContext> World.singleQuery(noinline factory: World.() -> E): SingleQuery<E> = queryService.singleQuery(factory)

inline fun <reified C : Any> World.shorthandQuery(
    size1: QueryShorthands.Size1? = null, noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<Shorthand1Query<C>> = query<Shorthand1Query<C>> {
    object : Shorthand1Query<C>(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            configure()
        }

        override val component1: C by component<C>()
    }
}

inline fun <reified C1 : Any, reified C2 : Any> World.shorthandQuery(
    size2: QueryShorthands.Size2? = null, noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<Shorthand2Query<C1, C2>> = query<Shorthand2Query<C1, C2>> {
    object : Shorthand2Query<C1, C2>(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            configure()
        }

        override val component1: C1 by component<C1>()
        override val component2: C2 by component<C2>()
    }
}

inline fun <reified C1 : Any, reified C2 : Any, reified C3 : Any> World.shorthandQuery(
    size3: QueryShorthands.Size3? = null, noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<Shorthand3Query<C1, C2, C3>> = query<Shorthand3Query<C1, C2, C3>> {
    object : Shorthand3Query<C1, C2, C3>(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            configure()
        }

        override val component1: C1 by component<C1>()
        override val component2: C2 by component<C2>()
        override val component3: C3 by component<C3>()
    }
}

inline fun <reified C1 : Any, reified C2 : Any, reified C3 : Any, reified C4 : Any> World.shorthandQuery(
    size4: QueryShorthands.Size4? = null, noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<Shorthand4Query<C1, C2, C3, C4>> = query<Shorthand4Query<C1, C2, C3, C4>> {
    object : Shorthand4Query<C1, C2, C3, C4>(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            configure()
        }

        override val component1: C1 by component<C1>()
        override val component2: C2 by component<C2>()
        override val component3: C3 by component<C3>()
        override val component4: C4 by component<C4>()
    }
}

inline fun <reified C1 : Any, reified C2 : Any, reified C3 : Any, reified C4 : Any, reified C5 : Any> World.shorthandQuery(
    size5: QueryShorthands.Size5? = null, noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<Shorthand5Query<C1, C2, C3, C4, C5>> = query<Shorthand5Query<C1, C2, C3, C4, C5>> {
    object : Shorthand5Query<C1, C2, C3, C4, C5>(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            configure()
        }

        override val component1: C1 by component<C1>()
        override val component2: C2 by component<C2>()
        override val component3: C3 by component<C3>()
        override val component4: C4 by component<C4>()
        override val component5: C5 by component<C5>()
    }
}

inline fun <reified C1 : Any, reified C2 : Any, reified C3 : Any, reified C4 : Any, reified C5 : Any, reified C6 : Any> World.shorthandQuery(
    size6: QueryShorthands.Size6? = null, noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<Shorthand6Query<C1, C2, C3, C4, C5, C6>> = query<Shorthand6Query<C1, C2, C3, C4, C5, C6>> {
    object : Shorthand6Query<C1, C2, C3, C4, C5, C6>(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            configure()
        }

        override val component1: C1 by component<C1>()
        override val component2: C2 by component<C2>()
        override val component3: C3 by component<C3>()
        override val component4: C4 by component<C4>()
        override val component5: C5 by component<C5>()
        override val component6: C6 by component<C6>()
    }
}

object QueryShorthands {
    sealed class Size1
    sealed class Size2
    sealed class Size3
    sealed class Size4
    sealed class Size5
    sealed class Size6
}