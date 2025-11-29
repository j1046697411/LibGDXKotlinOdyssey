package cn.jzl.ecs

import cn.jzl.datastructure.list.ObjectFastList

class ArchetypeService(private val world: World) : ArchetypeProvider {
    private val typeToArchetypeMap = mutableMapOf<EntityType, Archetype>()
    private val archetypes = ObjectFastList<Archetype>()

    override val rootArchetype: Archetype = getArchetype(EntityType.ENTITY_TYPE_EMPTY)

    override fun getArchetype(entityType: EntityType): Archetype {
        return typeToArchetypeMap.getOrPut(entityType) { createArchetype(entityType) }
    }

    private fun createArchetype(entityType: EntityType): Archetype {
        val archetype = Archetype(
            archetypes.size,
            entityType,
            this,
            world.componentService,
            world.entityService
        )
        archetypes.insertLast(archetype)
        world.familyService.registerArchetype(archetype)
        return archetype
    }

    @PublishedApi
    internal operator fun get(id: Int): Archetype {
        check(id in archetypes.indices) { "archetype id $id is out of bounds" }
        return archetypes[id]
    }
}