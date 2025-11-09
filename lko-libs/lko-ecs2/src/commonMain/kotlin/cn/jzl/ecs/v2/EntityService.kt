package cn.jzl.ecs.v2

import cn.jzl.datastructure.signal.Signal
import cn.jzl.di.instance

/**
 * EntityService - 实体服务
 * 
 * 核心职责：
 * - 管理实体的创建、配置和销毁生命周期
 * - 提供实体查询和访问的统一入口
 * - 发布实体生命周期事件通知
 * - 协调实体与组件服务的交互
 * 
 * 实体服务是ECS框架中实体管理的核心组件，它封装了实体的CRUD操作，
 * 并确保在实体状态变化时通知相关系统，维护整个ECS生态的一致性。
 * 
 * @property world ECS世界实例
 */
class EntityService(private val world: World) {
    /**
     * 实体存储服务 - 负责实体ID和版本管理
     */
    private val entityStore by world.instance<EntityStore>()
    
    /**
     * 实体更新上下文 - 提供实体组件操作能力
     */
    private val entityUpdateContext by world.instance<EntityUpdateContext>()
    
    /**
     * 组件服务 - 管理组件的存储和访问
     */
    private val componentService by world.instance<ComponentService>()

    /**
     * 获取所有活动实体的序列
     * 
     * @return 实体序列
     */
    val entities: Sequence<Entity> get() = entityStore.entities
    
    /**
     * 获取当前活动实体数量
     * 
     * @return 实体数量
     */
    val size: Int get() = entityStore.size

    /**
     * 实体创建信号 - 当实体被创建时触发
     */
    val onEntityCreate = Signal<Entity>()
    
    /**
     * 实体更新信号 - 当实体组件被修改时触发
     */
    val onEntityUpdate = Signal<Entity>()
    
    /**
     * 实体销毁信号 - 当实体被销毁时触发
     */
    val onEntityDestroy = Signal<Entity>()

    /**
     * 检查实体是否存在 - 实现contains运算符
     * 
     * @param entity 要检查的实体
     * @return 如果实体存在返回true
     */
    operator fun contains(entity: Entity): Boolean = entity in entityStore

    /**
     * 创建新实体
     * 
     * @param configuration 实体配置函数，用于初始化实体的组件
     * @return 创建的新实体
     */
    fun create(configuration: EntityCreateContext.(Entity) -> Unit): Entity {
        // 创建实体并执行后续处理
        return postCreate(entityStore.create(), configuration)
    }

    /**
     * 使用指定ID创建实体
     * 
     * @param entityId 指定的实体ID
     * @param configuration 实体配置函数
     * @return 创建的实体
     */
    fun create(entityId: Int, configuration: EntityCreateContext.(Entity) -> Unit): Entity {
        // 使用指定ID创建实体并执行后续处理
        return postCreate(entityStore.create(entityId), configuration)
    }

    /**
     * 实体创建后处理 - 内部方法
     * 
     * @param entity 创建的实体
     * @param configuration 配置函数
     * @return 配置完成的实体
     */
    private inline fun postCreate(entity: Entity, configuration: EntityCreateContext.(Entity) -> Unit): Entity {
        // 配置实体组件
        entityUpdateContext.configuration(entity)
        // 触发创建信号
        onEntityCreate(entity)
        return entity
    }

    /**
     * 配置现有实体
     * 
     * @param entity 要配置的实体
     * @param configuration 配置函数
     * @throws IllegalArgumentException 如果实体不存在
     */
    fun configure(entity: Entity, configuration: EntityUpdateContext.(Entity) -> Unit) {
        // 验证实体是否存在
        require(entity in entityStore) { "Entity $entity is not in entityStore" }
        // 配置实体组件
        entityUpdateContext.configuration(entity)
        // 触发更新信号
        onEntityUpdate(entity)
    }

    /**
     * 通过ID获取实体 - 实现get运算符
     * 
     * @param entityId 实体ID
     * @return 对应的实体
     */
    operator fun get(entityId: Int): Entity = entityStore[entityId]

    /**
     * 移除实体 - 完整的实体销毁流程实现
     * 
     * 实现逻辑：
     * 1. 验证实体是否存在于实体存储中
     * 2. 获取实体的组件位集，了解其拥有的所有组件
     * 3. 迭代并移除实体的所有组件，确保资源正确释放
     * 4. 清空组件位集，清除组件关联信息
     * 5. 触发实体销毁信号，通知所有监听系统
     * 6. 从实体存储中移除实体，释放ID和版本信息
     * 
     * 这个方法确保了实体的干净销毁，包括组件的移除、资源释放和事件通知，
     * 维持了ECS系统的一致性和完整性。采用空安全操作确保即使在组件移除过程中
     * 出现异常情况也能继续执行销毁流程。
     * 
     * @param entity 要移除的实体
     * @return 如果实体存在并成功移除返回true，如果实体不存在返回false
     */
    fun remove(entity: Entity): Boolean {
        // 检查实体是否存在于实体存储中，如果不存在则直接返回false
        if (entity !in entityStore) return false
        
        // 获取实体的组件位集，包含实体当前拥有的所有组件类型索引
        val componentBits = componentService.componentBits(entity)
        
        // 遍历组件位集中的每个索引，移除实体的所有组件
        // 使用holderOrNull安全获取组件持有器，避免空指针异常
        componentBits.forEach { componentIndex ->
            world.componentService.holderOrNull<Any>(componentIndex)?.remove(entity)
        }
        
        // 清空组件位集，确保没有引用残留
        componentBits.clear()
        
        // 触发实体销毁信号，通知所有监听的系统进行相应处理
        onEntityDestroy(entity)
        
        // 最后从实体存储中移除实体，释放实体ID供重用
        entityStore -= entity
        
        // 返回操作成功标志
        return true
    }
}