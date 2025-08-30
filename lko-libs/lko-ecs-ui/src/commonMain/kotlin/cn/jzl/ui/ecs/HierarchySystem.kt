package cn.jzl.ui.ecs

import cn.jzl.ecs.*
import cn.jzl.ui.node.ComposeUiNodeSystem

class HierarchySystem(world: World) : System(world) {

    val rootEntities: Family = world.family { it.all(ComposeUiNodeSystem.ComposeUiRootNode) }

    fun createRoot(configuration: EntityCreateContext.(Entity)-> Unit): Entity = world.create {
        it += RootNode
        configuration(it)
    }

    fun addHierarchy(parent: Entity, child: Entity) {
        val hierarchy = child.getOrNull(Hierarchy)
        if (hierarchy != null) {
            updateHierarchy(parent, child)
        } else {
            world.configure(child) { it += Hierarchy(parent) }
            world.configure(parent) { it.getOrPut(Children) { Children() }.addChild(child) }
        }
    }

    fun updateHierarchy(parent: Entity, child: Entity) {
        val hierarchy = child.getOrNull(Hierarchy)
        if (hierarchy == null) return addHierarchy(parent, child)
        if (hierarchy.parent == parent) return
        world.configure(hierarchy.parent) { it.getOrNull(Children)?.remove(child) }
        world.configure(child) { it += Hierarchy(parent) }
        world.configure(child) { it.getOrPut(Children) { Children() }.addChild(child) }
    }

    fun removeHierarchy(child: Entity) {
        val hierarchy = child.getOrNull(Hierarchy) ?: return
        world.configure(hierarchy.parent) { it.getOrNull(Children)?.remove(child) }
        world.configure(child) { it -= Hierarchy }
    }

    fun getParent(child: Entity): Entity? {
        return child.getOrNull(Hierarchy)?.parent
    }

    fun getChildren(parent: Entity): Sequence<Entity> {
        return parent.getOrNull(Children) ?: emptySequence()
    }

    fun move(entity: Entity, from: Int, to: Int, count: Int) {
        entity.getOrNull(Children)?.move(from, to, count)
    }

    fun insert(parent: Entity, index: Int, child: Entity) {
        val hierarchy = child.getOrNull(Hierarchy)
        if (hierarchy != null) {
            world.configure(hierarchy.parent) { it.getOrNull(Children)?.remove(child) }
        }
        world.configure(child) { it += Hierarchy(parent) }
        world.configure(parent) { it.getOrPut(Children) { Children() }.addChild(index, child) }
    }

    fun remove(entity: Entity, from: Int, count: Int) {
        val children = entity.getOrNull(Children) ?: return
        for (index in from until from + count) {
            world.delete(children[index])
        }
        children.remove(from, count)
    }

    fun clear(entity: Entity) {
        world.delete(entity)
        val children = entity.getOrNull(Children) ?: return
        for (child in children) {
            clear(child)
        }
        children.clear()
    }
}