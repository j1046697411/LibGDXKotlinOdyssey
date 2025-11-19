package cn.jzl.ecs

interface ArchetypeProvider {
    val rootArchetype: Archetype
    fun getArchetype(entityType: EntityType): Archetype
}