@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.LongFastList
import cn.jzl.datastructure.list.ObjectFastList
import cn.jzl.di.*
import cn.jzl.ecs.observers.ObserveService
import cn.jzl.ecs.query.QueryService

typealias ComponentId = Entity

inline fun <reified C> ComponentProvider.id(): ComponentId = getOrRegisterEntityForClass(C::class)

inline fun <reified C> ComponentProvider.configure(configuration: ComponentConfigureContext.(ComponentId) -> Unit): ComponentId {
    val componentEntity = getOrRegisterEntityForClass(C::class)
    world.entityService.configure(componentEntity) {
        val componentConfigureContext = ComponentConfigureContext(this)
        componentConfigureContext.configuration(it)
    }
    return componentEntity
}


private inline fun composite(
    world: World,
    keys: LongFastList,
    key: Long,
    block: FamilyMatcher.FamilyBuilder.() -> Unit,
    factory: (List<FamilyMatcher>) -> FamilyMatcher
): FamilyMatcher {
    val familyMatchers = mutableListOf<FamilyMatcher>()
    keys.add(key)
    val familyBuilder = object : FamilyMatcher.FamilyBuilder {
        override val keys: LongFastList get() = keys
        override val world: World get() = world
        override fun matcher(familyMatcher: FamilyMatcher) {
            familyMatchers.add(familyMatcher)
        }
    }
    familyBuilder.block()
    return factory(familyMatchers)
}

fun and(world: World, keys: LongFastList, block: FamilyMatcher.FamilyBuilder.() -> Unit): FamilyMatcher {
    val result = BitSet()
    return composite(world, keys, 0, block) { familyMatchers ->
        object : FamilyMatcher {
            override fun FamilyMatcher.FamilyMatchScope.getArchetypeBits(): BitSet {
                result.clear()
                familyMatchers.forEachIndexed { index, familyMatcher ->
                    val bits = familyMatcher.run { getArchetypeBits() }
                    if (index == 0) result.or(bits) else result.and(bits)
                }
                return result
            }

            override fun match(archetype: Archetype): Boolean = familyMatchers.all { it.match(archetype) }
        }
    }
}

fun or(world: World, keys: LongFastList, block: FamilyMatcher.FamilyBuilder.() -> Unit): FamilyMatcher {
    val result = BitSet()
    return composite(world, keys, 1, block) { familyMatchers ->
        object : FamilyMatcher {
            override fun match(archetype: Archetype): Boolean = familyMatchers.any { it.match(archetype) }

            override fun FamilyMatcher.FamilyMatchScope.getArchetypeBits(): BitSet {
                result.clear()
                return familyMatchers.fold(result) { acc, familyMatcher ->
                    val bits = familyMatcher.run { getArchetypeBits() }
                    acc.or(bits)
                    result
                }
            }
        }
    }
}

fun xor(world: World, keys: LongFastList, block: FamilyMatcher.FamilyBuilder.() -> Unit): FamilyMatcher {
    val result = BitSet()
    val temp = BitSet(0)
    val m2 = BitSet()
    val allArchetypeResults = ObjectFastList<BitSet>()

    return composite(world, keys, 2, block) { familyMatchers ->
        object : FamilyMatcher {
            override fun match(archetype: Archetype): Boolean =
                familyMatchers.count { it.match(archetype) } == 1

            override fun FamilyMatcher.FamilyMatchScope.getArchetypeBits(): BitSet {
                result.clear()
                when (familyMatchers.size) {
                    0 -> return result
                    1 -> return familyMatchers.single().run { getArchetypeBits() }
                    2 -> {
                        // 两个匹配器的特殊情况 - 使用原生 XOR 操作
                        val first = familyMatchers.component1().run { getArchetypeBits() }
                        val second = familyMatchers.component2().run { getArchetypeBits() }
                        result.or(first)
                        result.xor(second)
                        return result
                    }
                    else -> {
                        // 多个匹配器的通用情况
                        allArchetypeResults.clear()

                        // 保持您的高性能批量插入操作
                        allArchetypeResults.safeInsertLast(familyMatchers.size) {
                            familyMatchers.forEach { unsafeInsert(it.run { getArchetypeBits() }) }
                        }

                        // 计算所有位的 OR (至少在一个匹配器中存在的位)
                        for (i in 0 until allArchetypeResults.size) {
                            result.or(allArchetypeResults[i])
                        }

                        // 计算 M2 (在至少两个匹配器中存在的位)
                        m2.clear()
                        for (i in 0 until allArchetypeResults.size) {
                            val first = allArchetypeResults[i]
                            for (j in i + 1 until allArchetypeResults.size) {
                                val second = allArchetypeResults[j]
                                temp.clear()
                                temp.or(first)
                                temp.and(second)
                                m2.or(temp)
                            }
                        }

                        // 最终结果: OR_all AND NOT M2
                        result.andNot(m2)
                        return result
                    }
                }
            }
        }
    }
}

fun not(world: World, keys: LongFastList, block: FamilyMatcher.FamilyBuilder.() -> Unit): FamilyMatcher {
    val result = BitSet()
    return composite(world, keys, 3, block) { familyMatchers ->
        object : FamilyMatcher {
            override fun match(archetype: Archetype): Boolean = familyMatchers.none { it.match(archetype) }
            override fun FamilyMatcher.FamilyMatchScope.getArchetypeBits(): BitSet {
                result.clear()
                familyMatchers.fold(result) { acc, familyMatcher ->
                    val bits = familyMatcher.run { getArchetypeBits() }
                    acc.or(bits)
                    acc
                }
                result.xor(allArchetypeBits)
                return result
            }
        }
    }
}

fun FamilyMatcher.FamilyBuilder.and(block: FamilyMatcher.FamilyBuilder.() -> Unit) = matcher(and(world, this.keys, block))
fun FamilyMatcher.FamilyBuilder.or(block: FamilyMatcher.FamilyBuilder.() -> Unit) = matcher(or(world, keys, block))
fun FamilyMatcher.FamilyBuilder.xor(block: FamilyMatcher.FamilyBuilder.() -> Unit) = matcher(xor(world, keys, block))
fun FamilyMatcher.FamilyBuilder.not(block: FamilyMatcher.FamilyBuilder.() -> Unit) = matcher(not(world, keys, block))

fun FamilyMatcher.FamilyBuilder.relation(kind: ComponentId, target: Entity): Unit = relation(Relation(kind, target))

fun FamilyMatcher.FamilyBuilder.relation(relation: Relation) {
    keys.add(relation.data)
    matcher(object : FamilyMatcher {

        override fun match(archetype: Archetype): Boolean = relation in archetype

        override fun FamilyMatcher.FamilyMatchScope.getArchetypeBits(): BitSet = getArchetypeBits(relation)
    })
}

inline fun <reified C : Any> FamilyMatcher.FamilyBuilder.component(): Unit = component(world.componentService.id<C>())

inline fun FamilyMatcher.FamilyBuilder.component(componentId: ComponentId) {
    relation(componentId, world.componentService.components.componentId)
}

fun FamilyMatcher.FamilyBuilder.target(target: Entity) {
    val relation = Relation(world.componentService.components.any, target)
    keys.add(relation.data)
    matcher(object : FamilyMatcher {
        override fun match(archetype: Archetype): Boolean = archetype.entityType.any { it.target == target }

        override fun FamilyMatcher.FamilyMatchScope.getArchetypeBits(): BitSet = getArchetypeBits(relation)
    })
}

fun FamilyMatcher.FamilyBuilder.kind(kind: ComponentId) {
    val relation = Relation(kind, world.componentService.components.any)
    keys.add(relation.data)
    matcher(object : FamilyMatcher {
        override fun match(archetype: Archetype): Boolean = archetype.entityType.any { it.kind == kind }

        override fun FamilyMatcher.FamilyMatchScope.getArchetypeBits(): BitSet = getArchetypeBits(relation)
    })
}

inline fun World.entity(entity: Entity, configuration: EntityUpdateContext.(Entity) -> Unit) = entityService.configure(entity, configuration)
inline fun World.entity(entityId: Int, configuration: EntityCreateContext.(Entity) -> Unit): Entity = entityService.create(entityId, configuration)
inline fun World.entity(configuration: EntityCreateContext.(Entity) -> Unit): Entity = entityService.create(configuration)

inline fun World.childOf(entity: Entity, configuration: EntityCreateContext.(Entity) -> Unit): Entity = entity {
    configuration(it)
    it.addRelation(componentService.components.childOf, entity)
}

inline fun World.childOf(entity: Entity, entityId: Int, configuration: EntityCreateContext.(Entity) -> Unit): Entity = entity(entityId) {
    configuration(it)
    it.addRelation(componentService.components.childOf, entity)
}

inline fun <reified C> World.componentId(): ComponentId = componentService.id<C>()
inline fun <reified C> World.component(): Relation = componentService.component<C>()
inline fun <reified C> World.componentId(configuration: ComponentConfigureContext.(ComponentId) -> Unit): ComponentId {
    return componentService.configure<C>(configuration)
}

fun world(configuration: DIMainBuilder.() -> Unit): World {
    val di = DI {
        this bind singleton { World(this.di) }

        this bind singleton { new(::EntityService) }
        this bind singleton { new(::EntityStoreImpl) }

        this bind singleton { new(::ArchetypeService) }
        this bind singleton { new(::ComponentService) }
        this bind singleton { new(::Components) }

        this bind singleton { new(::RelationService) }
        this bind singleton { new(::FamilyService) }

        this bind singleton { new(::QueryService) }
        this bind singleton { new(::ObserveService) }

        configuration()
    }
    val world by di.instance<World>()

    return world
}