package cn.jzl.sect.ecs.core

import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId

/**
 * 核心组件包，包含所有ECS系统的基础组件
 *
 * 主要提供：
 * 1. 实体命名和描述
 * 2. 关系标记组件
 * 3. 核心addon配置
 */

/**
 * 命名组件
 * 用于标识实体的名称
 *
 * @param name 实体名称
 */
@JvmInline
value class Named(val name: String) {
    companion object {
        val EMPTY = Named("")
    }
}

/**
 * 描述组件
 * 用于提供实体的详细描述
 *
 * @param description 实体描述
 */
@JvmInline
value class Description(val description: String)

/**
 * 所有权关系标记
 * 表示实体被谁拥有的关系
 *
 * 用法：
 * ```kotlin
 * entity.addRelation<OwnedBy>(ownerEntity)
 * ```
 */
sealed class OwnedBy

/**
 * 使用关系标记
 * 表示实体被谁使用的关系
 *
 * 用法：
 * ```kotlin
 * entity.addRelation<UsedBy>(userEntity)
 * ```
 */
sealed class UsedBy

/**
 * 核心addon
 * 注册所有基础组件，是其他addon的基础依赖
 */
val coreAddon = createAddon("core", {}) {
    components {
        world.componentId<Named>()
        world.componentId<OwnedBy> {
            it.singleRelation()
            it.tag()
        }
        world.componentId<UsedBy> {
            it.singleRelation()
            it.tag()
        }
    }
}
