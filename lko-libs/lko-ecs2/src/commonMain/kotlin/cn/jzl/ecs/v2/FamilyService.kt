package cn.jzl.ecs.v2

class FamilyService(private val world: World) : Sequence<Family> {

    private val families = mutableMapOf<FamilyDefinition, Family>()

    init {
        world.entityService.onEntityCreate.add { entityChanges(it) }
        world.entityService.onEntityUpdate.add { entityChanges(it) }
        world.entityService.onEntityDestroy.add { entityChanges(it) }
    }

    internal fun entityChanges(entity: Entity) {
        if (families.isEmpty()) return
        families.values.forEach { family -> family.entityChanged(entity) }
    }

    fun family(configuration: FamilyDefinition.() -> Unit): Family {
        val familyDefinition = FamilyDefinition().apply(configuration)
        return families.getOrPut(familyDefinition) { createFamily(familyDefinition) }
    }

    private fun createFamily(familyDefinition: FamilyDefinition): Family {
        val family = Family(world, familyDefinition)
        world.entityService.entities.forEach { entity -> family.entityChanged(entity) }
        return family
    }

    override fun iterator(): Iterator<Family> = families.values.iterator()
}