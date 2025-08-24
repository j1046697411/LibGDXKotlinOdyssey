package cn.jzl.ui.node

import cn.jzl.ecs.Component
import cn.jzl.ecs.ComponentType
import cn.jzl.ui.modifier.Merge
import cn.jzl.ui.modifier.Modifier
import kotlin.reflect.KClass

internal class ModifierComponent(modifier: Modifier) : Component<ModifierComponent> {
    private val elements = mutableMapOf<KClass<*>, Modifier.Element>()
    private val modifierNodeElements = mutableListOf<ModifierNodeElement<ModifierNode>>()

    val modifierNodes: Sequence<ModifierNodeElement<ModifierNode>> = modifierNodeElements.asSequence()

    var modifier: Modifier = modifier
        set(value) {
            if (value != field) {
                update(value)
            }
            field = value
        }

    @Suppress("UNCHECKED_CAST")
    operator fun <E : Modifier.Element> get(type: KClass<E>): E? {
        return elements[type] as E?
    }

    private fun update(modifier: Modifier) {
        elements.clear()
        modifierNodeElements.clear()
        modifier.foldIn(elements) { elements, element ->
            val oldElement = elements[element::class]
            val newElement = if (oldElement != null && element is Merge<*>) {
                element.unsafeMergeWith(oldElement)
            } else {
                element
            }
            if (newElement is ModifierNodeElement<out ModifierNode>) {
                modifierNodeElements.add(newElement as ModifierNodeElement<ModifierNode>)
            }
            elements[element::class] = newElement
            elements
        }
    }

    override val type: ComponentType<ModifierComponent> get() = ModifierComponent

    companion object : ComponentType<ModifierComponent>()
}