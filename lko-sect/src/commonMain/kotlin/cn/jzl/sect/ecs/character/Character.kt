package cn.jzl.sect.ecs.character

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.cultivation.CultivationService
import cn.jzl.sect.ecs.cultivation.cultivationAddon
import cn.jzl.sect.ecs.healing.healthAddon
import cn.jzl.sect.ecs.upgradeable.levelingAddon

/**
 * 角色系统包，包含角色组件、服务和addon配置
 *
 * 主要功能：
 * 1. 定义角色实体标记
 * 2. 提供角色创建服务
 * 3. 整合健康、升级和修炼系统
 */

/**
 * 角色标记组件
 * 用于标识实体为角色
 */
sealed class Character

/**
 * 角色addon
 * 注册角色相关组件和服务，并整合其他依赖系统
 */
val characterAddon = createAddon("character") {
    install(healthAddon)
    install(levelingAddon)
    install(cultivationAddon)
    injects { this bind singleton { new(::CharacterService) } }
    components {
        world.componentId<Character> { it.tag() }
    }
}

/**
 * 角色服务
 * 负责角色的创建和管理
 *
 * @param world ECS世界实例
 */
class CharacterService(world: World) : EntityRelationContext(world) {

    private val cultivationService by world.di.instance<CultivationService>()

    /**
     * 创建角色实体
     *
     * @param named 角色名称
     * @param block 角色配置块，用于添加额外组件和关系
     * @return 创建的角色实体
     */
    @ECSDsl
    fun createCharacter(named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity {
        return world.entity {
            cultivationService.cultivable(this, it)
            it.addTag<Character>()
            it.addComponent(named)
            block(it)
        }
    }
}
