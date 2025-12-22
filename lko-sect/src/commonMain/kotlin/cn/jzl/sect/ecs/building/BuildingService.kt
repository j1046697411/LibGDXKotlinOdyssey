package cn.jzl.sect.ecs.building

import cn.jzl.di.instance
import cn.jzl.ecs.*
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.query
import cn.jzl.sect.ecs.*
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.sect.SectResourceService

/**
 * 建筑服务
 * 提供宗门建筑的创建、升级、查询等功能
 */
class BuildingService(world: World) : EntityRelationContext(world) {

    private val levelingService by world.di.instance<LevelingService>()
    private val sectResourceService by world.di.instance<SectResourceService>()
    private val attributeService by world.di.instance<AttributeService>()

    /**
     * 创建炼丹房
     */
    @ECSDsl
    fun createAlchemyHall(
        sect: Entity,
        named: Named,
        baseCost: BuildingBaseCost = BuildingBaseCost(),
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return world.entity {
            it.addTag<Building>()
            it.addTag<AlchemyHall>()
            it.addComponent(BuildingTypeComponent(BuildingType.ALCHEMY_HALL))
            it.addComponent(named)
            it.addComponent(baseCost)
            it.addComponent(BuildingEfficiency.DEFAULT)
            it.addComponent(BuildingCapacity.DEFAULT)
            it.addRelation<OwnedBy>(sect)
            levelingService.upgradeable(this, it)
            block(it)
        }
    }

    /**
     * 创建藏经阁
     */
    @ECSDsl
    fun createLibrary(
        sect: Entity,
        named: Named,
        baseCost: BuildingBaseCost = BuildingBaseCost(),
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return world.entity {
            it.addTag<Building>()
            it.addTag<Library>()
            it.addComponent(BuildingTypeComponent(BuildingType.LIBRARY))
            it.addComponent(named)
            it.addComponent(baseCost)
            it.addComponent(BuildingEfficiency.DEFAULT)
            it.addComponent(BuildingCapacity.DEFAULT)
            it.addRelation<OwnedBy>(sect)
            levelingService.upgradeable(this, it)
            block(it)
        }
    }

    /**
     * 创建练功房
     */
    @ECSDsl
    fun createTrainingHall(
        sect: Entity,
        named: Named,
        baseCost: BuildingBaseCost = BuildingBaseCost(),
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return world.entity {
            it.addTag<Building>()
            it.addTag<TrainingHall>()
            it.addComponent(BuildingTypeComponent(BuildingType.TRAINING_HALL))
            it.addComponent(named)
            it.addComponent(baseCost)
            it.addComponent(BuildingEfficiency.DEFAULT)
            it.addComponent(BuildingCapacity.DEFAULT)
            it.addRelation<OwnedBy>(sect)
            levelingService.upgradeable(this, it)
            block(it)
        }
    }

    /**
     * 创建藏宝阁
     */
    @ECSDsl
    fun createTreasureVault(
        sect: Entity,
        named: Named,
        baseCost: BuildingBaseCost = BuildingBaseCost(),
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return world.entity {
            it.addTag<Building>()
            it.addTag<TreasureVault>()
            it.addComponent(BuildingTypeComponent(BuildingType.TREASURE_VAULT))
            it.addComponent(named)
            it.addComponent(baseCost)
            it.addComponent(BuildingEfficiency.DEFAULT)
            it.addComponent(BuildingCapacity.DEFAULT)
            it.addRelation<OwnedBy>(sect)
            levelingService.upgradeable(this, it)
            block(it)
        }
    }

    /**
     * 通用建筑创建方法
     */
    @ECSDsl
    fun createBuilding(
        sect: Entity,
        type: BuildingType,
        named: Named,
        baseCost: BuildingBaseCost = BuildingBaseCost(),
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = when (type) {
        BuildingType.ALCHEMY_HALL -> createAlchemyHall(sect, named, baseCost, block)
        BuildingType.LIBRARY -> createLibrary(sect, named, baseCost, block)
        BuildingType.TRAINING_HALL -> createTrainingHall(sect, named, baseCost, block)
        BuildingType.TREASURE_VAULT -> createTreasureVault(sect, named, baseCost, block)
    }

    /**
     * 计算建筑升级消耗
     */
    fun calculateUpgradeCost(building: Entity): Pair<Int, Map<Entity, Int>> {
        require(building.hasTag<Building>()) { "实体${building.id}不是建筑" }
        val baseCost = building.getComponent<BuildingBaseCost?>() ?: BuildingBaseCost()
        val currentLevel = getBuildingLevel(building)
        val spiritStones = calculateUpgradeSpiritStoneCost(baseCost, currentLevel)
        val materials = calculateMaterialCost(baseCost, currentLevel)
        return spiritStones to materials
    }

    private fun calculateMaterialCost(baseCost: BuildingBaseCost, currentLevel: Long): Map<Entity, Int> {
        var factor = 1.0f
        repeat((currentLevel - 1).toInt()) { factor *= 1.5f }
        val result = mutableMapOf<Entity, Int>()
        baseCost.materials.forEach { (material, baseAmount) ->
            result[material] = (baseAmount * factor).toInt()
        }
        return result
    }

    /**
     * 检查是否可以升级建筑
     */
    fun canUpgrade(building: Entity, sect: Entity): BuildingUpgradeError? {
        require(building.hasTag<Building>()) { "实体${building.id}不是建筑" }
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }

        val currentLevel = getBuildingLevel(building)
        val maxLevel = 10L
        if (currentLevel >= maxLevel) {
            return BuildingUpgradeError.MaxLevelReached(currentLevel, maxLevel)
        }

        val requiredSectLevel = currentLevel + 1
        val sectLevel = getSectLevel(sect)
        if (sectLevel < requiredSectLevel) {
            return BuildingUpgradeError.SectLevelTooLow(requiredSectLevel, sectLevel)
        }

        val (spiritStones, materials) = calculateUpgradeCost(building)
        val sectMoney = sect.getComponent<Money?>()?.value ?: 0
        if (sectMoney < spiritStones) {
            return BuildingUpgradeError.InsufficientResources(emptyMap())
        }

        val missing = sectResourceService.getMissingResources(sect, materials)
        if (missing.isNotEmpty()) {
            return BuildingUpgradeError.InsufficientResources(missing)
        }

        return null
    }

    /**
     * 升级建筑
     */
    fun upgrade(building: Entity, sect: Entity): BuildingUpgradeError? {
        val error = canUpgrade(building, sect)
        if (error != null) return error

        val (spiritStones, materials) = calculateUpgradeCost(building)

        // 扣除灵石
        val currentMoney = sect.getComponent<Money?>()?.value ?: 0
        world.entity(sect) {
            it.addComponent(Money(currentMoney - spiritStones))
        }

        // 扣除材料
        sectResourceService.withdrawAll(sect, materials)

        // 升级建筑等级
        levelingService.addExperience(building, 100L)

        // 更新效率和容量
        val currentEfficiency = building.getComponent<BuildingEfficiency?>() ?: BuildingEfficiency.DEFAULT
        val currentCapacity = building.getComponent<BuildingCapacity?>() ?: BuildingCapacity.DEFAULT
        world.entity(building) {
            it.addComponent(BuildingEfficiency(currentEfficiency.value + 0.1f))
            it.addComponent(BuildingCapacity(currentCapacity.value + 50))
        }

        return null
    }

    /**
     * 获取建筑等级
     */
    fun getBuildingLevel(building: Entity): Long {
        val levelAttribute = attributeService.attribute(ATTRIBUTE_LEVEL)
        return building.getRelation<AttributeValue?>(levelAttribute)?.value ?: 1L
    }

    /**
     * 获取宗门等级
     */
    fun getSectLevel(sect: Entity): Long {
        val levelAttribute = attributeService.attribute(ATTRIBUTE_LEVEL)
        return sect.getRelation<AttributeValue?>(levelAttribute)?.value ?: 1L
    }

    /**
     * 获取建筑效率
     */
    fun getBuildingEfficiency(building: Entity): Float {
        return building.getComponent<BuildingEfficiency?>()?.value ?: 1.0f
    }

    /**
     * 获取建筑容量
     */
    fun getBuildingCapacity(building: Entity): Int {
        return building.getComponent<BuildingCapacity?>()?.value ?: 100
    }

    /**
     * 获取建筑类型
     */
    fun getBuildingType(building: Entity): BuildingType? {
        return building.getComponent<BuildingTypeComponent?>()?.type
    }

    /**
     * 获取建筑所属宗门
     */
    fun getBuildingOwner(building: Entity): Entity? {
        require(building.hasTag<Building>()) { "实体${building.id}不是建筑" }
        return building.getRelationUp<OwnedBy>()
    }

    /**
     * 获取宗门的所有建筑
     */
    fun getBuildingsBySect(sect: Entity): Sequence<Entity> {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }
        return world.query { BuildingQueryContext(this) }.entities.filter { building ->
            building.getRelationUp<OwnedBy>() == sect
        }
    }

    /**
     * 获取宗门指定类型的建筑
     */
    fun getBuildingByType(sect: Entity, type: BuildingType): Entity? {
        return getBuildingsBySect(sect).firstOrNull { building ->
            building.getComponent<BuildingTypeComponent?>()?.type == type
        }
    }

    /**
     * 检查宗门是否有指定类型的建筑
     */
    fun hasBuildingType(sect: Entity, type: BuildingType): Boolean {
        return getBuildingByType(sect, type) != null
    }

    /**
     * 获取宗门建筑数量
     */
    fun getBuildingCount(sect: Entity): Int {
        return getBuildingsBySect(sect).count()
    }

    /**
     * 获取当前宗门等级可解锁的建筑类型
     */
    fun getUnlockedBuildingTypes(sect: Entity): Set<BuildingType> {
        val sectLevel = getSectLevel(sect)
        return BuildingType.entries.filter { type ->
            getRequiredSectLevel(type) <= sectLevel
        }.toSet()
    }

    /**
     * 检查宗门是否可以建造指定类型的建筑
     */
    fun canBuild(sect: Entity, type: BuildingType): BuildingBuildError? {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }

        // 检查宗门等级
        val sectLevel = getSectLevel(sect)
        val requiredLevel = getRequiredSectLevel(type)
        if (sectLevel < requiredLevel) {
            return BuildingBuildError.SectLevelTooLow(requiredLevel, sectLevel)
        }

        // 检查是否已有该类型建筑
        if (hasBuildingType(sect, type)) {
            return BuildingBuildError.BuildingTypeExists(type)
        }

        // 检查建筑数量上限
        val currentCount = getBuildingCount(sect)
        val maxBuildings = getMaxBuildingCount(sect)
        if (currentCount >= maxBuildings) {
            return BuildingBuildError.MaxBuildingReached(currentCount, maxBuildings)
        }

        return null
    }

    /**
     * 获取宗门最大建筑数量
     */
    fun getMaxBuildingCount(sect: Entity): Int {
        val sectLevel = getSectLevel(sect)
        return 2 + ((sectLevel - 1) * 2).toInt()
    }

    /**
     * 获取建筑类型所需的宗门等级
     */
    fun getRequiredSectLevel(type: BuildingType): Long = when (type) {
        BuildingType.ALCHEMY_HALL -> 1L
        BuildingType.LIBRARY -> 1L
        BuildingType.TRAINING_HALL -> 2L
        BuildingType.TREASURE_VAULT -> 3L
    }

    /**
     * 删除建筑
     */
    fun demolishBuilding(building: Entity) {
        require(building.hasTag<Building>()) { "实体${building.id}不是建筑" }
        world.destroy(building)
    }

    @PublishedApi
    internal class BuildingQueryContext(world: World) : EntityQueryContext(world) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<Building>()
        }
    }

    companion object {
        private val ATTRIBUTE_LEVEL = Named("level")
    }
}

/**
 * 建筑建造错误
 */
sealed class BuildingBuildError {
    data class SectLevelTooLow(val required: Long, val current: Long) : BuildingBuildError()
    data class BuildingTypeExists(val type: BuildingType) : BuildingBuildError()
    data class MaxBuildingReached(val current: Int, val max: Int) : BuildingBuildError()
    data class InsufficientResources(val missing: Map<Entity, Int>) : BuildingBuildError()
}
