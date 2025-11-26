package cn.jzl.ecs

open class EntityCreateContext(
    world: World,
    @PublishedApi internal val entityEditor: EntityEditor
) : EntityRelationContext(world) {

    inline fun <reified C : Any> Entity.addComponent(data: C) {
        entityEditor.addRelation(this, component<C>(), data)
    }

    inline fun <reified C : Any> Entity.addSharedComponent(data: C) {
        entityEditor.addRelation(this, sharedComponent<C>(), data)
    }

    inline fun <reified T> Entity.addSharedComponent() {
        entityEditor.addRelation(this, sharedComponent<T>())
    }

    inline fun <reified T> Entity.addTag(): Unit = entityEditor.addRelation(this, component<T>())

    @Suppress("NOTHING_TO_INLINE")
    inline fun Entity.addRelation(kind: ComponentId, target: Entity) {
        entityEditor.addRelation(this, Relation(kind, target))
    }

    inline fun <reified K : Any, reified T> Entity.addRelation(data: K) {
        entityEditor.addRelation(this, relation<K, T>(), data)
    }

    inline fun <reified K : Any> Entity.addRelation(target: Entity, data: K) {
        entityEditor.addRelation(this, relation<K>(target), data)
    }

    inline fun <reified K, reified T> Entity.addRelation() {
        entityEditor.addRelation(this, relation<K, T>())
    }

    inline fun <reified K> Entity.addRelation(target: Entity) {
        entityEditor.addRelation(this, relation<K>(target))
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun Entity.parentOf(parent: Entity) {
        entityEditor.addRelation(this, relation<Components.ChildOf>(parent))
    }
}