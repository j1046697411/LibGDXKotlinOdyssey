package cn.jzl.sect.ecs.attribute

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.World
import cn.jzl.sect.ecs.core.Named

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