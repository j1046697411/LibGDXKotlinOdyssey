package cn.jzl.sect.ecs.building

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.sect.ecs.TrainingHall
import cn.jzl.sect.ecs.TreasureVault
import cn.jzl.sect.ecs.levelingAddon
import cn.jzl.sect.ecs.moneyAddon
import cn.jzl.sect.ecs.sectAddon

/**
 * 建筑系统 Addon
 * 提供宗门建筑的创建、升级等功能
 */
val buildingAddon = createAddon("building") {
    install(sectAddon)
    install(levelingAddon)
    install(moneyAddon)

    injects {
        this bind singleton { new(::BuildingService) }
    }

    components {
        world.componentId<TrainingHall> { it.tag() }
        world.componentId<TreasureVault> { it.tag() }
        world.componentId<BuildingTypeComponent>()
        world.componentId<BuildingEfficiency>()
        world.componentId<BuildingCapacity>()
        world.componentId<BuildingBaseCost>()
    }
}

