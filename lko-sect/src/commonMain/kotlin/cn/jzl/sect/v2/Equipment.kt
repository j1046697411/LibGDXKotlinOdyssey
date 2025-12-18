package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.destroy
import cn.jzl.ecs.entity
import cn.jzl.ecs.isActive
import cn.jzl.ecs.query.firstOrNull
import cn.jzl.ecs.query.forEach
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy

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

enum class Profession {
    WARRIOR,
    MAGE,
    ROGUE
}

@JvmInline
value class CharacterProfession(val profession: Profession)

@JvmInline
value class SetBelonging(val setPrefab: Entity)

@JvmInline
value class EnhancementLevel(val level: Long)
sealed class Locked

@JvmInline
value class Money(val value: Int)
data class EnhancementMaterial(
    val enhancementValue: Long
)

data class EnhancementMaterialRequirement(
    val materialPrefab: Entity,
    val quantity: Int
)

enum class SetBonusLevel {
    NONE,
    TWO_PIECE,
    FOUR_PIECE
}

data class EquipmentFilters(
    val quality: EquipmentQuality? = null,
    val type: EquipmentType? = null,
    val minLevel: Int? = null,
    val maxLevel: Int? = null
)

enum class SortBy {
    LEVEL_ASCENDING,
    LEVEL_DESCENDING,
    QUALITY_ASCENDING,
    QUALITY_DESCENDING,
    TYPE_ASCENDING,
    TYPE_DESCENDING
}

sealed class EquippedTo
sealed class BelongsToSet
data class SlotData(val slot: EquipmentSlot)
sealed class EquipmentException(message: String) : Exception(message) {
    data class LevelRequirementNotMet(
        val required: Long,
        val actual: Long
    ) : EquipmentException(
        "Level requirement not met: required $required, actual $actual"
    )

    data class ProfessionMismatch(
        val required: Profession,
        val actual: Profession?
    ) : EquipmentException(
        "Profession mismatch: required $required, actual $actual"
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

sealed class EquipmentRequirementData {
    data class Level(val level: Long) : EquipmentRequirementData()
    data class Profession(val profession: cn.jzl.sect.v2.Profession) : EquipmentRequirementData()
}

data class EquipmentRequirements(
    val requirement: EquipmentRequirement,
    val requirementData: EquipmentRequirementData? = null
)

class EquipmentPrefabContext(
    internal val entityContext: EntityCreateContext,
    private val equipmentService: EquipmentService,
    private val currentEntity: Entity
) {
    fun attribute(attributeName: String, value: Long) {
        val attributeService = equipmentService.attributeService
        val attribute = attributeService.attribute(Named(attributeName))
        with(entityContext) {
            currentEntity.addRelation(attribute, AttributeValue(value))
        }
    }

    fun attribute(attribute: Entity, value: Long) {
        with(entityContext) {
            currentEntity.addRelation(attribute, AttributeValue(value))
        }
    }

    fun attack(value: Long) = attribute("attack", value)
    fun defense(value: Long) = attribute("defense", value)
    fun health(value: Long) = attribute("health", value)
    fun levelRequirement(level: Long) {
        val requirementData = EquipmentRequirementData.Level(level)
        val requirement = requirementData.toRequirement(equipmentService)
        with(entityContext) {
            currentEntity.addComponent(EquipmentRequirements(requirement, requirementData))
        }
    }
}

class EquipmentService(world: World) : EntityRelationContext(world) {
    internal val levelingService by world.di.instance<LevelingService>()
    internal val itemService by world.di.instance<ItemService>()
    internal val effectService by world.di.instance<EffectService>()
    internal val attributeService by world.di.instance<AttributeService>()
    internal val inventoryService by world.di.instance<InventoryService>()
    val attackAttribute by lazy { attributeService.attribute(Named("attack")) }
    val defenseAttribute by lazy { attributeService.attribute(Named("defense")) }
    val healthAttribute by lazy { attributeService.attribute(Named("health")) }
    fun createEquipmentPrefab(
        name: String,
        type: EquipmentType,
        quality: EquipmentQuality,
        equipmentRequirement: EquipmentRequirement? = null,
        configure: EquipmentPrefabContext.() -> Unit = {}
    ): Entity {
        return itemService.itemPrefab(Named(name)) {
            it.addComponent(type)
            it.addComponent(quality)
            val context = EquipmentPrefabContext(this, this@EquipmentService, it)
            context.configure()
            if (equipmentRequirement != null) {
                it.addComponent(EquipmentRequirements(equipmentRequirement))
            }
        }.also { prefab ->
            createEquipmentEffectPrefab(prefab)
        }
    }

    fun requireAll(vararg requirementData: EquipmentRequirementData): EquipmentRequirement {
        return EquipmentRequirement { character, equipment ->
            requirementData.all { data ->
                val req = data.toRequirement(this@EquipmentService)
                req.run { check(character, equipment) }
            }
        }
    }

    private fun createEquipmentEffectPrefab(equipmentPrefab: Entity): Entity {
        val name = equipmentPrefab.getComponent<Named>().name
        return effectService.effectPrefab(
            name = "${name}效果",
            type = EffectType.PERMANENT
        ) {
            val attackValue = equipmentPrefab.getRelation<AttributeValue?>(attackAttribute)?.value
            if (attackValue != null && attackValue > 0) {
                it.addComponent(
                    AttributeModifier(
                        attribute = attackAttribute,
                        modifierType = ModifierType.ADD,
                        value = attackValue
                    )
                )
            }
            val defenseValue = equipmentPrefab.getRelation<AttributeValue?>(defenseAttribute)?.value
            if (defenseValue != null && defenseValue > 0) {
                it.addComponent(
                    AttributeModifier(
                        attribute = defenseAttribute,
                        modifierType = ModifierType.ADD,
                        value = defenseValue
                    )
                )
            }
            val healthValue = equipmentPrefab.getRelation<AttributeValue?>(healthAttribute)?.value
            if (healthValue != null && healthValue > 0) {
                it.addComponent(
                    AttributeModifier(
                        attribute = healthAttribute,
                        modifierType = ModifierType.ADD,
                        value = healthValue
                    )
                )
            }
        }
    }

    fun canEquip(character: Entity, equipment: Entity): Boolean {
        val requirementsComponent = equipment.getComponent<EquipmentRequirements?>()
            ?: equipment.prefab?.getComponent<EquipmentRequirements?>()
        return requirementsComponent?.requirement?.run { check(character, equipment) } ?: true
    }

    fun getEquippedItem(character: Entity, slot: EquipmentSlot): Entity? {
        return character.getRelationDown<SlotData>()
            .firstOrNull {
                component1.slot == slot
            }?.entity
    }

    fun getAllEquippedItems(character: Entity): Map<EquipmentSlot, Entity> {
        val result = mutableMapOf<EquipmentSlot, Entity>()
        character.getRelationDown<SlotData>()
            .forEach {
                result[component1.slot] = entity
            }
        return result
    }

    private fun findEffectByEquipment(character: Entity, equipment: Entity): Entity? {
        return character.getRelationDown<AppliedTo>()
            .firstOrNull {
                entity.hasRelation<FromEquipment>(equipment)
            }?.entity
    }

    fun equip(character: Entity, equipment: Entity, slot: EquipmentSlot) {
        val requirementsComponent = equipment.getComponent<EquipmentRequirements?>()
            ?: equipment.prefab?.getComponent<EquipmentRequirements?>()
        if (requirementsComponent != null) {
            val passed = requirementsComponent.requirement.run { check(character, equipment) }
            if (!passed) {
                throw IllegalStateException("Equipment requirement not met")
            }
        }
        val equipmentType = equipment.getComponent<EquipmentType?>()
            ?: equipment.prefab?.getComponent<EquipmentType?>()
            ?: throw IllegalArgumentException("Equipment missing EquipmentType component: $equipment")
        if (!isSlotCompatible(equipmentType, slot)) {
            throw EquipmentException.SlotTypeMismatch(equipmentType, slot)
        }
        val existingEquipment = getEquippedItem(character, slot)
        if (existingEquipment != null) {
            unequip(character, slot)
        }
        val equipmentPrefab = equipment.prefab
            ?: throw IllegalArgumentException("Equipment missing prefab: $equipment")
        val effectPrefab = createEquipmentEffectPrefab(equipmentPrefab)
        val effectInstance = effectService.applyEffect(
            target = character,
            effectPrefab = effectPrefab,
            source = equipment
        )
        world.entity(effectInstance) {
            it.addRelation<FromEquipment>(equipment)
        }
        world.entity(equipment) {
            it.addRelation(character, SlotData(slot))
        }
        checkAndApplySetBonus(character)
    }

    fun unequip(character: Entity, slot: EquipmentSlot) {
        val equipment = getEquippedItem(character, slot)
            ?: throw EquipmentException.NoEquipmentInSlot(character, slot)
        val effectInstance = findEffectByEquipment(character, equipment)
        if (effectInstance != null) {
            effectService.removeEffect(effectInstance)
        }
        world.entity(equipment) {
            it.removeRelation<SlotData>(character)
        }
        checkAndApplySetBonus(character)
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

    fun grantEquipmentToCharacter(character: Entity, equipmentPrefab: Entity): Entity {
        val items = inventoryService.addItem(character, equipmentPrefab, 1)
        return items.firstOrNull()
            ?: throw IllegalStateException("Failed to add equipment to inventory")
    }

    fun discardEquipment(character: Entity, equipment: Entity) {
        if (isEquipped(equipment)) {
            throw EquipmentException.EquipmentEquipped(equipment)
        }
        val equipmentPrefab = equipment.prefab
            ?: throw IllegalArgumentException("Equipment missing prefab: $equipment")
        inventoryService.removeItem(character, equipmentPrefab, 1)
    }

    fun sellEquipment(character: Entity, equipment: Entity) {
        if (isEquipped(equipment)) {
            throw EquipmentException.EquipmentEquipped(equipment)
        }
        val unitPrice = equipment.getComponent<UnitPrice?>()?.price
            ?: throw IllegalArgumentException("Equipment missing UnitPrice: $equipment")
        world.entity(character) {
            val currentMoney = it.getComponent<Money?>()?.value ?: 0
            it.addComponent(Money(currentMoney + unitPrice))
        }
        val equipmentPrefab = equipment.prefab
            ?: throw IllegalArgumentException("Equipment missing prefab: $equipment")
        inventoryService.removeItem(character, equipmentPrefab, 1)
    }

    fun getEquipmentQualityColor(quality: EquipmentQuality): String {
        return when (quality) {
            EquipmentQuality.COMMON -> "#FFFFFF"
            EquipmentQuality.RARE -> "#4169E1"
        }
    }

    fun isEquipped(equipment: Entity): Boolean {
        return equipment.getRelationsWithData<SlotData>().any()
    }

    fun getSetBonus(character: Entity, setPrefab: Entity): SetBonusLevel {
        val equippedItems = getAllEquippedItems(character).values
        val setItemCount = equippedItems.count { equipment ->
            val setBelonging = equipment.getComponent<SetBelonging?>()
            setBelonging?.setPrefab == setPrefab
        }
        return when {
            setItemCount >= 4 -> SetBonusLevel.FOUR_PIECE
            setItemCount >= 2 -> SetBonusLevel.TWO_PIECE
            else -> SetBonusLevel.NONE
        }
    }

    private fun checkAndApplySetBonus(character: Entity) {
        val equippedItems = getAllEquippedItems(character).values
        val setPrefabs = equippedItems.mapNotNull { equipment ->
            equipment.getComponent<SetBelonging?>()?.setPrefab
        }.distinct()
        val existingSetEffects = mutableMapOf<Entity, Entity>()
        var processedEffects = emptySet<Entity>()
        while (true) {
            val nextEffect = character.getRelationDown<AppliedTo>().firstOrNull {
                val effectInstance = entity
                if (effectInstance in processedEffects) {
                    return@firstOrNull false
                }
                val effectPrefab = effectInstance.prefab
                if (effectPrefab != null && effectPrefab.getComponent<Named?>()?.name?.contains("套装效果") == true) {
                    val setPrefab = effectInstance.getRelationsWithData<FromEquipment>()
                        .firstOrNull()
                        ?.relation
                        ?.target
                    if (setPrefab != null && !existingSetEffects.containsKey(setPrefab)) {
                        return@firstOrNull true
                    }
                }
                false
            }?.entity
            if (nextEffect == null) break
            processedEffects = processedEffects + nextEffect
            val effectPrefab = nextEffect.prefab
            if (effectPrefab != null && effectPrefab.getComponent<Named?>()?.name?.contains("套装效果") == true) {
                val setPrefab = nextEffect.getRelationsWithData<FromEquipment>()
                    .firstOrNull()
                    ?.relation
                    ?.target
                if (setPrefab != null) {
                    existingSetEffects[setPrefab] = nextEffect
                }
            }
        }
        val allSetPrefabs = (setPrefabs + existingSetEffects.keys).distinct()
        allSetPrefabs.forEach { setPrefab ->
            val currentBonus = getSetBonus(character, setPrefab)
            val existingSetEffect = existingSetEffects[setPrefab]
            if (currentBonus != SetBonusLevel.NONE && existingSetEffect == null) {
                val setEffectPrefab = createSetEffectPrefab(setPrefab, currentBonus)
                if (setEffectPrefab != null) {
                    val setEffectInstance = effectService.applyEffect(
                        target = character,
                        effectPrefab = setEffectPrefab,
                        source = setPrefab
                    )
                    world.entity(setEffectInstance) {
                        it.addRelation<FromEquipment>(setPrefab)
                    }
                }
            } else if (currentBonus == SetBonusLevel.NONE && existingSetEffect != null) {
                effectService.removeEffect(existingSetEffect)
            } else if (currentBonus != SetBonusLevel.NONE && existingSetEffect != null) {
                effectService.removeEffect(existingSetEffect)
                val newSetEffectPrefab = createSetEffectPrefab(setPrefab, currentBonus)
                if (newSetEffectPrefab != null) {
                    val newSetEffectInstance = effectService.applyEffect(
                        target = character,
                        effectPrefab = newSetEffectPrefab,
                        source = setPrefab
                    )
                    world.entity(newSetEffectInstance) {
                        it.addRelation<FromEquipment>(setPrefab)
                    }
                }
            }
        }
    }

    private fun checkAndRemoveSetBonus(character: Entity) {
        val equippedItems = getAllEquippedItems(character).values
        val setPrefabs = equippedItems.mapNotNull { equipment ->
            equipment.getComponent<SetBelonging?>()?.setPrefab
        }.distinct()
        setPrefabs.forEach { setPrefab ->
            val currentBonus = getSetBonus(character, setPrefab)
            if (currentBonus == SetBonusLevel.NONE) {
                val setEffect = character.getRelationDown<AppliedTo>()
                    .firstOrNull {
                        val effectPrefab = entity.prefab
                        effectPrefab != null && effectPrefab.getComponent<Named?>()?.name?.contains("套装效果") == true &&
                                entity.hasRelation<FromEquipment>(setPrefab)
                    }?.entity
                if (setEffect != null) {
                    effectService.removeEffect(setEffect)
                }
            }
        }
    }

    private fun createSetEffectPrefab(setPrefab: Entity, bonusLevel: SetBonusLevel): Entity? {
        if (bonusLevel == SetBonusLevel.NONE) {
            return null
        }
        val setName = setPrefab.getComponent<Named?>()?.name ?: "套装"
        val effectName = when (bonusLevel) {
            SetBonusLevel.TWO_PIECE -> "${setName}2件套效果"
            SetBonusLevel.FOUR_PIECE -> "${setName}4件套效果"
            SetBonusLevel.NONE -> return null
        }
        return effectService.effectPrefab(
            name = effectName,
            type = EffectType.PERMANENT
        ) {
            when (bonusLevel) {
                SetBonusLevel.TWO_PIECE -> {
                    it.addComponent(
                        AttributeModifier(
                            attribute = attackAttribute,
                            modifierType = ModifierType.ADD,
                            value = 10
                        )
                    )
                }

                SetBonusLevel.FOUR_PIECE -> {
                    it.addComponent(
                        AttributeModifier(
                            attribute = attackAttribute,
                            modifierType = ModifierType.ADD,
                            value = 20
                        )
                    )
                    it.addComponent(
                        AttributeModifier(
                            attribute = defenseAttribute,
                            modifierType = ModifierType.ADD,
                            value = 10
                        )
                    )
                }

                SetBonusLevel.NONE -> {
                }
            }
        }
    }

    private fun calculateEnhancementSuccessRate(currentLevel: Long): Double {
        val baseRate = 1.0 - (currentLevel * 0.1)
        return maxOf(0.1, baseRate)
    }

    private fun calculateEnhancementValue(materials: List<EnhancementMaterialRequirement>, currentLevel: Long): Long {
        var totalValue = 0L
        materials.forEach { requirement ->
            val material = requirement.materialPrefab.getComponent<EnhancementMaterial?>()
                ?: throw IllegalArgumentException("Material prefab missing EnhancementMaterial component: ${requirement.materialPrefab}")
            totalValue += material.enhancementValue * requirement.quantity
        }
        val levelMultiplier = maxOf(0.5, 1.0 - (currentLevel * 0.05))
        return (totalValue * levelMultiplier).toLong()
    }

    private fun hasEnoughMaterials(character: Entity, materials: List<EnhancementMaterialRequirement>): Boolean {
        materials.forEach { requirement ->
            val actualCount = inventoryService.getItemCount(character, requirement.materialPrefab)
            if (actualCount < requirement.quantity) {
                return false
            }
        }
        return true
    }

    private fun consumeMaterials(character: Entity, materials: List<EnhancementMaterialRequirement>) {
        materials.forEach { requirement ->
            inventoryService.removeItem(character, requirement.materialPrefab, requirement.quantity)
        }
    }

    fun enhance(character: Entity, equipment: Entity, materials: List<EnhancementMaterialRequirement>): Long {
        if (equipment.hasTag<Locked>()) {
            throw EquipmentException.EquipmentLocked(equipment)
        }
        if (!hasEnoughMaterials(character, materials)) {
            val required = materials.associate { it.materialPrefab to it.quantity }
            val actual = materials.associate {
                it.materialPrefab to inventoryService.getItemCount(character, it.materialPrefab)
            }
            throw EquipmentException.InsufficientMaterials(required, actual)
        }
        val currentLevel = equipment.getComponent<EnhancementLevel?>()?.level ?: 0
        consumeMaterials(character, materials)
        val successRate = calculateEnhancementSuccessRate(currentLevel)
        val success = kotlin.random.Random.nextDouble() < successRate
        if (success) {
            val newLevel = currentLevel + 1
            val enhancementValue = calculateEnhancementValue(materials, currentLevel)
            world.entity(equipment) {
                it.addComponent(EnhancementLevel(newLevel))
            }
            val currentAttackValue = equipment.getRelation<AttributeValue?>(attackAttribute)?.value ?: 0
            val currentDefenseValue = equipment.getRelation<AttributeValue?>(defenseAttribute)?.value ?: 0
            val currentHealthValue = equipment.getRelation<AttributeValue?>(healthAttribute)?.value ?: 0
            val enhancedAttackValue = currentAttackValue + enhancementValue
            val enhancedDefenseValue = currentDefenseValue + enhancementValue
            val enhancedHealthValue = currentHealthValue + enhancementValue
            world.entity(equipment) {
                if (enhancedAttackValue > 0) {
                    it.addRelation(attackAttribute, AttributeValue(enhancedAttackValue))
                }
                if (enhancedDefenseValue > 0) {
                    it.addRelation(defenseAttribute, AttributeValue(enhancedDefenseValue))
                }
                if (enhancedHealthValue > 0) {
                    it.addRelation(healthAttribute, AttributeValue(enhancedHealthValue))
                }
            }
            val equippedCharacter = equipment.getRelationsWithData<SlotData>().firstOrNull()?.relation?.target
            if (equippedCharacter != null) {
                val effectInstance = findEffectByEquipment(equippedCharacter, equipment)
                if (effectInstance != null) {
                    effectService.removeEffect(effectInstance)
                    val equipmentName = equipment.getComponent<Named>()?.name ?: "装备"
                    val effectPrefab = effectService.effectPrefab(
                        name = "${equipmentName}效果",
                        type = EffectType.PERMANENT
                    ) {
                        if (enhancedAttackValue > 0) {
                            it.addComponent(
                                AttributeModifier(
                                    attribute = attackAttribute,
                                    modifierType = ModifierType.ADD,
                                    value = enhancedAttackValue
                                )
                            )
                        }
                        if (enhancedDefenseValue > 0) {
                            it.addComponent(
                                AttributeModifier(
                                    attribute = defenseAttribute,
                                    modifierType = ModifierType.ADD,
                                    value = enhancedDefenseValue
                                )
                            )
                        }
                        if (enhancedHealthValue > 0) {
                            it.addComponent(
                                AttributeModifier(
                                    attribute = healthAttribute,
                                    modifierType = ModifierType.ADD,
                                    value = enhancedHealthValue
                                )
                            )
                        }
                    }
                    val newEffectInstance = effectService.applyEffect(
                        target = equippedCharacter,
                        effectPrefab = effectPrefab,
                        source = equipment
                    )
                    world.entity(newEffectInstance) {
                        it.addRelation<FromEquipment>(equipment)
                    }
                }
            }
            return newLevel
        } else {
            throw EquipmentException.EnhancementFailed(equipment, currentLevel)
        }
    }

    fun lock(equipment: Entity) {
        world.entity(equipment) {
            it.addTag<Locked>()
        }
    }

    fun unlock(equipment: Entity) {
        world.entity(equipment) {
            it.removeTag<Locked>()
        }
    }

    fun isLocked(equipment: Entity): Boolean {
        return equipment.hasTag<Locked>()
    }

    private fun getOrCreateDefaultMaterialPrefab(): Entity {
        val existingPrefab = itemService.itemPrefabs()
            .firstOrNull { prefab ->
                with(itemService) {
                    val name = prefab.getComponent<Named?>()?.name
                    val hasMaterial = prefab.hasComponent<EnhancementMaterial>()
                    name == "强化石" && hasMaterial
                }
            }
        if (existingPrefab != null) {
            return existingPrefab
        }
        return try {
            itemService.itemPrefab(Named("强化石")) {
                it.addComponent(EnhancementMaterial(enhancementValue = 5))
                it.addTag<Stackable>()
            }
        } catch (e: IllegalArgumentException) {
            itemService.itemPrefabs()
                .firstOrNull { prefab ->
                    with(itemService) {
                        prefab.getComponent<Named?>()?.name == "强化石"
                    }
                } ?: throw e
        }
    }

    private fun calculateDecomposeMaterials(equipment: Entity): Map<Entity, Int> {
        val quality = equipment.getComponent<EquipmentQuality?>()
            ?: throw IllegalStateException("Equipment missing EquipmentQuality component: $equipment")
        val enhancementLevel = equipment.getComponent<EnhancementLevel?>()?.level ?: 0
        val materialPrefab = getOrCreateDefaultMaterialPrefab()
        val baseMaterialCount = when (quality) {
            EquipmentQuality.COMMON -> 1
            EquipmentQuality.RARE -> 3
        }
        val totalMaterialCount = baseMaterialCount + enhancementLevel.toInt()
        return mapOf(materialPrefab to totalMaterialCount)
    }

    fun decompose(character: Entity, equipment: Entity): Map<Entity, Int> {
        if (isLocked(equipment)) {
            throw EquipmentException.EquipmentLocked(equipment)
        }
        if (isEquipped(equipment)) {
            throw EquipmentException.EquipmentEquipped(equipment)
        }
        val materials = calculateDecomposeMaterials(equipment)
        materials.forEach { (materialPrefab, quantity) ->
            inventoryService.addItem(character, materialPrefab, quantity)
        }
        try {
            val owner = equipment.getRelationUp<OwnedBy>()
            if (owner != null && world.isActive(equipment)) {
                world.entity(equipment) {
                    it.removeRelation<OwnedBy>(owner)
                }
            }
        } catch (e: Exception) {
        }
        world.destroy(equipment)
        return materials
    }

    fun filterEquipment(equipmentList: List<Entity>, filters: EquipmentFilters): List<Entity> {
        return equipmentList.filter { equipment ->
            if (filters.quality != null) {
                val quality = equipment.getComponent<EquipmentQuality?>()
                if (quality != filters.quality) {
                    return@filter false
                }
            }
            if (filters.type != null) {
                val type = equipment.getComponent<EquipmentType?>()
                if (type != filters.type) {
                    return@filter false
                }
            }
            if (filters.minLevel != null || filters.maxLevel != null) {
                val levelRequirement = extractLevelRequirement(equipment)
                if (filters.minLevel != null && levelRequirement < filters.minLevel) {
                    return@filter false
                }
                if (filters.maxLevel != null && levelRequirement > filters.maxLevel) {
                    return@filter false
                }
            }
            true
        }
    }

    fun extractLevelRequirement(equipment: Entity): Int {
        val requirements = equipment.getComponent<EquipmentRequirements?>()
            ?: equipment.prefab?.getComponent<EquipmentRequirements?>()
        val levelRequirement = requirements?.requirementData?.let { data ->
            when (data) {
                is EquipmentRequirementData.Level -> data.level.toInt()
                else -> 0
            }
        } ?: 0
        return levelRequirement
    }

    fun sortEquipment(equipmentList: List<Entity>, sortBy: SortBy): List<Entity> {
        return when (sortBy) {
            SortBy.LEVEL_ASCENDING -> {
                equipmentList.sortedBy { equipment ->
                    extractLevelRequirement(equipment)
                }
            }

            SortBy.LEVEL_DESCENDING -> {
                equipmentList.sortedByDescending { equipment ->
                    extractLevelRequirement(equipment)
                }
            }

            SortBy.QUALITY_ASCENDING -> {
                equipmentList.sortedBy { equipment ->
                    val quality = equipment.getComponent<EquipmentQuality?>() ?: EquipmentQuality.COMMON
                    quality.ordinal
                }
            }

            SortBy.QUALITY_DESCENDING -> {
                equipmentList.sortedByDescending { equipment ->
                    val quality = equipment.getComponent<EquipmentQuality?>() ?: EquipmentQuality.COMMON
                    quality.ordinal
                }
            }

            SortBy.TYPE_ASCENDING -> {
                equipmentList.sortedBy { equipment ->
                    val type = equipment.getComponent<EquipmentType?>() ?: EquipmentType.WEAPON
                    type.ordinal
                }
            }

            SortBy.TYPE_DESCENDING -> {
                equipmentList.sortedByDescending { equipment ->
                    val type = equipment.getComponent<EquipmentType?>() ?: EquipmentType.WEAPON
                    type.ordinal
                }
            }
        }
    }

    fun autoEquip(character: Entity): Map<EquipmentSlot, Entity> {
        val equippedItems = mutableMapOf<EquipmentSlot, Entity>()
        val equipmentList = mutableListOf<Entity>()
        inventoryService.getAllItems(character).forEach {
            val itemEntity = entity
            val equipmentType = itemEntity.getComponent<EquipmentType?>()
            if (equipmentType != null) {
                equipmentList.add(itemEntity)
            }
        }
        val eligibleEquipment = equipmentList.filter { equipment ->
            canEquip(character, equipment)
        }
        val sortedEquipment = eligibleEquipment.sortedWith(compareByDescending<Entity> { equipment ->
            val quality = equipment.getComponent<EquipmentQuality?>() ?: EquipmentQuality.COMMON
            quality.ordinal
        }.thenByDescending { equipment ->
            extractLevelRequirement(equipment)
        })
        val usedSlots = mutableSetOf<EquipmentSlot>()
        for (equipment in sortedEquipment) {
            val equipmentType = equipment.getComponent<EquipmentType?>() ?: continue
            val slot = when (equipmentType) {
                EquipmentType.WEAPON -> EquipmentSlot.WEAPON
                EquipmentType.ARMOR -> {
                    when {
                        !usedSlots.contains(EquipmentSlot.ARMOR) -> EquipmentSlot.ARMOR
                        !usedSlots.contains(EquipmentSlot.HELMET) -> EquipmentSlot.HELMET
                        !usedSlots.contains(EquipmentSlot.LEGS) -> EquipmentSlot.LEGS
                        !usedSlots.contains(EquipmentSlot.BOOTS) -> EquipmentSlot.BOOTS
                        !usedSlots.contains(EquipmentSlot.GLOVES) -> EquipmentSlot.GLOVES
                        else -> EquipmentSlot.ARMOR
                    }
                }

                EquipmentType.ACCESSORY -> {
                    when {
                        !usedSlots.contains(EquipmentSlot.ACCESSORY1) -> EquipmentSlot.ACCESSORY1
                        !usedSlots.contains(EquipmentSlot.ACCESSORY2) -> EquipmentSlot.ACCESSORY2
                        else -> EquipmentSlot.ACCESSORY1
                    }
                }
            }
            if (usedSlots.contains(slot)) {
                continue
            }
            try {
                equip(character, equipment, slot)
                equippedItems[slot] = equipment
                usedSlots.add(slot)
            } catch (e: EquipmentException) {
                continue
            } catch (e: IllegalStateException) {
                continue
            } catch (e: IllegalArgumentException) {
                continue
            }
        }
        return equippedItems
    }
}

fun EquipmentRequirementData.toRequirement(service: EquipmentService): EquipmentRequirement {
    return when (this) {
        is EquipmentRequirementData.Level -> EquipmentRequirement { character, equipment ->
            service.levelingService.getLevel(character) >= level
        }

        is EquipmentRequirementData.Profession -> EquipmentRequirement { character, equipment ->
            val characterProfession = character.getComponent<CharacterProfession>()?.profession
            characterProfession == profession
        }
    }
}

val equipmentAddon = createAddon("equipment") {
    install(itemAddon)
    install(effectAddon)
    install(attributeAddon)
    install(inventoryAddon)
    install(levelingAddon)
    install(characterAddon)
    injects {
        this bind singleton { new(::EquipmentService) }
    }
    components {
        world.componentId<EquipmentType>()
        world.componentId<EquipmentQuality>()
        world.componentId<CharacterProfession>()
        world.componentId<SetBelonging>()
        world.componentId<EnhancementLevel>()
        world.componentId<Locked>() { it.tag() }
        world.componentId<EquipmentRequirements>()
        world.componentId<EquippedTo>()
        world.componentId<SlotData>()
        world.componentId<BelongsToSet>()
        world.componentId<Money>()
    }
}