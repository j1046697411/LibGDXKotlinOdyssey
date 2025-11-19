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
        // 使用 archetypes.size 而不是 typeToArchetypeMap.size，确保 ID 和索引一致
        val archetype = Archetype(archetypes.size, entityType, this, world.componentService)
        // 先添加到 archetypes，确保 ID 和索引一致
        archetypes.insertLast(archetype)
        // 然后注册到 familyService
        world.familyService.registerArchetype(archetype)
        return archetype
    }

    @PublishedApi
    internal operator fun get(id: Int): Archetype {
        check(id in archetypes.indices) { "archetype id $id is out of bounds" }
        return archetypes[id]
    }
}