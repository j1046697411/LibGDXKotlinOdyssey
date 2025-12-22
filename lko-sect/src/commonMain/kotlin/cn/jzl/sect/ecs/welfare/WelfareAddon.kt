package cn.jzl.sect.ecs.welfare

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.sect.ecs.inventoryAddon
import cn.jzl.sect.ecs.sectAddon

/**
 * 福利系统 Addon
 * 提供福利规则管理和领取功能
 */
val welfareAddon = createAddon("welfare") {
    install(sectAddon)
    install(inventoryAddon)

    injects {
        this bind singleton { new(::WelfareService) }
    }

    components {
        world.componentId<WelfareRule> { it.tag() }
        world.componentId<WelfareConfigComponent>()
        world.componentId<WelfareRewardComponent>()
        world.componentId<WelfareStatus>()
    }
}

