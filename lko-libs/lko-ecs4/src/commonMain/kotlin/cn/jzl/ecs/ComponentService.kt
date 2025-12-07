package cn.jzl.ecs

import cn.jzl.datastructure.BitSet
import kotlin.reflect.KClassifier

class ComponentService(override val world: World) : ComponentProvider {

    private val componentIdEntities = mutableMapOf<KClassifier, ComponentId>()

    @PublishedApi
    internal val entityTags = BitSet()

    @PublishedApi
    internal val components: Components by lazy { Components(this) }

    @PublishedApi
    internal val singleRelationBits = BitSet()

    inline fun <reified C> component(): Relation = Relation(id<C>(), components.componentId)

    override fun holdsData(relation: Relation): Boolean = relation.kind.id !in entityTags

    fun isSingleRelation(relation: Relation): Boolean = relation.kind.id in singleRelationBits

    fun isShadedComponent(relation: Relation): Boolean = relation.target == components.sharedId

    override fun getOrRegisterEntityForClass(classifier: KClassifier): ComponentId {
        return componentIdEntities.getOrPut(classifier) { world.entityService.create(false) }
    }
}