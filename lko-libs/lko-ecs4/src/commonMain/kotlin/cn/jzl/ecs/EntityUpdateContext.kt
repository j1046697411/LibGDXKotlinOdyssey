package cn.jzl.ecs

class EntityUpdateContext(world: World, entityEditor: EntityEditor) : EntityCreateContext(world, entityEditor) {

    inline fun <reified C> Entity.removeComponent() {
        entityEditor.removeRelation(this, relations.component<C>())
    }

    inline fun <reified C> Entity.removeSharedComponent() {
        entityEditor.removeRelation(this, relations.sharedComponent<C>())
    }

    inline fun <reified K> Entity.removeTag() {
        entityEditor.removeRelation(this, relations.component<K>())
    }

    inline fun <reified K, reified T> Entity.removeRelation() {
        entityEditor.removeRelation(this, relations.relation<K, T>())
    }

    inline fun <reified K> Entity.removeRelation(target: Entity) {
        entityEditor.removeRelation(this, relations.relation<K>(target))
    }
}