package cn.jzl.ecs

import cn.jzl.datastructure.list.SortSet
import kotlin.jvm.JvmInline

@PublishedApi
internal data class BatchEntityEditor(internal var entity: Entity) : EntityEditor {

    private val operates = SortSet<EntityRelationOperate> { a, b -> a.relation.data.compareTo(b.relation.data) }

    override fun addRelation(entity: Entity, relation: Relation, data: Any) {
        check(entity == this.entity) { "entity must be $entity" }
        operates.add(AddEntityRelationOperateWithData(relation, data))
    }

    override fun addRelation(entity: Entity, relation: Relation) {
        check(entity == this.entity) { "entity must be $entity" }
        operates.add(AddEntityRelationOperate(relation))
    }

    override fun removeRelation(entity: Entity, relation: Relation) {
        check(entity == this.entity) { "entity must be $entity" }
        operates.add(RemoveEntityRelationOperate(relation))
    }

    fun apply(world: World) {
        if (operates.isEmpty()) return
        world.relationService.updateRelations(entity, operates)
        operates.clear()
    }

    sealed interface EntityRelationOperate {
        val relation: Relation
    }

    data class AddEntityRelationOperateWithData(override val relation: Relation, val data: Any) : EntityRelationOperate

    @JvmInline
    value class AddEntityRelationOperate(override val relation: Relation) : EntityRelationOperate

    @JvmInline
    value class RemoveEntityRelationOperate(override val relation: Relation) : EntityRelationOperate
}