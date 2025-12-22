package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HealthServiceTest {

    @Test
    fun initializeHealth_setsCurrentAndMaxHealth() {
        val world = world {
            install(attributeAddon)
            install(healthAddon)
        }

        val healthService by world.di.instance<HealthService>()

        val entity = world.entity { }
        world.entity(entity) {
            healthService.initializeHealth(this, it, maxHealth = 120)
        }

        assertEquals(120, healthService.getMaxHealth(entity))
        assertEquals(120, healthService.getCurrentHealth(entity))
    }

    @Test
    fun damage_neverDropsBelowZero() {
        val world = world {
            install(attributeAddon)
            install(healthAddon)
        }

        val healthService by world.di.instance<HealthService>()

        val entity = world.entity { }
        world.entity(entity) {
            healthService.initializeHealth(this, it, maxHealth = 50)
        }

        healthService.damage(entity, amount = 999)

        assertEquals(0, healthService.getCurrentHealth(entity))
    }

    @Test
    fun useHealingItem_returnsFalse_whenNoInventoryItem() {
        val world = world {
            install(itemAddon)
            install(inventoryAddon)
            install(attributeAddon)
            install(healthAddon)
        }

        val healthService by world.di.instance<HealthService>()
        val itemService by world.di.instance<ItemService>()

        val character = world.entity { }
        world.entity(character) {
            healthService.initializeHealth(this, it, maxHealth = 10)
        }

        val potionPrefab = itemService.itemPrefab(cn.jzl.sect.ecs.core.Named("Potion")) {
            it.addTag<Usable>()
            it.addComponent(HealingAmount(5))
        }

        assertFalse(healthService.useHealingItem(character, potionPrefab))
    }

    @Test
    fun useHealingItem_healsAndConsumesItem_whenValid() {
        val world = world {
            install(itemAddon)
            install(inventoryAddon)
            install(attributeAddon)
            install(healthAddon)
        }

        val healthService by world.di.instance<HealthService>()
        val inventoryService by world.di.instance<InventoryService>()
        val itemService by world.di.instance<ItemService>()

        val character = world.entity { }
        world.entity(character) {
            healthService.initializeHealth(this, it, maxHealth = 100)
        }

        // Bring health down so healing has effect
        healthService.damage(character, 40)
        assertEquals(60, healthService.getCurrentHealth(character))

        val potionPrefab = itemService.itemPrefab(cn.jzl.sect.ecs.core.Named("Potion")) {
            it.addTag<Usable>()
            it.addComponent(HealingAmount(15))
        }

        // Put 1 healing item into inventory
        inventoryService.addItem(character, potionPrefab, 1).toList()

        assertTrue(healthService.useHealingItem(character, potionPrefab))
        assertEquals(75, healthService.getCurrentHealth(character))
        assertEquals(0, inventoryService.getItemCount(character, potionPrefab))
    }
}

