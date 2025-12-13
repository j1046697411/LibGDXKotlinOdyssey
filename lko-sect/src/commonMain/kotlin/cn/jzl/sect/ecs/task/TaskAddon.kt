package cn.jzl.sect.ecs.task

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.sect.ecs.Countdown
import cn.jzl.sect.ecs.countdownAddon
import cn.jzl.sect.ecs.inventoryAddon
import cn.jzl.sect.ecs.sectAddon

/**
 * 任务系统 Addon
 * 提供宗门任务的创建、领取、完成等功能
 */
val taskAddon = createAddon("task") {
    install(sectAddon)
    install(inventoryAddon)
    install(countdownAddon)

    injects {
        this bind singleton { new(::TaskService) }
    }

    components {
        world.componentId<SectTask> { it.tag() }
        world.componentId<TaskTypeComponent>()
        world.componentId<TaskRequirement>()
        world.componentId<TaskRewardConfig>()
        world.componentId<TaskLimit>()
        world.componentId<TaskProgress>()
    }
}


