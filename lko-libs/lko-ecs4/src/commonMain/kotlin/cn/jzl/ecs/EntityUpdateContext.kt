package cn.jzl.ecs

class EntityUpdateContext(world: World, entityEditor: EntityEditor) : EntityCreateContext(world, entityEditor) {

    inline fun <reified C : Any> Entity.removeComponent() {
        entityEditor.removeRelation(this, component<C>())
    }

    inline fun <reified K : Any, reified T> Entity.removeRelation() {
        entityEditor.removeRelation(this, relation<K, T>())
    }

    inline fun <reified K : Any> Entity.removeRelation(target: Entity) {
        entityEditor.removeRelation(this, relation<K>(target))
    }
}