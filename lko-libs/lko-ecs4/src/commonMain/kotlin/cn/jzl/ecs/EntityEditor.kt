package cn.jzl.ecs

interface EntityEditor {

    fun addRelation(entity: Entity, relation: Relation, data: Any)

    fun addRelation(entity: Entity, relation: Relation)

    fun removeRelation(entity: Entity, relation: Relation)
}