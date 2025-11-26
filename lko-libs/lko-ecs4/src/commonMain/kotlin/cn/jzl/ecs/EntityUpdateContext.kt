package cn.jzl.ecs

class EntityUpdateContext(world: World, entityEditor: EntityEditor) : EntityCreateContext(world, entityEditor) {

    inline fun <reified C> Entity.removeComponent() {
        entityEditor.removeRelation(this, component<C>())
    }

    inline fun <reified C> Entity.removeSharedComponent() {
        entityEditor.removeRelation(this, sharedComponent<C>())
    }

    inline fun <reified K> Entity.removeTag() {
        entityEditor.removeRelation(this, component<K>())
    }

    inline fun <reified K, reified T> Entity.removeRelation() {
        entityEditor.removeRelation(this, relation<K, T>())
    }

    inline fun <reified K> Entity.removeRelation(target: Entity) {
        entityEditor.removeRelation(this, relation<K>(target))
    }
}