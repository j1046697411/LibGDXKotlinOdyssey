package cn.jzl.sect.ecs.technique

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.sect.ecs.sectAddon

/**
 * 功法系统 Addon
 * 提供功法的创建、学习、管理等功能
 */
val techniqueAddon = createAddon("technique") {
    install(sectAddon)

    injects {
        this bind singleton { new(::TechniqueService) }
    }

    components {
        world.componentId<Technique> { it.tag() }
        world.componentId<TechniqueGradeComponent>()
        world.componentId<TechniqueTypeComponent>()
        world.componentId<TechniqueRequirement>()
        world.componentId<TechniqueEffect>()
        world.componentId<TechniqueLearned>()
        world.componentId<LibraryContains>()
    }
}

