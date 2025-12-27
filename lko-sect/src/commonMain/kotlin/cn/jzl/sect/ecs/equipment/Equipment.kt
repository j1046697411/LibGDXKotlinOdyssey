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

/**
 * 装备系统包，包含装备组件、服务和addon配置
 * 
 * 主要功能：
 * 1. 定义装备的基本属性和类型
 * 2. 提供装备创建、装备和卸下功能
 * 3. 实现装备属性的提供机制
 * 4. 支持装备需求验证
 */

/**
 * 装备标记组件
 * 用于标识实体为装备
 */
sealed class Equipment

/**
 * 装备类型枚举
 * 定义不同类型的装备
 */
enum class EquipmentType {
    /** 武器类型 */
    WEAPON,
    /** 防具类型 */
    ARMOR,
    /** 饰品类型 */
    ACCESSORY
}

/**
 * 装备品质枚举
 * 定义不同品质的装备
 */
enum class EquipmentQuality {
    /** 普通品质 */
    COMMON,
    /** 稀有品质 */
    RARE
}

/**
 * 装备槽位枚举
 * 定义不同的装备槽位
 */
enum class EquipmentSlot {
    /** 武器槽 */
    WEAPON,
    /** 头盔槽 */
    HELMET,
    /** 胸甲槽 */
    ARMOR,
    /** 腿部槽 */
    LEGS,
    /** 靴子槽 */
    BOOTS,
    /** 手套槽 */
    GLOVES,
    /** 饰品1槽 */
    ACCESSORY1,
    /** 饰品2槽 */
    ACCESSORY2
}

/**
 * 装备异常
 * 当装备操作失败时抛出
 * 
 * @param message 异常信息
 */
sealed class EquipmentException(message: String) : Exception(message) {
    /**
     * 等级要求未满足
     * 
     * @param required 所需等级
     * @param actual 实际等级
     */
    data class LevelRequirementNotMet(
        val required: Long,
        val actual: Long
    ) : EquipmentException(
        "Level requirement not met: required $required, actual $actual"
    )

    /**
     * 槽位类型不匹配
     * 
     * @param equipmentType 装备类型
     * @param slot 装备槽位
     */
    data class SlotTypeMismatch(
        val equipmentType: EquipmentType,
        val slot: EquipmentSlot
    ) : EquipmentException(
        "Slot type mismatch: equipment type $equipmentType cannot be equipped to slot $slot"
    )

    /**
     * 装备不在背包中
     * 
     * @param equipment 装备实体
     * @param character 角色实体
     */
    data class EquipmentNotInInventory(
        val equipment: Entity,
        val character: Entity
    ) : EquipmentException(
        "Equipment $equipment is not in character $character's inventory"
    )

    /**
     * 槽位中没有装备
     * 
     * @param character 角色实体
     * @param slot 装备槽位
     */
    data class NoEquipmentInSlot(
        val character: Entity,
        val slot: EquipmentSlot
    ) : EquipmentException(
        "No equipment in slot $slot for character $character"
    )

    /**
     * 装备已锁定
     * 
     * @param equipment 装备实体
     */
    data class EquipmentLocked(
        val equipment: Entity
    ) : EquipmentException(
        "Equipment $equipment is locked"
    )

    /**
     * 装备已装备
     * 
     * @param equipment 装备实体
     */
    data class EquipmentEquipped(
        val equipment: Entity
    ) : EquipmentException(
        "Equipment $equipment is already equipped"
    )

    /**
     * 材料不足
     * 
     * @param required 所需材料
     * @param actual 实际材料
     */
    data class InsufficientMaterials(
        val required: Map<Entity, Int>,
        val actual: Map<Entity, Int>
    ) : EquipmentException(
        "Insufficient materials for enhancement"
    )

    /**
     * 强化失败
     * 
     * @param equipment 装备实体
     * @param level 强化等级
     */
    data class EnhancementFailed(
        val equipment: Entity,
        val level: Long
    ) : EquipmentException(
        "Enhancement failed for equipment $equipment at level $level"
    )
}

/**
 * 装备需求验证接口
 * 用于验证角色是否满足装备需求
 */
fun interface EquipmentRequirement {
    /**
     * 检查角色是否满足装备需求
     * 
     * @param character 角色实体
     * @param equipment 装备实体
     * @return 是否满足需求
     */
    fun EntityRelationContext.check(character: Entity, equipment: Entity): Boolean
}

/**
 * 装备预制体配置上下文
 * 用于配置装备预制体的属性
 */
fun interface EquipmentPrefabContext {
    /**
     * 添加装备属性
     * 
     * @param attribute 属性实体
     * @param value 属性值
     */
    fun attribute(attribute: Entity, value: AttributeValue)
}

/**
 * 装备服务
 * 管理装备系统的核心功能
 * 
 * @param world ECS世界实例
 */
class EquipmentService(world: World) : EntityRelationContext(world) {

    internal val itemService by world.di.instance<ItemService>()
    internal val attributeService by world.di.instance<AttributeService>()
    internal val inventoryService by world.di.instance<InventoryService>()
    internal val attributes by world.di.instance<SectAttributes>()

    /**
     * 创建装备预制体
     * 
     * @param name 装备名称
     * @param type 装备类型
     * @param equipmentRequirement 装备需求，可为空
     * @param configure 装备配置块，默认空
     * @return 创建的装备预制体
     */
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

    /**
     * 检查角色是否可以装备该装备
     * 
     * @param character 角色实体
     * @param equipment 装备实体
     * @return 是否可以装备
     */
    fun canEquip(character: Entity, equipment: Entity): Boolean {
        if (equipment.getRelationUp<OwnedBy>() != character) return false
        if (!equipment.hasComponent<Equipment>()) return false
        val equipmentRequirement = equipment.getComponent<EquipmentRequirement?>()
        return equipmentRequirement?.run { check(character, equipment) } ?: true
    }

    /**
     * 获取角色特定槽位的装备
     * 
     * @param character 角色实体
     * @param slot 装备槽位
     * @return 装备实体，可为空
     */
    fun getEquippedItem(character: Entity, slot: EquipmentSlot): Entity? {
        return character.getRelationDown<EquipmentSlot>().firstOrNull { component1 == slot }?.entity
    }

    /**
     * 获取角色所有已装备的装备
     * 
     * @param character 角色实体
     * @return 装备槽位到装备实体的映射
     */
    fun getAllEquippedItems(character: Entity): Map<EquipmentSlot, Entity> {
        val result = mutableMapOf<EquipmentSlot, Entity>()
        character.getRelationDown<EquipmentSlot>().forEach { result[component1] = entity }
        return result
    }

    /**
     * 装备物品到指定槽位
     * 
     * @param character 角色实体
     * @param equipment 装备实体
     * @param slot 装备槽位
     */
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

    /**
     * 从指定槽位卸下装备
     * 
     * @param character 角色实体
     * @param slot 装备槽位
     */
    fun unequip(character: Entity, slot: EquipmentSlot) {
        val equipment = getEquippedItem(character, slot) ?: throw EquipmentException.NoEquipmentInSlot(character, slot)
        world.entity(equipment) {
            it.removeRelation<EquipmentSlot>(character)
        }
    }

    /**
     * 检查装备类型与槽位是否兼容
     * 
     * @param equipmentType 装备类型
     * @param slot 装备槽位
     * @return 是否兼容
     */
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

    /**
     * 检查装备是否已装备
     * 
     * @param character 角色实体
     * @param equipment 装备实体
     * @return 是否已装备
     */
    fun isEquipped(character: Entity, equipment: Entity): Boolean {
        return equipment.hasRelation<EquipmentSlot>(character)
    }
}

/**
 * 装备属性提供者
 * 用于提供装备的属性值
 * 
 * @param world ECS世界实例
 */
class EquipmentAttributeProvider(world: World) : AttributeProvider, EntityRelationContext(world) {

    private val equipmentService by world.di.instance<EquipmentService>()

    /**
     * 获取装备提供的属性值
     * 
     * @param attributeService 属性服务
     * @param entity 角色实体
     * @param attribute 属性实体
     * @return 属性值
     */
    override fun getAttributeValue(attributeService: AttributeService, entity: Entity, attribute: Entity): AttributeValue {
        return equipmentService.getAllEquippedItems(entity).values.fold(AttributeValue.zero) { acc, equipment ->
            val attributeValue = attributeService.getTotalAttributeValue(equipment, attribute)
            acc + attributeValue
        }
    }
}

/**
 * 装备addon
 * 注册装备相关组件、服务和属性提供者
 */
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