package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.QueryStream
import cn.jzl.ecs.query.query

/**
 * <p>粮食 (FOOD)	🌾	宗门生存基础	弟子饮食、杂役俸禄	农田产出、区域占领	每位弟子10单位/月</p>
 * <p>布料 (CLOTH)	👕	衣物和日常用品	弟子衣物、宗门装饰	纺织坊产出、交易	每位弟子5单位/月</p>
 * <p>木材 (WOOD)	🌲	建筑和工具材料	设施建设、家具制造	伐木场产出、森林区域	设施维护5-20单位/月</p>
 * <p>矿石 (STONE)	⛏️	建筑和武器材料	设施建设、武器打造	矿场产出、矿脉区域	设施升级50-200单位/次</p>
 * <p>灵石 (SPIRIT_STONE)	💎	修炼和高级交易货币	修炼加速、高级丹药、外部交易	灵脉区域、试炼奖励	长老100/月，核心弟子50/月</p>
 * <p>草药 (MEDICINE_HERB)	🌿	炼丹基础材料	炼制基础丹药、治疗	药田产出、采集	炼丹房消耗20-100单位/次</p>
 *
 * <p>凡俗百姓	👨‍🌾	普通凡人百姓	提供劳动力、基础资源	粮食、布料、基础劳动力	粮食、衣物、住所</p>
 * <p>宗门杂役	👷	凡人中的杂役人员	宗门日常维护、基础建设	维护宗门设施、基础工作	粮食、少量灵石、住所</p>
 * <p>外门弟子	🧑‍🎓	刚入门的修真者	基础任务执行、资源采集	基础资源、少量贡献点	粮食、布料、修炼指导</p>
 *
 */
val resourcesAddon = createAddon("resources") {
    install(itemAddon)
    injects { this bind singleton { new(::Resources) } }
}

sealed class Resource

class Resources(world: World) : EntityRelationContext(world) {

    private val itemService by world.di.instance<ItemService>()

    // 粮食
    val foodPrefab: Entity by lazy { createResourcePrefab(Named("food")) }

    // 布料
    val clothPrefab: Entity by lazy { createResourcePrefab(Named("cloth")) }

    // 木材
    val woodPrefab: Entity by lazy { createResourcePrefab(Named("wood")) }

    // 矿石
    val stonePrefab: Entity by lazy { createResourcePrefab(Named("stone")) }

    // 灵石
    val spiritStonePrefab: Entity by lazy { createResourcePrefab(Named("spirit_stone")) }

    // 草药
    val medicineHerbPrefab: Entity by lazy { createResourcePrefab(Named("medicine_herb")) }

    // 凡俗百姓
    val ordinaryPeoplePrefab: Entity by lazy { createResourcePrefab(Named("ordinary_people")) }

    // 宗门杂役
    val sectServantsPrefab: Entity by lazy { createResourcePrefab(Named("sect_servants")) }

    // 外门弟子
    val outerDisciplesPrefab: Entity by lazy { createResourcePrefab(Named("outer_disciples")) }

    // 宗门贡献点
    val sectContributionPointsPrefab: Entity by lazy { createResourcePrefab(Named("sect_contribution_points")) }

    private fun createResourcePrefab(
        named: Named,
        block: Entity.() -> Unit = {}
    ): Entity = itemService.itemPrefab(named) {
        it.addTag<Stackable>()
        it.addTag<Resource>()
        block(it)
    }
}
