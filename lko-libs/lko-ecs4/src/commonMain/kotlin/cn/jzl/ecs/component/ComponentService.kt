package cn.jzl.ecs.component

import cn.jzl.di.instance
import cn.jzl.ecs.Relation
import cn.jzl.ecs.World
import cn.jzl.ecs.entity.Entity
import cn.jzl.ecs.entity.EntityCreateContext
import kotlin.jvm.JvmInline
import kotlin.reflect.KClassifier

class ComponentService(val world: World) : ComponentProvider {

    private val componentIdMap = mutableMapOf<KClassifier, Entity>()

    val components: Components by world.di.instance<Components>()

    @PublishedApi
    internal val tagRelation: Relation by lazy { Relation(components.tag, components.component) }

    inline fun <reified C> component(): Relation = Relation(id<C>(), components.component)

    override fun getOrRegisterComponentIdForClass(classifier: KClassifier): ComponentId {
        return getOrRegisterEntityForClass(classifier).entityId
    }

    @PublishedApi
    internal fun getOrRegisterEntityForClass(classifier: KClassifier): Entity {
        return componentIdMap.getOrPut(classifier) { world.entityStore.create() }
    }

    inline fun <reified T : Any> configure(configuration: ComponentConfigureContext.(Entity) -> Unit): Entity {
        return world.entityService.create {
            val componentConfigureContext = ComponentConfigureContext(this)
            componentConfigureContext.configuration(it)
        }
    }
}

@JvmInline
value class ComponentConfigureContext(val entityCreateContext: EntityCreateContext) {

    fun Entity.tag(): Unit = with(entityCreateContext) {
        addRelation(entityId, componentService.tagRelation)
    }
}