package cn.jzl.sect.ecs.attribute

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.AddonSetup
import cn.jzl.ecs.addon.WorldSetup
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.associatedBy
import cn.jzl.ecs.query.query
import cn.jzl.ecs.system.Phase
import cn.jzl.sect.ecs.core.Named
import kotlin.math.roundToLong
import kotlin.random.Random

/**
 * 属性系统包，包含属性组件、服务和配置
 * 
 * 主要功能：
 * 1. 定义属性的基本组件和值类型
 * 2. 提供属性创建、查询和修改的服务
 * 3. 支持属性提供者扩展
 * 4. 定义宗门常用属性
 */

/**
 * 属性标记组件
 * 用于标识实体为属性
 */
sealed class Attribute

/**
 * 属性值组件
 * 表示属性的具体数值
 * 
 * @param value 属性值
 */
@JvmInline
value class AttributeValue(val value: Long) {
    /**
     * 加法运算符重载，用于属性值相加
     * 
     * @param other 另一个属性值
     * @return 相加后的属性值
     */
    operator fun plus(other: AttributeValue): AttributeValue = AttributeValue(value + other.value)

    companion object {
        /**
         * 零值属性
         */
        val zero = AttributeValue(0)
        
        /**
         * 单位属性值
         */
        val one = AttributeValue(1)
    }
}

/**
 * 属性注册表接口
 * 用于注册属性提供者
 */
interface AttributeRegistry {
    /**
     * 注册属性提供者
     * 
     * @param provider 属性提供者
     */
    fun register(provider: AttributeProvider)
}

/**
 * 属性构建器
 * 用于配置属性系统
 */
class AttributeBuilder {

    internal val providers = mutableListOf<WorldOwner.() -> AttributeProvider>()

    /**
     * 添加属性提供者
     * 
     * @param provider 属性提供者工厂函数
     */
    @ECSDsl
    fun provider(provider: WorldOwner.() -> AttributeProvider) {
        providers.add(provider)
    }
}

/**
 * Addon扩展函数，用于配置属性系统
 * 
 * @param config 属性配置块
 */
@ECSDsl
fun AddonSetup<*>.attributes(config: AttributeBuilder.() -> Unit) {
    install(attributeAddon, config)
}

/**
 * World扩展函数，用于配置属性系统
 * 
 * @param config 属性配置块
 */
@ECSDsl
fun WorldSetup.attributes(config: AttributeBuilder.() -> Unit) {
    install(attributeAddon, config)
}

/**
 * 属性addon
 * 注册属性组件和服务
 */
val attributeAddon = createAddon("Attribute", { AttributeBuilder() }) {
    injects { 
        this bind singleton { new(::AttributeService) }
        this bind singleton { new(::SectAttributes) }
    }
    components { 
        world.componentId<Attribute> { it.tag() }
        world.componentId<AttributeValue>()
    }
    on(Phase.ADDONS_CONFIGURED) {
        val attributeService by world.di.instance<AttributeService>()
        configuration.providers.forEach { provider ->
            attributeService.register(provider())
        }
    }
}

/**
 * 属性提供者接口
 * 用于提供额外的属性值计算
 * 
 * @param attributeService 属性服务实例
 * @param entity 目标实体
 * @param attribute 属性实体
 * @return 计算得到的属性值
 */
fun interface AttributeProvider {
    fun getAttributeValue(attributeService: AttributeService, entity: Entity, attribute: Entity): AttributeValue
}

/**
 * 属性助手接口
 * 用于生成和设置属性值
 */
interface AttributeAssistant {
    
    /**
     * 生成属性值
     * 
     * @param attribute 属性实体
     * @param minPer 最小值百分比（0f-1f）
     * @param maxPer 最大值百分比（0f-1f）
     * @return 生成的属性值
     */
    fun generateAttribute(attribute: Entity, minPer: Float, maxPer: Float): AttributeValue

    /**
     * 辅助设置属性值
     * 
     * @param context 实体创建上下文
     * @param owner 属性所有者
     * @param attribute 属性实体
     * @param baseValue 基础属性值
     */
    fun assistAttribute(context: EntityCreateContext, owner: Entity, attribute: Entity, baseValue: AttributeValue)

    /**
     * 范围属性助手
     * 根据范围生成属性值
     * 
     * @param range 属性值范围
     */
    class RangeAttributeAssistant(val range: LongRange) : AttributeAssistant {

        /**
         * 根据范围和百分比生成属性值
         * 
         * @param attribute 属性实体
         * @param minPer 最小值百分比
         * @param maxPer 最大值百分比
         * @return 生成的属性值
         */
        override fun generateAttribute(attribute: Entity, minPer: Float, maxPer: Float): AttributeValue {
            val interval = range.last + 1 - range.first
            val minOffset = (interval * minPer.coerceIn(0f, maxPer)).roundToLong()
            val maxOffset = (interval * maxPer.coerceIn(minPer, 1f)).roundToLong()
            return AttributeValue(Random.nextLong(range.first + minOffset, range.first + maxOffset))
        }

        /**
         * 设置属性值，并确保在范围内
         * 
         * @param context 实体创建上下文
         * @param owner 属性所有者
         * @param attribute 属性实体
         * @param baseValue 基础属性值
         */
        override fun assistAttribute(context: EntityCreateContext, owner: Entity, attribute: Entity, baseValue: AttributeValue) = context.run {
            owner.addRelation(attribute, AttributeValue(baseValue.value.coerceIn(range.first, range.last)))
        }
    }
}

/**
 * 属性服务
 * 管理属性的创建、查询和修改
 * 
 * @param world ECS世界实例
 */
class AttributeService(world: World) : EntityRelationContext(world), AttributeRegistry {

    private val attributes = world.query { EntityAttributeContext(this) }.associatedBy { named }
    private val attributeProviders = mutableListOf<AttributeProvider>()

    /**
     * 获取所有属性名称
     * 
     * @return 属性名称序列
     */
    val attributeNames: Sequence<Named> get() = attributes.keys

    /**
     * 注册属性提供者
     * 
     * @param provider 属性提供者
     */
    override fun register(provider: AttributeProvider) {
        if (provider in attributeProviders) return
        attributeProviders.add(provider)
    }

    /**
     * 创建属性
     * 
     * @param named 属性名称
     * @param attributeAssistant 属性助手
     * @return 创建的属性实体
     */
    fun attribute(named: Named, attributeAssistant: AttributeAssistant): Entity {
        return world.entity {
            it.addTag<Attribute>()
            it.addComponent(named)
            it.addComponent(attributeAssistant)
        }
    }

    /**
     * 创建范围属性
     * 
     * @param named 属性名称
     * @param min 最小值
     * @param max 最大值
     * @return 创建的属性实体
     */
    fun attribute(named: Named, min: Long, max: Long): Entity = attribute(named, AttributeAssistant.RangeAttributeAssistant(min..max))

    /**
     * 创建或获取属性
     * 
     * @param named 属性名称
     * @param block 属性配置块
     * @return 属性实体
     */
    fun attribute(named: Named, block: EntityCreateContext.(Entity) -> Unit = {}): Entity {
        return attributes[named] ?: world.entity {
            it.addTag<Attribute>()
            it.addComponent(named)
            block(it)
        }
    }

    /**
     * 生成并设置属性值
     * 
     * @param context 实体创建上下文
     * @param owner 属性所有者
     * @param attribute 属性实体
     * @param minPer 最小值百分比
     * @param maxPer 最大值百分比
     */
    fun generateAttribute(context: EntityCreateContext, owner: Entity, attribute: Entity, minPer: Float = 0f, maxPer: Float = 1f) {
        val attributeAssistant = attribute.getComponent<AttributeAssistant>()
        attributeAssistant.assistAttribute(context, owner, attribute, attributeAssistant.generateAttribute(attribute, minPer, maxPer))
    }

    /**
     * 获取实体的属性值
     * 
     * @param owner 属性所有者
     * @param attribute 属性实体
     * @return 属性值，若不存在则返回null
     */
    fun getAttributeValue(owner: Entity, attribute: Entity): AttributeValue? = owner.getRelation<AttributeValue?>(attribute)

    /**
     * 设置实体的属性值
     * 
     * @param context 实体创建上下文
     * @param owner 属性所有者
     * @param attribute 属性实体
     * @param value 属性值
     */
    fun setAttributeValue(context: EntityCreateContext, owner: Entity, attribute: Entity, value: AttributeValue) {
        attribute.getComponent<AttributeAssistant>().assistAttribute(context, owner, attribute, value)
    }

    /**
     * 获取实体的所有属性
     * 
     * @param owner 属性所有者
     * @return 属性关系序列
     */
    fun getAttributes(owner: Entity): Sequence<RelationWithData<AttributeValue>> = owner.getRelationsWithData<AttributeValue>()

    /**
     * 获取实体的总属性值，包括所有属性提供者的贡献
     * 
     * @param owner 属性所有者
     * @param attribute 属性实体
     * @param defaultAttributeValue 默认属性值，若不存在则使用
     * @return 总属性值
     */
    fun getTotalAttributeValue(owner: Entity, attribute: Entity, defaultAttributeValue: AttributeValue = AttributeValue.zero): AttributeValue {
        return attributeProviders.fold(getAttributeValue(owner, attribute) ?: defaultAttributeValue) { acc, attributeProvider ->
            acc + attributeProvider.getAttributeValue(this, owner, attribute)
        }
    }

    /**
     * 属性查询上下文
     * 用于查询属性及其属性助手
     * 
     * @param world ECS世界实例
     */
    private class EntityAttributeContext(world: World) : EntityQueryContext(world) {

        val named by component<Named>()
        val attributeAssistant by component<AttributeAssistant>()

        /**
         * 配置查询条件
         */
        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Attribute>())
        }
    }
}

/**
 * 宗门属性
 * 预定义宗门常用的各种属性
 * 
 * @param world ECS世界实例
 */
class SectAttributes(world: World) {
    private val attributeService by world.di.instance<AttributeService>()

    // 五行之灵根属性
    /**
     * 金属性灵根
     */
    val metalSpiritRoot: Entity by lazy { attributeService.attribute(Named("MetalSpiritRoot"), 0L, 100L) }
    
    /**
     * 木属性灵根
     */
    val woodSpiritRoot: Entity by lazy { attributeService.attribute(Named("WoodSpiritRoot"), 0, 100) }
    
    /**
     * 水属性灵根
     */
    val waterSpiritRoot: Entity by lazy { attributeService.attribute(Named("WaterSpiritRoot"), 0, 100) }
    
    /**
     * 火属性灵根
     */
    val fireSpiritRoot: Entity by lazy { attributeService.attribute(Named("FireSpiritRoot"), 0, 100) }
    
    /**
     * 土属性灵根
     */
    val earthSpiritRoot: Entity by lazy { attributeService.attribute(Named("EarthSpiritRoot"), 0, 100) }

    // 核心资质
    /**
     * 根骨资质
     */
    val rootBone: Entity by lazy { attributeService.attribute(Named("RootBone"), 0, 100) }
    
    /**
     * 悟性资质
     */
    val comprehension: Entity by lazy { attributeService.attribute(Named("comprehension"), 0, 100) }
    
    /**
     * 福缘资质
     */
    val fortune: Entity by lazy { attributeService.attribute(Named("fortune"), 0, 100) }
    
    /**
     * 体质资质
     */
    val constitution: Entity by lazy { attributeService.attribute(Named("constitution"), 0, 100) }
    
    /**
     * 魂力资质
     */
    val soulPower: Entity by lazy { attributeService.attribute(Named("soulPower"), 0, 100) }

    // 特殊资质
    /**
     * 剑道天赋
     */
    val swordTalent: Entity by lazy { attributeService.attribute(Named("swordTalent"), 0, 150) }
    
    /**
     * 炼丹天赋
     */
    val alchemyTalent: Entity by lazy { attributeService.attribute(Named("alchemyTalent"), 0, 150) }
    
    /**
     * 锻造天赋
     */
    val forgingTalent: Entity by lazy { attributeService.attribute(Named("forgingTalent"), 0, 150) }
    
    /**
     * 阵法天赋
     */
    val formationTalent: Entity by lazy { attributeService.attribute(Named("formationTalent"), 0, 150) }
    
    /**
     * 御兽天赋
     */
    val beastTamingTalent: Entity by lazy { attributeService.attribute(Named("beastTamingTalent"), 0, 150) }

    // 生命
    /**
     * 当前生命值
     */
    val health: Entity by lazy { attributeService.attribute(Named("Health"), 1L, Long.MAX_VALUE) }
    
    /**
     * 最大生命值
     */
    val maxHealth: Entity by lazy { attributeService.attribute(Named("MaxHealth"), 1L, Long.MAX_VALUE) }

    //灵力
    /**
     * 当前灵力值
     */
    val spiritualEnergy: Entity by lazy { attributeService.attribute(Named("SpiritualEnergy"), 1L, Long.MAX_VALUE) }
    
    /**
     * 最大灵力值
     */
    val maxSpiritualEnergy: Entity by lazy { attributeService.attribute(Named("MaxSpiritualEnergy"), 1L, Long.MAX_VALUE) }

    // 修为/ 最高修为/ 境界
    /**
     * 当前修为
     */
    val cultivation: Entity by lazy { attributeService.attribute(Named("Cultivation"), 0L, Long.MAX_VALUE) }
    
    /**
     * 最大修为
     */
    val maxCultivation: Entity by lazy { attributeService.attribute(Named("MaxCultivation"), 0L, Long.MAX_VALUE) }
    
    /**
     * 修炼境界
     */
    val realm: Entity by lazy { attributeService.attribute(Named("Realm"), 1L, Long.MAX_VALUE) }

    /**
     * 寿命
     */
    val lifespan: Entity by lazy { attributeService.attribute(Named("Lifespan"), 1L, Long.MAX_VALUE) }
}