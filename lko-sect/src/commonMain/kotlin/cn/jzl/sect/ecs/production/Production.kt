package cn.jzl.sect.ecs.production

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.sect.ecs.inventory.InventoryService
import cn.jzl.sect.ecs.item.ItemService
import kotlin.getValue
import kotlin.sequences.forEach

/**
 * 资源生产系统包，包含资源生产组件、服务和addon配置
 *
 * 主要功能：
 * 1. 定义资源产出组件
 * 2. 提供资源生产服务
 * 3. 支持资源产出配置和执行
 * 4. 实现资源生产逻辑
 */

/**
 * 资源生产addon
 * 注册资源生产相关组件和服务
 */
val resourceProductionAddon = createAddon("resourceProduction") {
    injects {
        this bind singleton { new(::ResourceProductionService) }
    }
    entities { world.componentId<ResourceOutputAmount>() }
}


/**
 * 资源产出配置接口
 * 用于配置资源生产实体的产出
 */
fun interface ResourceOutputConfig {
    /**
     * 添加资源产出
     *
     * @param itemPrefab 物品预制体
     * @param amount 产出数量
     */
    fun addOutput(itemPrefab: Entity, amount: Int)
}

/**
 * 资源产出数量组件
 * 表示资源生产实体的产出数量
 *
 * @param amount 产出数量
 */
@JvmInline
value class ResourceOutputAmount(val amount: Int)

/**
 * 资源生产服务
 * 管理资源生产系统的核心功能
 *
 * @param world ECS世界实例
 */
class ResourceProductionService(world: World) : EntityRelationContext(world) {

    private val inventoryService by world.di.instance<InventoryService>()
    private val itemService by world.di.instance<ItemService>()

    /**
     * 配置资源产出
     *
     * @param context 实体创建上下文
     * @param entity 目标实体
     * @param block 产出配置块
     */
    @ECSDsl
    fun configureOutput(context: EntityCreateContext, entity: Entity, block: ResourceOutputConfig.() -> Unit) = context.run {
        ResourceOutputConfig { itemPrefab, amount ->
            require(itemService.isItemPrefab(itemPrefab)) { }
            entity.addRelation(itemPrefab, ResourceOutputAmount(amount))
        }.block()
    }

    /**
     * 执行资源生产
     *
     * @param context 实体创建上下文
     * @param receiver 资源接收者
     * @param producer 资源生产者
     */
    fun executeProduction(context: EntityCreateContext, receiver: Entity, producer: Entity) = context.run {
        producer.getRelationsWithData<ResourceOutputAmount>().forEach {
            inventoryService.addItem(receiver, it.relation.target, it.data.amount)
        }
    }

    /**
     * 获取资源产出数量
     *
     * @param producer 资源生产者
     * @param itemPrefab 物品预制体
     * @return 产出数量
     */
    fun getOutputAmount(producer: Entity, itemPrefab: Entity): Int = producer.getRelation<ResourceOutputAmount?>(itemPrefab)?.amount ?: 0
}