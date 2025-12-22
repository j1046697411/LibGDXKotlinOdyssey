package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.Equipment
import cn.jzl.sect.ecs.EquipmentException
import cn.jzl.sect.ecs.EquipmentService
import cn.jzl.sect.ecs.EquipmentSlot
import cn.jzl.sect.ecs.EquipmentType
import cn.jzl.sect.ecs.ItemService
import cn.jzl.sect.ecs.Named
import cn.jzl.sect.ecs.OwnedBy
import cn.jzl.sect.ecs.attributeAddon
import cn.jzl.sect.ecs.characterAddon
import cn.jzl.sect.ecs.equipmentAddon
import cn.jzl.sect.ecs.healthAddon
import cn.jzl.sect.ecs.inventoryAddon
import cn.jzl.sect.ecs.itemAddon
import cn.jzl.sect.ecs.levelingAddon
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EquipmentServiceTest {

    @Test
    fun equip_throwsWhenEquipmentNotOwnedByCharacter() {
        val world = world {
            install(itemAddon)
            install(attributeAddon)
            install(inventoryAddon)
            install(levelingAddon)
            install(healthAddon)
            install(characterAddon)
            install(equipmentAddon)
        }

        val equipmentService by world.di.instance<EquipmentService>()
        val itemService by world.di.instance<ItemService>()

        val character = world.entity { }

        val weaponPrefab = itemService.itemPrefab(Named("Sword")) {
            it.addTag<Equipment>()
            it.addComponent(EquipmentType.WEAPON)
        }

        val weapon = itemService.item(weaponPrefab) {
            // not owned by character
        }

        assertFailsWith<EquipmentException.EquipmentNotInInventory> {
            equipmentService.equip(character, weapon, EquipmentSlot.WEAPON)
        }
    }

    @Test
    fun equip_addsRelationAndUnequipRemovesIt() {
        val world = world {
            install(itemAddon)
            install(attributeAddon)
            install(inventoryAddon)
            install(levelingAddon)
            install(healthAddon)
            install(characterAddon)
            install(equipmentAddon)
        }

        val equipmentService by world.di.instance<EquipmentService>()
        val itemService by world.di.instance<ItemService>()

        val character = world.entity { }

        val weaponPrefab = itemService.itemPrefab(Named("Sword")) {
            it.addTag<Equipment>()
            it.addComponent(EquipmentType.WEAPON)
        }

        val weapon = itemService.item(weaponPrefab) {
            it.addRelation<OwnedBy>(character)
        }

        assertNull(equipmentService.getEquippedItem(character, EquipmentSlot.WEAPON))

        equipmentService.equip(character, weapon, EquipmentSlot.WEAPON)

        assertTrue(equipmentService.isEquipped(character, weapon))
        assertNotNull(equipmentService.getEquippedItem(character, EquipmentSlot.WEAPON))

        equipmentService.unequip(character, EquipmentSlot.WEAPON)

        assertNull(equipmentService.getEquippedItem(character, EquipmentSlot.WEAPON))
    }

    @Test
    fun equip_throwsWhenSlotNotCompatible() {
        val world = world {
            install(itemAddon)
            install(attributeAddon)
            install(inventoryAddon)
            install(levelingAddon)
            install(healthAddon)
            install(characterAddon)
            install(equipmentAddon)
        }

        val equipmentService by world.di.instance<EquipmentService>()
        val itemService by world.di.instance<ItemService>()

        val character = world.entity { }

        val weaponPrefab = itemService.itemPrefab(Named("Sword")) {
            it.addTag<Equipment>()
            it.addComponent(EquipmentType.WEAPON)
        }

        val weapon = itemService.item(weaponPrefab) {
            it.addRelation<OwnedBy>(character)
        }

        assertFailsWith<EquipmentException.SlotTypeMismatch> {
            equipmentService.equip(character, weapon, EquipmentSlot.HELMET)
        }
    }

    @Test
    fun equip_replacesExistingEquipmentInSameSlot() {
        val world = world {
            install(itemAddon)
            install(attributeAddon)
            install(inventoryAddon)
            install(levelingAddon)
            install(healthAddon)
            install(characterAddon)
            install(equipmentAddon)
        }

        val equipmentService by world.di.instance<EquipmentService>()
        val itemService by world.di.instance<ItemService>()

        val character = world.entity { }

        val weaponPrefab1 = itemService.itemPrefab(Named("Sword1")) {
            it.addTag<Equipment>()
            it.addComponent(EquipmentType.WEAPON)
        }
        val weaponPrefab2 = itemService.itemPrefab(Named("Sword2")) {
            it.addTag<Equipment>()
            it.addComponent(EquipmentType.WEAPON)
        }

        val weapon1 = itemService.item(weaponPrefab1) { it.addRelation<OwnedBy>(character) }
        val weapon2 = itemService.item(weaponPrefab2) { it.addRelation<OwnedBy>(character) }

        equipmentService.equip(character, weapon1, EquipmentSlot.WEAPON)
        assertNotNull(equipmentService.getEquippedItem(character, EquipmentSlot.WEAPON))
        assertTrue(equipmentService.isEquipped(character, weapon1))

        // Equipping a second weapon should unequip the first.
        equipmentService.equip(character, weapon2, EquipmentSlot.WEAPON)
        val equipped = equipmentService.getEquippedItem(character, EquipmentSlot.WEAPON)
        assertNotNull(equipped)
        assertTrue(equipmentService.isEquipped(character, weapon2))
        assertTrue(!equipmentService.isEquipped(character, weapon1))
    }

    @Test
    fun unequip_throwsWhenSlotEmpty() {
        val world = world {
            install(itemAddon)
            install(attributeAddon)
            install(inventoryAddon)
            install(levelingAddon)
            install(healthAddon)
            install(characterAddon)
            install(equipmentAddon)
        }

        val equipmentService by world.di.instance<EquipmentService>()
        val character = world.entity { }

        assertNull(equipmentService.getEquippedItem(character, EquipmentSlot.WEAPON))

        assertFailsWith<EquipmentException.NoEquipmentInSlot> {
            equipmentService.unequip(character, EquipmentSlot.WEAPON)
        }
    }
}
