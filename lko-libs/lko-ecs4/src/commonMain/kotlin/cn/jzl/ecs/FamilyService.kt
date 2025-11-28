package cn.jzl.ecs

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.LongFastList

class FamilyService(private val world: World) : FamilyMatcher.FamilyMatchScope {

    private val componentMap = mutableMapOf<Relation, BitSet>()
    override val allArchetypeBits: BitSet = BitSet()

    private val families = mutableMapOf<String, Family>()
    private val emptyBits = BitSet()

    private inline val componentService: ComponentService get() = world.componentService
    private inline val archetypeService: ArchetypeService get() = world.archetypeService

    internal fun registerArchetype(archetype: Archetype) {
        allArchetypeBits.set(archetype.id)
        fun set(relation: Relation): Unit = componentMap.getOrPut(relation) { BitSet.Companion() }.set(archetype.id)
        archetype.entityType.forEach { relation: Relation ->
            if (relation.isRelation()) {
                set(Relation(relation.kind, componentService.components.any))
                set(Relation(componentService.components.any, relation.target))
            }
            set(relation)
        }
        families.values.forEach { if (it.familyMatcher.match(archetype)) it.addArchetype(archetype) }
    }

    private fun Relation.isRelation(): Boolean = world.componentService.components.componentId != target

    override fun getArchetypeBits(relation: Relation): BitSet = componentMap[relation] ?: emptyBits

    fun family(block: FamilyMatcher.FamilyBuilder.() -> Unit): Family {
        val keys = LongFastList()
        val familyMatcher = and(world, keys, block)
        val keyString = keys.joinToString(",") { it.toString(36) }
        return families.getOrPut(keyString) {
            val family = Family(world, familyMatcher)
            familyMatcher.run { getArchetypeBits() }.map { archetypeService[it] }.forEach(family::addArchetype)
            family
        }
    }
}