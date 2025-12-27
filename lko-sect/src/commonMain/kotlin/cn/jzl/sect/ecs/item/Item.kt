package cn.jzl.sect.ecs.item

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.associatedBy
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.core.Named

/**
 * 物品系统包，包含物品组件、服务和addon配置
 *
 * 主要功能：
 * 1. 定义物品的基本属性和标记
 * 2. 提供物品预制体和实例化功能
 * 3. 管理物品的堆叠性和可用性
 */

/**
 * 物品标记组件
 * 用于标识实体为物品
 */
sealed class Item

/**
 * 可堆叠标记组件
 * 表示物品可以堆叠
 */
sealed class Stackable

/**
 * 可用标记组件
 * 表示物品可以被使用
 */
sealed class Usable

/**
 * 单价组件
 * 表示物品的单位价格
 *
 * @param price 物品单价
 */
@JvmInline
value class UnitPrice(val price: Int)

/**
 * 物品addon
 * 注册物品相关组件和服务
 */
val itemAddon = createAddon("item") {
    injects { this bind singleton { new(::ItemService) } }
    components {
        world.componentId<Item> { it.tag() }
        world.componentId<Stackable> { it.tag() }
        world.componentId<Usable> { it.tag() }
        world.componentId<UnitPrice>()
    }
}

/**
 * 物品服务
 * 管理物品预制体和实例
 *
 * @param world ECS世界实例
 */
class ItemService(world: World) : EntityRelationContext(world) {

    private val itemPrefabs = world.query { EntityItemPrefabContext(this) }.associatedBy { named }

    /**
     * 获取所有物品预制体
     *
     * @return 物品预制体序列
     */
    fun itemPrefabs(): Sequence<Entity> = itemPrefabs.entities

    /**
     * 检查实体是否为物品预制体
     *
     * @param itemPrefab 要检查的实体
     * @return 是否为物品预制体
     */
    fun isItemPrefab(itemPrefab: Entity): Boolean {
        return itemPrefab.hasTag<Item>() && itemPrefab.hasPrefab()
    }

    /**
     * 检查物品是否可堆叠
     *
     * @param item 物品实体
     * @return 是否可堆叠
     */
    fun isStackable(item: Entity): Boolean = item.hasTag<Stackable>()

    /**
     * 检查物品是否可用
     *
     * @param item 物品实体
     * @return 是否可用
     */
    fun isUsable(item: Entity): Boolean = item.hasTag<Usable>()

    /**
     * 创建物品预制体
     *
     * @param named 物品名称
     * @param block 物品配置块
     * @return 创建的物品预制体
     */
    @ECSDsl
    fun itemPrefab(named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity {
        require(named !in itemPrefabs) { "物品名称已存在: ${named.name}" }
        return world.prefab {
            it.addComponent(named)
            it.addTag<Item>()
            block(it)
        }
    }

    /**
     * 从预制体创建物品实例
     *
     * @param itemPrefab 物品预制体
     * @param block 物品实例配置块
     * @return 创建的物品实例
     */
    fun item(itemPrefab: Entity, block: EntityCreateContext.(Entity) -> Unit): Entity {
        require(isItemPrefab(itemPrefab)) { "实体不是物品预制体" }
        return world.instanceOf(itemPrefab, block)
    }

    /**
     * 物品预制体查询上下文
     * 用于查询物品预制体及其属性
     *
     * @param world ECS世界实例
     * @param forPrefabs 是否仅查询预制体
     */
    private class EntityItemPrefabContext(world: World) : EntityQueryContext(world, true) {

        val named by component<Named>()
        val isStackable by ifRelationExist(relations.component<Stackable>())
        val price by component<UnitPrice?>()

        /**
         * 配置查询条件
         */
        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Item>())
            relation(relations.component(components.prefab))
        }
    }
}
