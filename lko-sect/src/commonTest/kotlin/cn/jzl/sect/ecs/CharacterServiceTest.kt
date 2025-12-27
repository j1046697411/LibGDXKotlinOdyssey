package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.character.Character
import cn.jzl.sect.ecs.character.CharacterService
import cn.jzl.sect.ecs.character.characterAddon
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.upgradeable.Upgradeable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 角色服务测试
 *
 * 测试角色的创建和生命周期管理
 */
class CharacterServiceTest {

    private val world by lazy {
        world {
            install(characterAddon)
        }
    }

    private val characterService by world.di.instance<CharacterService>()

    /**
     * 测试创建角色
     */
    @Test
    fun testCreateCharacter() {
        val character = characterService.createCharacter(Named("李白")) {}

        // 验证角色创建成功
        assertNotNull(character)

        // 验证标签和属性在 entity context 中访问
        world.entity(character) {
            assertTrue(it.hasTag<Character>())
            val named = it.getComponent<Named>()
            assertNotNull(named)
            assertEquals("李白", named.name)
            // 验证角色是可升级的
            assertTrue(it.hasTag<Upgradeable>())
        }
    }

    /**
     * 测试角色名称唯一性
     */
    @Test
    fun testCreateCharacterWithDuplicateNameFails() {
        characterService.createCharacter(Named("李白")) {}

        try {
            characterService.createCharacter(Named("李白")) {}
            assertTrue(false, "Should have thrown exception for duplicate character name")
        } catch (e: IllegalArgumentException) {
            assertTrue(true, "Expected exception for duplicate character name")
        }
    }

    /**
     * 测试角色初始等级（简化：验证 Upgradeable 标签存在）
     */
    @Test
    fun testCharacterInitialLevel() {
        val character = characterService.createCharacter(Named("白居易")) {}

        world.entity(character) {
            // 简化检查：确保标记为可升级
            assertTrue(it.hasTag<Upgradeable>())
        }
    }
}
