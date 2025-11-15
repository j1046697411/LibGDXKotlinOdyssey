package cn.jzl.ecs

import cn.jzl.di.*
import cn.jzl.ecs.component.ComponentService
import cn.jzl.ecs.component.Components
import cn.jzl.ecs.entity.EntityService
import cn.jzl.ecs.entity.EntityStoreImpl

fun world(configuration: DIMainBuilder.() -> Unit): World {
    val di = DI {
        this bind singleton { World(this.di) }

        this bind singleton { new(::EntityService) }
        this bind singleton { new(::EntityStoreImpl) }

        this bind singleton { new(::ArchetypeService) }
        this bind singleton { new(::ComponentService) }
        this bind singleton { new(::Components) }

        this bind singleton { new(::RelationService) }

        configuration()
    }
    val world by di.instance<World>()

    return world
}
