package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.ecs.entity
import cn.jzl.ecs.system.update
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.Amount
import cn.jzl.sect.ecs.InventoryService
import cn.jzl.sect.ecs.ItemService
import cn.jzl.sect.ecs.Named
import cn.jzl.sect.ecs.OwnedBy
import cn.jzl.sect.ecs.Stackable
import cn.jzl.sect.ecs.inventoryAddon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds

class InventoryServiceTest {

    @Test
    fun addAndRemoveStackableItem_updatesCountsAndKeepsEntity() {
        val world = world {
            install(inventoryAddon)
        }

        val inventoryService by world.di.instance<InventoryService>()
        val itemService by world.di.instance<ItemService>()

        val owner = world.entity { }

        val potionPrefab = itemService.itemPrefab(Named("Potion")) {
            it.addTag<Stackable>()
        }

        val entities = inventoryService.addItem(owner, potionPrefab, 3).toList()
        assertEquals(1, entities.size)
        assertEquals(3, inventoryService.getItemCount(owner, potionPrefab))

        inventoryService.removeItem(owner, potionPrefab, 2)
        assertEquals(1, inventoryService.getItemCount(owner, potionPrefab))

        // Removing remainder should delete the entity.
        inventoryService.removeItem(owner, potionPrefab, 1)
        world.update(16.milliseconds)
        assertEquals(0, inventoryService.getItemCount(owner, potionPrefab))
    }

    @Test
    fun addNonStackableCreatesMultipleEntitiesAndConsumeRemoves() {
        val world = world {
            install(inventoryAddon)
        }

        val inventoryService by world.di.instance<InventoryService>()
        val itemService by world.di.instance<ItemService>()

        val owner = world.entity { }

        val applePrefab = itemService.itemPrefab(Named("Apple")) {
            // not stackable
        }

        val entities = inventoryService.addItem(owner, applePrefab, 2).toList()
        assertEquals(2, entities.size)
        assertEquals(2, inventoryService.getItemCount(owner, applePrefab))

        inventoryService.consumeItems(owner, mapOf(applePrefab to 2))
        world.update(16.milliseconds)
        assertEquals(0, inventoryService.getItemCount(owner, applePrefab))
    }

    @Test
    fun transferItem_splitsStackWhenPartialTransfer() {
        val world = world {
            install(inventoryAddon)
        }

        val inventoryService by world.di.instance<InventoryService>()
        val itemService by world.di.instance<ItemService>()

        val provider = world.entity { }
        val receiver = world.entity { }

        val potionPrefab = itemService.itemPrefab(Named("Potion")) {
            it.addTag<Stackable>()
        }

        val potion = itemService.item(potionPrefab) {
            it.addRelation<OwnedBy>(provider)
            it.addComponent(Amount(5))
        }

        inventoryService.transferItem(provider, receiver, potion, count = 2)

        assertEquals(3, inventoryService.getItemCount(provider, potionPrefab))
        assertEquals(2, inventoryService.getItemCount(receiver, potionPrefab))
    }

    @Test
    fun consumeItems_throwsWhenNotEnough() {
        val world = world {
            install(inventoryAddon)
        }

        val inventoryService by world.di.instance<InventoryService>()
        val itemService by world.di.instance<ItemService>()

        val owner = world.entity { }

        val applePrefab = itemService.itemPrefab(Named("Apple")) { }

        inventoryService.addItem(owner, applePrefab, 1).toList()
        assertEquals(1, inventoryService.getItemCount(owner, applePrefab))

        assertFailsWith<IllegalArgumentException> {
            inventoryService.consumeItems(owner, mapOf(applePrefab to 2))
        }
    }
}
