package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.attribute.AttributeService
import cn.jzl.sect.ecs.attribute.AttributeValue
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.upgradeable.ExperienceFormula
import cn.jzl.sect.ecs.upgradeable.LevelingService
import cn.jzl.sect.ecs.upgradeable.Upgradeable
import cn.jzl.sect.ecs.upgradeable.levelingAddon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 升级系统测试
 *
 * 测试经验值、等级、升级事件等功能
 */
class LevelingServiceTest {

    private val world by lazy {
        world {
            install(levelingAddon)
        }
    }

    private val levelingService by world.di.instance<LevelingService>()
    private val attributeService by world.di.instance<AttributeService>()

    private val attributeLevel by lazy { attributeService.attribute(LevelingService.ATTRIBUTE_LEVEL) }
    private val attributeExperience by lazy { attributeService.attribute(LevelingService.ATTRIBUTE_EXPERIENCE) }

    /**
     * 测试创建可升级实体
     */
    @Test
    fun testCreateUpgradeableEntity() {
        val entity = world.entity {
            it.addComponent(Named("测试对象"))
            levelingService.upgradeable(this, it)
        }

        // 验证实体是可升级的
        assertNotNull(entity)

        world.entity(entity) {
            assertTrue(it.hasTag<Upgradeable>())

            // 验证初始等级为1
            val level = it.getRelation<AttributeValue?>(attributeLevel)
            assertNotNull(level)
            assertEquals(1L, level.value)

            // 验证初始经验为0
            val experience = it.getRelation<AttributeValue?>(attributeExperience)
            assertNotNull(experience)
            assertEquals(0L, experience.value)
        }
    }

    /**
     * 测试添加经验值
     */
    @Test
    fun testAddExperience() {
        val entity = world.entity {
            it.addComponent(Named("测试对象"))
            levelingService.upgradeable(this, it)
        }

        // 添加经验
        levelingService.addExperience(entity, 50)

        world.entity(entity) {
            val experience = it.getRelation<AttributeValue?>(attributeExperience)
            assertNotNull(experience)
            assertEquals(50L, experience.value)

            // 等级应该保持为1
            val level = it.getRelation<AttributeValue?>(attributeLevel)
            assertNotNull(level)
            assertEquals(1L, level.value)
        }
    }

    /**
     * 测试升级（经验值达到升级条件）
     */
    @Test
    fun testUpgrade() {
        val entity = world.entity {
            it.addComponent(Named("测试对象"))
            levelingService.upgradeable(this, it)
        }

        // 添加足够的经验升级 (需要100经验)
        levelingService.addExperience(entity, 100)

        world.entity(entity) {
            // 验证升级后等级为2
            val level = it.getRelation<AttributeValue?>(attributeLevel)
            assertNotNull(level)
            assertEquals(2L, level.value)

            // 验证剩余经验为0
            val experience = it.getRelation<AttributeValue?>(attributeExperience)
            assertNotNull(experience)
            assertEquals(0L, experience.value)
        }
    }

    /**
     * 测试多级升级
     */
    @Test
    fun testMultipleUpgrades() {
        val entity = world.entity {
            it.addComponent(Named("测试对象"))
            levelingService.upgradeable(this, it)
        }

        // 添加足够升级到第3级的经验
        // 第1->2级需要100, 第2->3级需要200, 总共300
        levelingService.addExperience(entity, 300)

        world.entity(entity) {
            // 验证升级到第3级
            val level = it.getRelation<AttributeValue?>(attributeLevel)
            assertNotNull(level)
            assertEquals(4L, level.value)

            // 验证剩余经验为0
            val experience = it.getRelation<AttributeValue?>(attributeExperience)
            assertNotNull(experience)
            assertEquals(0L, experience.value)
        }
    }

    /**
     * 测试升级后有剩余经验
     */
    @Test
    fun testUpgradeWithRemainingExperience() {
        val entity = world.entity {
            it.addComponent(Named("测试对象"))
            levelingService.upgradeable(this, it)
        }

        // 添加120经验（升级后剩余20）
        levelingService.addExperience(entity, 120)

        world.entity(entity) {
            // 验证升级到第2级
            val level = it.getRelation<AttributeValue?>(attributeLevel)
            assertNotNull(level)
            assertEquals(2L, level.value)

            // 验证剩余经验为20
            val experience = it.getRelation<AttributeValue?>(attributeExperience)
            assertNotNull(experience)
            assertEquals(20L, experience.value)
        }
    }

    /**
     * 测试自定义经验公式
     */
    @Test
    fun testCustomExperienceFormula() {
        val customFormula = cn.jzl.sect.ecs.upgradeable.ExperienceFormula { level -> level * 50 }

        val entity = world.entity {
            it.addComponent(Named("测试对象"))
            levelingService.upgradeable(this, it, customFormula)
        }

        // Verify formula component present and works
        world.entity(entity) {
            val comp = it.getComponent<ExperienceFormula?>()
            assertNotNull(comp)
            // for level 2, required exp should be 100
            assertEquals(100L, comp.getExperienceForLevel(2))
        }

        // 1->2级需要100经验 (level * 50, with next level = 2)
        levelingService.addExperience(entity, 100)

        world.entity(entity) {
            val level = it.getRelation<AttributeValue?>(attributeLevel)
            assertNotNull(level)
            assertEquals(2L, level.value)
        }

        // 2->3级需要150经验 (next level = 3)
        levelingService.addExperience(entity, 150)

        world.entity(entity) {
            val level2 = it.getRelation<AttributeValue?>(attributeLevel)
            assertNotNull(level2)
            assertEquals(3L, level2.value)
        }
    }

    /**
     * 测试连续添加经验
     */
    @Test
    fun testAddExperienceMultipleTimes() {
        val entity = world.entity {
            it.addComponent(Named("测试对象"))
            levelingService.upgradeable(this, it)
        }

        // 第一次添加50经验
        levelingService.addExperience(entity, 50)

        world.entity(entity) {
            val experience = it.getRelation<AttributeValue?>(attributeExperience)
            assertNotNull(experience)
            assertEquals(50L, experience.value)
        }

        // 第二次添加60经验（总共110，应该升级）
        levelingService.addExperience(entity, 60)

        world.entity(entity) {
            val level = it.getRelation<AttributeValue?>(attributeLevel)
            assertNotNull(level)
            assertEquals(2L, level.value)

            val experience = it.getRelation<AttributeValue?>(attributeExperience)
            assertNotNull(experience)
            assertEquals(10L, experience.value)
        }
    }
}
