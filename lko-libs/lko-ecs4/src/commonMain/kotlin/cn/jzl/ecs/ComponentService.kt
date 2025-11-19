package cn.jzl.ecs

import cn.jzl.datastructure.BitSet
import cn.jzl.di.instance
import kotlin.reflect.KClassifier

class ComponentService(override val world: World) : ComponentProvider {

    private val componentIdEntities = mutableMapOf<KClassifier, ComponentId>()
    private val componentBits = BitSet()

    @PublishedApi
    internal val entityTags = BitSet()

    @PublishedApi
    internal val components: Components by world.di.instance<Components>()

    inline fun <reified C> component(): Relation = Relation(id<C>(), components.componentId)

    override fun holdsData(relation: Relation): Boolean = relation.kind.id !in entityTags

    override fun isComponent(entity: ComponentId): Boolean = entity.id in componentBits

    override fun getOrRegisterEntityForClass(classifier: KClassifier): ComponentId {
        return componentIdEntities.getOrPut(classifier) {
            val entity = world.entityService.create()
            componentBits.set(entity.id)
            entity
        }
    }
}