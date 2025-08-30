package cn.jzl.ui.ecs

import cn.jzl.ecs.Component
import cn.jzl.ecs.ComponentType
import cn.jzl.ecs.Entity

internal class Children : Component<Children>, Sequence<Entity> {

    val children: MutableList<Entity> = mutableListOf()

    operator fun get(index: Int): Entity = children[index]

    fun addChild(child: Entity) {
        children.add(child)
    }

    fun addChild(index: Int, child: Entity) {
        children.add(index, child)
    }

    fun remove(child: Entity) {
        children.remove(child)
    }

    fun remove(index: Int, count: Int) {
        if (count == 1) {
            children.removeAt(index)
        } else {
            children.subList(index, index + count).clear()
        }
    }

    fun move(from: Int, to: Int, count: Int) {
        val dest = if (from > to) to else to - count
        if (count == 1) {
            if (from == to + 1 || from == to - 1) {
                // Adjacent elements, perform swap to avoid backing array manipulations.
                val fromEl = children[from]
                val toEl = children.set(to, fromEl)
                children[from] = toEl
            } else {
                val fromEl = children.removeAt(from)
                children.add(dest, fromEl)
            }
        } else {
            val subView = children.subList(from, from + count)
            val subCopy = subView.toMutableList()
            subView.clear()
            children.addAll(dest, subCopy)
        }
    }

    fun clear() {
        children.clear()
    }

    override fun iterator(): Iterator<Entity> = children.iterator()

    override val type: ComponentType<Children> get() = Children

    companion object : ComponentType<Children>()
}