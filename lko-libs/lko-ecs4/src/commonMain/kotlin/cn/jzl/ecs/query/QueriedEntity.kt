package cn.jzl.ecs.query

import cn.jzl.ecs.*
import kotlin.reflect.KProperty

abstract class QueriedEntity(val world: World) : AccessorOperations() {

    @PublishedApi
    internal val properties = mutableMapOf<String, Accessor>()

    private var archetype: Archetype = world.archetypeService.rootArchetype

    @PublishedApi
    internal val batchEntityEditor = BatchEntityEditor(world, Entity.ENTITY_INVALID)

    @PublishedApi
    internal var entityIndex: Int = -1

    val entity: Entity get() = archetype.table.entities[entityIndex]
    val entityType: EntityType get() = archetype.entityType

    fun build(): Family = world.familyService.family {
        val families = mutableListOf<FamilyMatching>()
        accessors.asSequence().filterIsInstance<FamilyMatching>()
            .forEach {
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

    operator fun <A : Accessor> A.provideDelegate(thisRef: QueriedEntity, property: KProperty<*>): A {
        properties[property.name] = this
        return this
    }
}

abstract class ShorthandQuery(world: World) : QueriedEntity(world)
abstract class ShorthandQuery1<C>(world: World) : ShorthandQuery(world) {
    abstract val component1: C
    operator fun component1(): C = component1
}

abstract class ShorthandQuery2<C1, C2>(world: World) : ShorthandQuery1<C1>(world) {
    abstract val component2: C2
    operator fun component2(): C2 = component2
}

abstract class ShorthandQuery3<C1, C2, C3>(world: World) : ShorthandQuery2<C1, C2>(world) {
    abstract val component3: C3
    operator fun component3(): C3 = component3
}

abstract class ShorthandQuery4<C1, C2, C3, C4>(world: World) : ShorthandQuery3<C1, C2, C3>(world) {
    abstract val component4: C4
    operator fun component4(): C4 = component4
}

abstract class ShorthandQuery5<C1, C2, C3, C4, C5>(world: World) : ShorthandQuery4<C1, C2, C3, C4>(world) {
    abstract val component5: C5
    operator fun component5(): C5 = component5
}

abstract class ShorthandQuery6<C1, C2, C3, C4, C5, C6>(world: World) : ShorthandQuery5<C1, C2, C3, C4, C5>(world) {
    abstract val component6: C6
    operator fun component6(): C6 = component6
}

inline fun <reified E : QueriedEntity> World.query(factory: World.() -> E): Query<E> = queryService.query(factory)

inline fun <reified C : Any> World.shorthandQuery(
    size1: QueryShorthands.Size1? = null,
    noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<ShorthandQuery1<C>> = query<ShorthandQuery1<C>> {
    object : ShorthandQuery1<C>(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            configure()
        }

        override val component1: C by component<C>()
    }
}

inline fun <reified C1 : Any, reified C2 : Any> World.shorthandQuery(
    size2: QueryShorthands.Size2? = null,
    noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<ShorthandQuery2<C1, C2>> = query<ShorthandQuery2<C1, C2>> {
    object : ShorthandQuery2<C1, C2>(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            configure()
        }

        override val component1: C1 by component<C1>()
        override val component2: C2 by component<C2>()
    }
}

inline fun <reified C1 : Any, reified C2 : Any, reified C3 : Any> World.shorthandQuery(
    size3: QueryShorthands.Size3? = null,
    noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<ShorthandQuery3<C1, C2, C3>> = query<ShorthandQuery3<C1, C2, C3>> {
    object : ShorthandQuery3<C1, C2, C3>(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            configure()
        }

        override val component1: C1 by component<C1>()
        override val component2: C2 by component<C2>()
        override val component3: C3 by component<C3>()
    }
}

inline fun <reified C1 : Any, reified C2 : Any, reified C3 : Any, reified C4 : Any> World.shorthandQuery(
    size4: QueryShorthands.Size4? = null,
    noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<ShorthandQuery4<C1, C2, C3, C4>> = query<ShorthandQuery4<C1, C2, C3, C4>> {
    object : ShorthandQuery4<C1, C2, C3, C4>(this) {
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
    size5: QueryShorthands.Size5? = null,
    noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<ShorthandQuery5<C1, C2, C3, C4, C5>> = query<ShorthandQuery5<C1, C2, C3, C4, C5>> {
    object : ShorthandQuery5<C1, C2, C3, C4, C5>(this) {
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
    size6: QueryShorthands.Size6? = null,
    noinline configure: FamilyMatcher.FamilyBuilder.() -> Unit = {}
): Query<ShorthandQuery6<C1, C2, C3, C4, C5, C6>> = query<ShorthandQuery6<C1, C2, C3, C4, C5, C6>> {
    object : ShorthandQuery6<C1, C2, C3, C4, C5, C6>(this) {
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