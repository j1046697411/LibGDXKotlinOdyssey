package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 宗门服务测试
 *
 * 测试宗门的创建、成员管理、建筑管理等功能
 */
class SectServiceTest {

    private val world by lazy {
        world {
            install(sectAddon)
        }
    }

    private val sectService by world.di.instance<SectService>()

    /**
     * 测试创建宗门
     */
    @Test
    fun testCreateSectWithName() {
        val leader = world.entity {
            it.addComponent(Named("玄天道长"))
        }

        val sect = sectService.createSect("飞仙派", leader) {}

        // 验证宗门创建成功
        assertNotNull(sect)
        world.entity(sect) { assertTrue(it.hasTag<Sect>()) }

        // 验证宗门名称
        world.entity(sect) {
            val named = it.getComponent<Named>()
            assertNotNull(named)
            assertEquals("飞仙派", named.name)
        }

        // 验证初始声望为0
        world.entity(sect) {
            val reputation = it.getComponent<SectReputation>()
            assertNotNull(reputation)
            assertEquals(0, reputation.value)
        }

        // 验证初始金钱为1000
        world.entity(sect) {
            val money = it.getComponent<Money>()
            assertNotNull(money)
            assertEquals(1000, money.value)
        }
    }

    /**
     * 测试宗门名称唯一性
     */
    @Test
    fun testCreateSectWithDuplicateNameFails() {
        val leader1 = world.entity {
            it.addComponent(Named("道长1"))
        }
        val leader2 = world.entity {
            it.addComponent(Named("道长2"))
        }

        sectService.createSect("飞仙派", leader1) {}

        // 尝试创建相同名称的宗门应该失败
        try {
            sectService.createSect("飞仙派", leader2) {}
            assertTrue(false, "Should have thrown exception for duplicate sect name")
        } catch (e: IllegalArgumentException) {
            assertTrue(true, "Expected exception for duplicate sect name")
        }
    }

    /**
     * 测试通过名称查询宗门
     */
    @Test
    fun testGetSectByName() {
        val leader = world.entity {
            it.addComponent(Named("掌门"))
        }

        val sect = sectService.createSect("天道宗", leader) {}

        // 通过名称查询
        val queriedSect = sectService["天道宗"]
        assertNotNull(queriedSect)
        assertEquals(sect.id, queriedSect.id)

        // 查询不存在的宗门返回 null
        val notFound = sectService["不存在的宗门"]
        assertNull(notFound)
    }

    /**
     * 测试添加宗门成员
     */
    @Test
    fun testAddMember() {
        val leader = world.entity {
            it.addComponent(Named("掌门"))
        }
        val sect = sectService.createSect("天道宗", leader) {}

        val disciple = world.entity {
            it.addComponent(Named("弟子1"))
        }

        // 添加弟子
        sectService.addMember(sect, disciple, MemberRole.OUTER_DISCIPLE)

        // 验证成员已添加
        val memberData = sectService.getMemberData(sect, disciple)
        assertNotNull(memberData)
        assertEquals(MemberRole.OUTER_DISCIPLE, memberData.role)
        assertEquals(0, memberData.contribution.value)
    }

    /**
     * 测试添加重复成员失败
     */
    @Test
    fun testAddDuplicateMemberFails() {
        val leader = world.entity {
            it.addComponent(Named("掌门"))
        }
        val sect = sectService.createSect("天道宗", leader) {}

        val disciple = world.entity {
            it.addComponent(Named("弟子1"))
        }

        sectService.addMember(sect, disciple, MemberRole.OUTER_DISCIPLE)

        // 尝试再次添加相同成员应该失败
        try {
            sectService.addMember(sect, disciple, MemberRole.OUTER_DISCIPLE)
            assertTrue(false, "Should have thrown exception for duplicate member")
        } catch (e: IllegalArgumentException) {
            assertTrue(true, "Expected exception for duplicate member")
        }
    }

    /**
     * 测试移除成员
     */
    @Test
    fun testRemoveMember() {
        val leader = world.entity {
            it.addComponent(Named("掌门"))
        }
        val sect = sectService.createSect("天道宗", leader) {}

        val disciple = world.entity {
            it.addComponent(Named("弟子1"))
        }

        sectService.addMember(sect, disciple, MemberRole.OUTER_DISCIPLE)
        assertNotNull(sectService.getMemberData(sect, disciple))

        // 移除成员
        sectService.removeMember(sect, disciple)

        // 验证成员已移除
        assertNull(sectService.getMemberData(sect, disciple))
    }

    /**
     * 测试改变成员角色
     */
    @Test
    fun testChangeMemberRole() {
        val leader = world.entity {
            it.addComponent(Named("掌门"))
        }
        val sect = sectService.createSect("天道宗", leader) {}

        val disciple = world.entity {
            it.addComponent(Named("弟子1"))
        }

        sectService.addMember(sect, disciple, MemberRole.OUTER_DISCIPLE)

        // 升级为长老
        sectService.changeMemberRole(sect, disciple, MemberRole.ELDER)

        val memberData = sectService.getMemberData(sect, disciple)
        assertNotNull(memberData)
        assertEquals(MemberRole.ELDER, memberData.role)
    }

    /**
     * 测试增加贡献度
     */
    @Test
    fun testAddContribution() {
        val leader = world.entity {
            it.addComponent(Named("掌门"))
        }
        val sect = sectService.createSect("天道宗", leader) {}

        val disciple = world.entity {
            it.addComponent(Named("弟子1"))
        }

        sectService.addMember(sect, disciple, MemberRole.OUTER_DISCIPLE)

        // 增加贡献度
        sectService.addContribution(sect, disciple, 100)

        val memberData = sectService.getMemberData(sect, disciple)
        assertNotNull(memberData)
        assertEquals(100, memberData.contribution.value)

        // 再次增加贡献度
        sectService.addContribution(sect, disciple, 50)

        val updatedMemberData = sectService.getMemberData(sect, disciple)
        assertNotNull(updatedMemberData)
        assertEquals(150, updatedMemberData.contribution.value)
    }

    /**
     * 测试获取所有成员
     */
    @Test
    fun testGetAllMembers() {
        val leader = world.entity {
            it.addComponent(Named("掌门"))
        }
        val sect = sectService.createSect("天道宗", leader) {}

        val disciple1 = world.entity {
            it.addComponent(Named("弟子1"))
        }
        val disciple2 = world.entity {
            it.addComponent(Named("弟子2"))
        }

        sectService.addMember(sect, disciple1, MemberRole.OUTER_DISCIPLE)
        sectService.addMember(sect, disciple2, MemberRole.OUTER_DISCIPLE)

        // 获取所有成员 (包括掌门)
        val members = sectService.getMembers(sect).toList()

        assertEquals(3, members.size)
        assertTrue(members.any { it.second.role == MemberRole.LEADER })
        assertTrue(members.count { it.second.role == MemberRole.OUTER_DISCIPLE } == 2)
    }

    /**
     * 测试增加宗门声望
     */
    @Test
    fun testAddReputation() {
        val leader = world.entity {
            it.addComponent(Named("掌门"))
        }
        val sect = sectService.createSect("天道宗", leader) {}

        // 初始声望为0
        world.entity(sect) {
            val reputation = it.getComponent<SectReputation>()
            assertEquals(0, reputation.value)
        }

        // 增加声望
        sectService.addReputation(sect, 100)

        world.entity(sect) {
            val reputation = it.getComponent<SectReputation>()
            assertEquals(100, reputation.value)
        }

        // 再次增加
        sectService.addReputation(sect, 50)

        world.entity(sect) {
            val reputation = it.getComponent<SectReputation>()
            assertEquals(150, reputation.value)
        }
    }

    /**
     * 测试创建炼丹房
     */
    @Test
    fun testCreateAlchemyHall() {
        val leader = world.entity {
            it.addComponent(Named("掌门"))
        }
        val sect = sectService.createSect("天道宗", leader) {}

        val alchemyHall = sectService.createAlchemyHall(sect, Named("长生殿")) {}

        // 验证建筑标签
        world.entity(alchemyHall) {
            assertTrue(it.hasTag<Building>())
            assertTrue(it.hasTag<AlchemyHall>())

            // 验证建筑名称
            val named = it.getComponent<Named>()
            assertNotNull(named)
            assertEquals("长生殿", named.name)

            // 验证建筑属于宗门
            val ownedBySect = it.getRelationUp<OwnedBy>()
            assertNotNull(ownedBySect)
            assertEquals(sect, ownedBySect)
        }
    }

    /**
     * 测试创建藏经阁
     */
    @Test
    fun testCreateLibrary() {
        val leader = world.entity {
            it.addComponent(Named("掌门"))
        }
        val sect = sectService.createSect("天道宗", leader) {}

        val library = sectService.createLibrary(sect, Named("太初阁")) {}

        // 验证建筑标签
        world.entity(library) {
            assertTrue(it.hasTag<Building>())
            assertTrue(it.hasTag<Library>())

            // 验证建筑名称
            val named = it.getComponent<Named>()
            assertNotNull(named)
            assertEquals("太初阁", named.name)
        }
    }
}
