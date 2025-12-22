package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.ItemService
import cn.jzl.sect.ecs.Named
import cn.jzl.sect.ecs.itemAddon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ItemServiceTest {

    @Test
    fun testItemPrefabCreatesPrefabEntityAndRegistersIt() {
        val world = world {
            install(itemAddon)
        }

        val itemService by world.di.instance<ItemService>()

        val potion = itemService.itemPrefab(Named("Potion")) { /* no extra data */ }

        assertTrue(itemService.isItemPrefab(potion))
        assertEquals(listOf(potion), itemService.itemPrefabs().toList())
    }

    @Test
    fun testItemInstanceOfPrefabIsNotPrefab() {
        val world = world {
            install(itemAddon)
        }

        val itemService by world.di.instance<ItemService>()

        val swordPrefab = itemService.itemPrefab(Named("Sword")) { }
        val sword = itemService.item(swordPrefab) { }

        assertTrue(itemService.isItemPrefab(swordPrefab))
        assertTrue(!itemService.isItemPrefab(sword))
    }
}
