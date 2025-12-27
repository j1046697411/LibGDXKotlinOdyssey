@file:Suppress("unused", "UnusedPrivateMember",)

package cn.jzl.sect.ecs.equipment

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.firstOrNull
import cn.jzl.ecs.query.forEach
import cn.jzl.sect.ecs.inventory.InventoryService
import cn.jzl.sect.ecs.item.ItemService
import cn.jzl.sect.ecs.attribute.AttributeProvider
import cn.jzl.sect.ecs.attribute.AttributeService
import cn.jzl.sect.ecs.attribute.AttributeValue
import cn.jzl.sect.ecs.attribute.SectAttributes
import cn.jzl.sect.ecs.attribute.attributes
import cn.jzl.sect.ecs.character.characterAddon
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.effects.effectAddon
import cn.jzl.sect.ecs.inventory.inventoryAddon
import cn.jzl.sect.ecs.item.itemAddon
import cn.jzl.sect.ecs.upgradeable.levelingAddon

sealed class Equipment

enum class EquipmentType {
    WEAPON,
    ARMOR,
    ACCESSORY
}

enum class EquipmentQuality {
    COMMON,
    RARE
}

enum class EquipmentSlot {
    WEAPON,
    HELMET,
    ARMOR,
    LEGS,
    BOOTS,
    GLOVES,
    ACCESSORY1,
    ACCESSORY2
}

sealed class EquipmentException(message: String) : Exception(message) {
    data class LevelRequirementNotMet(
        val required: Long,
        val actual: Long
    ) : EquipmentException(
        "Level requirement not met: required $required, actual $actual"
    )

    data class SlotTypeMismatch(
        val equipmentType: EquipmentType,
        val slot: EquipmentSlot
    ) : EquipmentException(
        "Slot type mismatch: equipment type $equipmentType cannot be equipped to slot $slot"
    )

    data class EquipmentNotInInventory(
        val equipment: Entity,
        val character: Entity
    ) : EquipmentException(
        "Equipment $equipment is not in character $character's inventory"
    )

    data class NoEquipmentInSlot(
        val character: Entity,
        val slot: EquipmentSlot
    ) : EquipmentException(
        "No equipment in slot $slot for character $character"
    )

    data class EquipmentLocked(
        val equipment: Entity
    ) : EquipmentException(
        "Equipment $equipment is locked"
    )

    data class EquipmentEquipped(
        val equipment: Entity
    ) : EquipmentException(
        "Equipment $equipment is already equipped"
    )

    data class InsufficientMaterials(
        val required: Map<Entity, Int>,
        val actual: Map<Entity, Int>
    ) : EquipmentException(
        "Insufficient materials for enhancement"
    )

    data class EnhancementFailed(
        val equipment: Entity,
        val level: Long
    ) : EquipmentException(
        "Enhancement failed for equipment $equipment at level $level"
    )
}

fun interface EquipmentRequirement {
    fun EntityRelationContext.check(character: Entity, equipment: Entity): Boolean
}

fun interface EquipmentPrefabContext {
    fun attribute(attribute: Entity, value: AttributeValue)
}

class EquipmentService(world: World) : EntityRelationContext(world) {

    internal val itemService by world.di.instance<ItemService>()
    internal val attributeService by world.di.instance<AttributeService>()
    internal val inventoryService by world.di.instance<InventoryService>()
    internal val attributes by world.di.instance<SectAttributes>()

    @ECSDsl
    fun equipmentPrefab(
        name: String,
        type: EquipmentType,
        equipmentRequirement: EquipmentRequirement? = null,
        configure: EquipmentPrefabContext.() -> Unit = {}
    ): Entity = itemService.itemPrefab(Named(name)) {
        it.addComponent(type)
        it.addTag<Equipment>()
        val equipmentPrefabContext = EquipmentPrefabContext { attribute, value ->
            attributeService.setAttributeValue(this, it, attribute, value)
        }
        equipmentPrefabContext.configure()
        if (equipmentRequirement != null) {
            it.addComponent(equipmentRequirement)
        }
    }

    fun canEquip(character: Entity, equipment: Entity): Boolean {
        if (equipment.getRelationUp<OwnedBy>() != character) return false
        if (!equipment.hasComponent<Equipment>()) return false
        val equipmentRequirement = equipment.getComponent<EquipmentRequirement?>()
        return equipmentRequirement?.run { check(character, equipment) } ?: true
    }

    fun getEquippedItem(character: Entity, slot: EquipmentSlot): Entity? {
        return character.getRelationDown<EquipmentSlot>().firstOrNull { component1 == slot }?.entity
    }

    fun getAllEquippedItems(character: Entity): Map<EquipmentSlot, Entity> {
        val result = mutableMapOf<EquipmentSlot, Entity>()
        character.getRelationDown<EquipmentSlot>().forEach { result[component1] = entity }
        return result
    }

    fun equip(character: Entity, equipment: Entity, slot: EquipmentSlot) {
        if (!canEquip(character, equipment)) {
            throw EquipmentException.EquipmentNotInInventory(equipment, character)
        }
        val equipmentType = equipment.getComponent<EquipmentType?>()
            ?: throw IllegalArgumentException("Equipment missing EquipmentType component: $equipment")
        if (!isSlotCompatible(equipmentType, slot)) {
            throw EquipmentException.SlotTypeMismatch(equipmentType, slot)
        }
        val existingEquipment = getEquippedItem(character, slot)
        if (existingEquipment != null) {
            unequip(character, slot)
        }
        world.entity(equipment) { it.addRelation(character, slot) }
    }

    fun unequip(character: Entity, slot: EquipmentSlot) {
        val equipment = getEquippedItem(character, slot) ?: throw EquipmentException.NoEquipmentInSlot(character, slot)
        world.entity(equipment) {
            it.removeRelation<EquipmentSlot>(character)
        }
    }

    private fun isSlotCompatible(equipmentType: EquipmentType, slot: EquipmentSlot): Boolean {
        return when (slot) {
            EquipmentSlot.WEAPON -> equipmentType == EquipmentType.WEAPON
            EquipmentSlot.HELMET, EquipmentSlot.ARMOR,
            EquipmentSlot.LEGS, EquipmentSlot.BOOTS,
            EquipmentSlot.GLOVES -> equipmentType == EquipmentType.ARMOR

            EquipmentSlot.ACCESSORY1,
            EquipmentSlot.ACCESSORY2 -> equipmentType == EquipmentType.ACCESSORY
        }
    }


    fun isEquipped(character: Entity, equipment: Entity): Boolean {
        return equipment.hasRelation<EquipmentSlot>(character)
    }
}

class EquipmentAttributeProvider(world: World) : AttributeProvider, EntityRelationContext(world) {

    private val equipmentService by world.di.instance<EquipmentService>()

    override fun getAttributeValue(attributeService: AttributeService, entity: Entity, attribute: Entity): AttributeValue {
        return equipmentService.getAllEquippedItems(entity).values.fold(AttributeValue.zero) { acc, equipment ->
            val attributeValue = attributeService.getTotalAttributeValue(equipment, attribute)
            acc + attributeValue
        }
    }
}


val equipmentAddon = createAddon("equipment") {
    install(itemAddon)
    install(effectAddon)
    install(inventoryAddon)
    install(levelingAddon)
    install(characterAddon)
    attributes { provider { EquipmentAttributeProvider(this.world) } }

    injects { this bind singleton { new(::EquipmentService) } }
    components {
        world.componentId<EquipmentType>()
        world.componentId<EquipmentQuality>()
        world.componentId<EquipmentRequirement>()
    }
}