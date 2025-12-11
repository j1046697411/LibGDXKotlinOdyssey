package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.*
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.core.coreAddon
import cn.jzl.sect.ecs.item.itemAddon

sealed class Formula
interface FormulaChecker {
    fun check(provider: Entity, receiver: Entity, formula: Entity): Boolean
}

@JvmInline
value class Material(val count: Int)

@JvmInline
value class Product(val count: Int)

// TODO 需要实现检查生产者是否有能力生产。
val formulaAddon = createAddon("formula") {
    install(coreAddon)
    install(itemAddon)
    install(inventoryAddon)
    injects { this bind singleton { new(::FormulaService) } }
    components {
        world.componentId<Formula> { it.tag() }
        world.componentId<Material>()
        world.componentId<Product>()
        world.componentId<FormulaChecker>()
    }
}

class FormulaService(world: World) : EntityRelationContext(world) {

    private val inventoryService by world.di.instance<InventoryService>()

    private val materials = world.query { EntityMaterialContext(this) }.groupedBy { formula }
    private val products = world.query { EntityProductContext(this) }.groupedBy { formula }

    fun executeFormula(provider: Entity, receiver: Entity, formula: Entity) {
        require(formula.hasTag<Formula>())
        val formulaChecker = formula.getComponent<FormulaChecker>()
        require(formulaChecker.check(provider, receiver, formula)) {
            "配方${formula.id}验证失败：提供者${provider.id}或接收者${receiver.id}不满足配方要求"
        }
        val materialGroup = materials[formula]
        val productGroup = products[formula]
        require(materialGroup != null) { "配方${formula.id}没有材料" }
        require(productGroup != null) { "配方${formula.id}没有产品" }
        // 检查用户是否有足够的材料
        require(materialGroup.all { inventoryService.hasEnoughItems(provider, itemPrefab, material.count) }) {
            "提供者${provider.id}材料不足，无法执行配方${formula.id}"
        }
        // 消耗材料
        materialGroup.forEach {
            inventoryService.removeItem(provider, itemPrefab, material.count)
        }
        // 添加产品
        productGroup.forEach {
            inventoryService.addItem(receiver, itemPrefab, product.count)
        }
    }

    @ECSDsl
    fun createFormula(named: Named, formulaChecker: FormulaChecker, block: FormulaContext.() -> Unit): Entity {
        val materials = mutableMapOf<Entity, Int>()
        val products = mutableMapOf<Entity, Int>()
        val formulaContext = object : FormulaContext {
            override fun material(itemPrefab: Entity, count: Int) {
                require(count >= 1) { "材料数量必须大于0，当前值: $count" }
                require(itemPrefab !in materials) { "材料物品预制体${itemPrefab.id}已存在于配方中，不能重复添加" }
                materials[itemPrefab] = count
            }

            override fun product(itemPrefab: Entity, count: Int) {
                require(count >= 1) { "产品数量必须大于0，当前值: $count" }
                require(itemPrefab !in products) { "产品物品预制体${itemPrefab.id}已存在于配方中，不能重复添加" }
                products[itemPrefab] = count
            }
        }
        formulaContext.block()
        require(materials.isNotEmpty()) { "配方必须至少包含一种材料" }
        require(products.isNotEmpty()) { "配方必须至少包含一种产品" }
        val formula = world.entity {
            it.addTag<Formula>()
            it.addComponent(named)
            it.addComponent(formulaChecker)
        }
        materials.forEach { (itemPrefab, count) ->
            world.childOf(formula) {
                it.addComponent(Material(count))
                it.addRelation<OwnedBy>(itemPrefab)
            }
        }
        products.forEach { (itemPrefab, count) ->
            world.childOf(formula) {
                it.addComponent(Product(count))
                it.addRelation<OwnedBy>(itemPrefab)
            }
        }
        return formula
    }

    interface FormulaContext {
        fun material(itemPrefab: Entity, count: Int)
        fun product(itemPrefab: Entity, count: Int)
    }

    class EntityMaterialContext(world: World) : EntityQueryContext(world) {
        val material by component<Material>()
        val formula by relationUp(components.childOf)
        val itemPrefab by relationUp<OwnedBy>()
    }

    class EntityProductContext(world: World) : EntityQueryContext(world) {
        val product by component<Product>()
        val formula by relationUp(components.childOf)
        val itemPrefab by relationUp<OwnedBy>()
    }
}
