/**
 * 资源系统模块，负责管理宗门的各种资源类型和资源实体。
 *
 * 资源包括基础物质资源（粮食、布料、木材等）和人力/人口资源（凡俗百姓、宗门杂役、外门弟子）。
 * 资源系统与物品系统深度集成，所有资源都是可堆叠的物品。
 */
package cn.jzl.sect.ecs.resources

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.item.ItemService
import cn.jzl.sect.ecs.item.Stackable
import cn.jzl.sect.ecs.item.itemAddon

/**
 * 资源类型说明：
 * <p>粮食 (FOOD) 🌾 宗门生存基础 - 弟子饮食、杂役俸禄 - 农田产出、区域占领 - 每位弟子10单位/月</p>
 * <p>布料 (CLOTH) 👕 衣物和日常用品 - 弟子衣物、宗门装饰 - 纺织坊产出、交易 - 每位弟子5单位/月</p>
 * <p>木材 (WOOD) 🌲 建筑和工具材料 - 设施建设、家具制造 - 伐木场产出、森林区域 - 设施维护5-20单位/月</p>
 * <p>矿石 (STONE) ⛏️ 建筑和武器材料 - 设施建设、武器打造 - 矿场产出、矿脉区域 - 设施升级50-200单位/次</p>
 * <p>灵石 (SPIRIT_STONE) 💎 修炼和高级交易货币 - 修炼加速、高级丹药、外部交易 - 灵脉区域、试炼奖励 - 长老100/月，核心弟子50/月</p>
 * <p>草药 (MEDICINE_HERB) 🌿 炼丹基础材料 - 炼制基础丹药、治疗 - 药田产出、采集 - 炼丹房消耗20-100单位/次</p>
 *
 * <p>凡俗百姓 👨‍🌾 普通凡人百姓 - 提供劳动力、基础资源 - 粮食、布料、基础劳动力 - 粮食、衣物、住所</p>
 * <p>宗门杂役 👷 凡人中的杂役人员 - 宗门日常维护、基础建设 - 维护宗门设施、基础工作 - 粮食、少量灵石、住所</p>
 * <p>外门弟子 🧑‍🎓 刚入门的修真者 - 基础任务执行、资源采集 - 基础资源、少量贡献点 - 粮食、布料、修炼指导</p>
 * <p>宗门贡献点 - 宗门内部货币 - 兑换资源、学习功法 - 任务奖励、贡献度兑换 - 用于宗门内部交易</p>
 */

/**
 * 资源系统插件，用于注册资源相关的组件、服务和依赖注入。
 *
 * 该插件负责：
 * - 安装物品系统插件
 * - 注册资源类为标签组件
 * - 注入Resources服务单例
 */
val resourcesAddon = createAddon("resources") {
    install(itemAddon)
    injects { this bind singleton { new(::Resources) } }
    entities {
        world.componentId<Resource> { it.tag() }
        world.componentId<ResourceIcon>()
    }
}

/**
 * 资源标签组件，用于标识实体为资源。
 *
 * 所有资源实体都必须添加此标签，以便资源系统进行管理和识别。
 */
sealed class Resource

@JvmInline
value class ResourceIcon(val icon: String)

/**
 * 资源服务类，负责创建和管理各种资源预制体。
 *
 * 该服务提供了宗门所需的各种资源预制体，包括物质资源和人力资源。
 * 所有资源都是通过物品系统创建的可堆叠物品。
 *
 * @property world ECS世界实例
 */
class Resources(world: World) : EntityRelationContext(world) {

    private val itemService by world.di.instance<ItemService>()

    /** 粮食资源预制体，用于宗门弟子饮食和杂役俸禄 */
    val foodPrefab: Entity by lazy {
        createResourcePrefab(Named("food")) {
            it.addComponent(ResourceIcon("🌾"))
        }
    }

    /** 布料资源预制体，用于弟子衣物和宗门装饰 */
    val clothPrefab: Entity by lazy {
        createResourcePrefab(Named("cloth")) {
            it.addComponent(ResourceIcon("👕"))
        }
    }

    /** 木材资源预制体，用于设施建设和家具制造 */
    val woodPrefab: Entity by lazy {
        createResourcePrefab(Named("wood")) {
            it.addComponent(ResourceIcon("🌲"))
        }
    }

    /** 矿石资源预制体，用于设施建设和武器打造 */
    val stonePrefab: Entity by lazy {
        createResourcePrefab(Named("stone")) {
            it.addComponent(ResourceIcon("⛏️"))
        }
    }

    /** 灵石资源预制体，用于修炼加速和高级交易 */
    val spiritStonePrefab: Entity by lazy {
        createResourcePrefab(Named("spirit_stone")) {
            it.addComponent(ResourceIcon("💎"))
        }
    }

    /** 草药资源预制体，用于炼制丹药和治疗 */
    val medicineHerbPrefab: Entity by lazy {
        createResourcePrefab(Named("medicine_herb")) {
            it.addComponent(ResourceIcon("🌿"))
        }
    }

    /** 凡俗百姓资源预制体，提供基础劳动力和资源 */
    val ordinaryPeoplePrefab: Entity by lazy {
        createResourcePrefab(Named("ordinary_people")) {
            it.addComponent(ResourceIcon("👨‍🌾"))
        }
    }

    /** 宗门杂役资源预制体，负责宗门日常维护和基础建设 */
    val sectServantsPrefab: Entity by lazy {
        createResourcePrefab(Named("sect_servants")) {
            it.addComponent(ResourceIcon("👷"))
        }
    }

    /** 外门弟子资源预制体，执行基础任务和资源采集 */
    val outerDisciplesPrefab: Entity by lazy {
        createResourcePrefab(Named("outer_disciples")) {
            it.addComponent(ResourceIcon("🧑‍"))
        }
    }

    /** 宗门贡献点资源预制体，宗门内部交易货币 */
    val sectContributionPointsPrefab: Entity by lazy { createResourcePrefab(Named("sect_contribution_points")) }

    /**
     * 创建资源预制体的内部方法。
     *
     * @param named 资源名称组件
     * @param block 可选的额外配置块，用于自定义资源属性
     * @return 创建的资源实体
     */
    private fun createResourcePrefab(
        named: Named,
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = itemService.itemPrefab(named) {
        it.addTag<Stackable>()
        it.addTag<Resource>()
        block(it)
    }
}
