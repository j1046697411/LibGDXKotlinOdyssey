package cn.jzl.ecs.entity

import cn.jzl.ecs.EntityId
import cn.jzl.ecs.Relation
import cn.jzl.ecs.World

open class EntityCreateContext(
    world: World,
    @PublishedApi internal val entityEditor: EntityEditor
) : EntityRelationContext(world) {

    inline fun <reified C : Any> Entity.addComponent(data: C) {
        entityEditor.addRelation(entityId, component<C>(), data)
    }

    inline fun <reified T> Entity.addTag() = entityEditor.addRelation(entityId, component<T>())

    inline fun <reified K : Any, reified T> Entity.addRelation(data: K) {
        entityEditor.addRelation(entityId, relation<K, T>(), data)
    }

    inline fun <reified K : Any> Entity.addRelation(target: EntityId, data: K) {
        entityEditor.addRelation(entityId, relation<K>(target), data)
    }

    inline fun <reified K : Any, reified T> Entity.addRelation() {
        entityEditor.addRelation(entityId, relation<K, T>())
    }

    inline fun <reified K : Any> Entity.addRelation(target: EntityId) {
        entityEditor.addRelation(entityId, relation<K>(target))
    }
}