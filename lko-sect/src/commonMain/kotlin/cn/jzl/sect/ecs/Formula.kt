package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.entity
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.associatedBy
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.coreAddon
import cn.jzl.sect.ecs.item.itemAddon

sealed class Formula

@JvmInline
value class FormulaMaterial(val count: Int)

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
        world.componentId<FormulaMaterial>()
        world.componentId<Product>()
    }
}

class FormulaService(world: World) : EntityRelationContext(world) {

    private val inventoryService by world.di.instance<InventoryService>()
    private val formulas = world.query { EntityFormulaContext(this) }.associatedBy { named }

    fun createFormula(named: Named, block: FormulaContext.() -> Unit): Entity {
        // 检查重复名称
        require(named !in formulas) { "配方${named.name}已存在" }

        val materialList = mutableListOf<Pair<Entity, Int>>()
        val productList = mutableListOf<Pair<Entity, Int>>()

        val entityFormulaContext = object : FormulaContext {
            override fun material(itemPrefab: Entity, count: Int) {
                require(count > 0) { "材料数量必须大于0" }
                materialList.add(itemPrefab to count)
            }

            override fun product(itemPrefab: Entity, count: Int) {
                require(count > 0) { "产品数量必须大于0" }
                productList.add(itemPrefab to count)
            }
        }
        entityFormulaContext.block()

        // 验证配方至少有一个材料和至少有一个产品
        require(materialList.isNotEmpty()) { "配方${named.name}必须至少有一个材料" }
        require(productList.isNotEmpty()) { "配方${named.name}必须至少有一个产品" }

        return world.entity {
            // 添加材料和产品关系
            materialList.forEach { (itemPrefab, count) ->
                it.addRelation(itemPrefab, FormulaMaterial(count))
            }
            productList.forEach { (itemPrefab, count) ->
                it.addRelation(itemPrefab, Product(count))
            }
            it.addTag<Formula>()
            it.addComponent(named)
        }
    }

    /**
     * 执行配方，消耗玩家的材料并生成产品
     * @param player 玩家实体
     * @param formulaEntity 配方实体
     */
    fun executeFormula(player: Entity, formulaEntity: Entity) {
        // 验证配方实体
        require(formulaEntity.hasTag<Formula>()) { "实体${formulaEntity.id}不是有效的配方" }

        // 获取材料需求（通过 FormulaMaterial 关系）
        val materialRequirements = formulaEntity.getRelationsWithData<FormulaMaterial>().toList()
        require(materialRequirements.isNotEmpty()) { "配方${formulaEntity.id}没有材料需求" }

        // 使用 InventoryService 验证材料是否足够
        materialRequirements.forEach { (relation, requirement) ->
            val itemPrefab = relation.target
            require(inventoryService.hasEnoughItems(player, itemPrefab, requirement.count)) {
                "玩家${player.id}材料不足，需要: ${requirement.count}, 拥有: ${inventoryService.getItemCount(player, itemPrefab)}"
            }
        }

        // 使用 InventoryService 批量消耗材料
        val materialsToConsume = materialRequirements.associate { it.relation.target to it.data.count }
        inventoryService.consumeItems(player, materialsToConsume)

        // 获取产品列表（通过 Product 关系）
        val products = formulaEntity.getRelationsWithData<Product>().toList()
        require(products.isNotEmpty()) { "配方${formulaEntity.id}没有产品定义" }

        // 使用 InventoryService 添加所有产品
        products.forEach { (relation, product) ->
            val itemPrefab = relation.target
            inventoryService.addItem(player, itemPrefab, product.count)
        }
    }

    fun getFormula(named: Named): Entity? = formulas[named]

    fun getAllFormulas(): Sequence<Entity> = formulas.entities

    interface FormulaContext {
        fun material(itemPrefab: Entity, count: Int)
        fun product(itemPrefab: Entity, count: Int)
    }

    class EntityFormulaContext(world: World) : EntityQueryContext(world) {
        val named by component<Named>()
        val materials get() = getRelations<FormulaMaterial>()
        val products get() = getRelations<Product>()

        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<Formula>()
        }
    }
}