package cn.jzl.ecs.entity

import cn.jzl.ecs.EntityId
import cn.jzl.ecs.Relation
import cn.jzl.ecs.World

interface EntityEditor {

    fun addRelation(entityId: EntityId, relation: Relation, data: Any)

    fun addRelation(entityId: EntityId, relation: Relation)

    fun removeRelation(entityId: EntityId, relation: Relation)
}

class EntityUpdateContext(world: World, entityEditor: EntityEditor) : EntityCreateContext(world, entityEditor) {

    inline fun <reified C : Any> Entity.removeComponent() {
        entityEditor.removeRelation(entityId, component<C>())
    }

    inline fun <reified K : Any, reified T> Entity.removeRelation() {
        entityEditor.removeRelation(entityId, relation<K, T>())
    }

    inline fun <reified K : Any> Entity.removeRelation(target: EntityId) {
        entityEditor.removeRelation(entityId, relation<K>(target))
    }
}