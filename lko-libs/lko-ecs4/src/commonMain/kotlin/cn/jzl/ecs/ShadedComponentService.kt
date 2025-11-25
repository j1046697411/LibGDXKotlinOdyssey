package cn.jzl.ecs

class ShadedComponentService(val world: World) {

    private val shadedComponents = mutableMapOf<Entity, Any>()

    operator fun get(relation: Relation): Any? = shadedComponents[relation.kind]

    operator fun set(relation: Relation, shadedComponent: Any) {
        shadedComponents[relation.kind] = shadedComponent
    }
}