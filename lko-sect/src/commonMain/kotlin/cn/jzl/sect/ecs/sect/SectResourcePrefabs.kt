package cn.jzl.sect.ecs.sect

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.sect.ecs.core.Description
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.item.ItemService
import cn.jzl.sect.ecs.item.Stackable

/**
 * 宗门资源预制体
 * 定义宗门系统使用的各种资源物品
 */
object SectResourcePrefabs {
    // 货币
    const val SPIRIT_STONE = "灵石"

    // 灵草类
    const val LOW_SPIRIT_HERB = "低级灵草"
    const val MID_SPIRIT_HERB = "中级灵草"
    const val HIGH_SPIRIT_HERB = "高级灵草"

    // 丹药类
    const val QI_NOURISHING_PILL = "养气丹"
    const val FOUNDATION_PILL = "筑基丹"

    // 材料类
    const val REFINED_IRON = "精铁"
    const val SPIRIT_JADE = "灵玉"

    /**
     * 注册所有宗门资源预制体
     */
    fun registerPrefabs(world: World) {
        val itemService by world.di.instance<ItemService>()

        // 灵草类
        itemService.itemPrefab(Named(LOW_SPIRIT_HERB)) {
            it.addComponent(Description("蕴含微弱灵气的草药，炼制低级丹药的基础材料"))
            it.addTag<Stackable>()
        }

        itemService.itemPrefab(Named(MID_SPIRIT_HERB)) {
            it.addComponent(Description("灵气充裕的草药，可用于炼制中级丹药"))
            it.addTag<Stackable>()
        }

        itemService.itemPrefab(Named(HIGH_SPIRIT_HERB)) {
            it.addComponent(Description("灵气浓郁的珍稀草药，高级丹药必备材料"))
            it.addTag<Stackable>()
        }

        // 丹药类
        itemService.itemPrefab(Named(QI_NOURISHING_PILL)) {
            it.addComponent(Description("温养真气的入门丹药，适合练气期弟子服用"))
            it.addTag<Stackable>()
        }

        itemService.itemPrefab(Named(FOUNDATION_PILL)) {
            it.addComponent(Description("突破筑基期的关键丹药，千金难求"))
            it.addTag<Stackable>()
        }

        // 材料类
        itemService.itemPrefab(Named(REFINED_IRON)) {
            it.addComponent(Description("经过精炼的铁材，用于锻造法器"))
            it.addTag<Stackable>()
        }

        itemService.itemPrefab(Named(SPIRIT_JADE)) {
            it.addComponent(Description("蕴含灵气的玉石，用于阵法布置"))
            it.addTag<Stackable>()
        }
    }
}

