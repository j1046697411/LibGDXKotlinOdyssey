package cn.jzl.sect.ecs.region

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
import cn.jzl.ecs.kind
import cn.jzl.ecs.not
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.Query
import cn.jzl.ecs.query.query
import cn.jzl.ecs.relation
import cn.jzl.ecs.relations
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.production.ResourceOutputConfig
import cn.jzl.sect.ecs.production.ResourceProductionService
import cn.jzl.sect.ecs.production.resourceProductionAddon

/**
 * 区域系统包，包含区域组件、服务和addon配置
 * 
 * 主要功能：
 * 1. 定义区域标记和包含关系
 * 2. 提供区域创建和管理服务
 * 3. 支持区域层次结构
 * 4. 整合资源生产系统
 */

/**
 * 区域标记组件
 * 用于标识实体为区域
 */
sealed class Region

/**
 * 包含关系组件
 * 表示区域之间的包含关系
 */
sealed class ContainsRelation

/**
 * 区域addon
 * 注册区域相关组件和服务
 */
val regionAddon = createAddon("Region") {
    install(resourceProductionAddon)
    injects { this bind singleton { new(::RegionService) } }
    entities {
        world.componentId<Region> { it.tag() }
        world.componentId<ContainsRelation> {
            it.tag()
            it.singleRelation()
        }
    }
}

/**
 * 区域服务
 * 管理区域系统的核心功能
 * 
 * @param world ECS世界实例
 */
class RegionService(world: World) : EntityRelationContext(world) {

    private val resourceProductionService by world.di.instance<ResourceProductionService>()

    private val regionQuery: Query<RegionContext> = world.query { RegionContext(this) }
    private val rootRegion: Query<RootRegionContext> = world.query { RootRegionContext(this) }

    private val subregionQueries = mutableMapOf<Entity, Query<SubregionContext>>()

    /**
     * 创建区域
     * 
     * @param named 区域名称
     * @param parentRegion 父区域，可为空
     * @param block 资源产出配置块
     * @return 创建的区域实体
     */
    @ECSDsl
    fun createRegion(named: Named, parentRegion: Entity?, block: ResourceOutputConfig.() -> Unit): Entity {
        return world.entity {
            it.addTag<Region>()
            it.addComponent(named)
            if (parentRegion != null) it.addRelation<ContainsRelation>(parentRegion)
            resourceProductionService.configureOutput(this, it, block)
        }
    }

    /**
     * 获取子区域查询
     * 
     * @param region 父区域
     * @return 子区域查询
     */
    fun getSubregion(region: Entity): Query<SubregionContext> {
        return subregionQueries.getOrPut(region) { world.query { SubregionContext(this, region) } }
    }

    /**
     * 获取根区域查询
     * 
     * @return 根区域查询
     */
    fun getRootRegion(): Query<RootRegionContext> = rootRegion

    /**
     * 获取所有区域查询
     * 
     * @return 所有区域查询
     */
    fun getAllRegions(): Query<RegionContext> = regionQuery

    /**
     * 区域查询上下文
     * 
     * @param world ECS世界实例
     */
    open class RegionContext(world: World) : EntityQueryContext(world) {
        val named: Named by component<Named>()
        val parentRegion: Entity? get() = getRelationUp<ContainsRelation>()
        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Region>())
        }
    }

    /**
     * 根区域查询上下文
     * 
     * @param world ECS世界实例
     */
    open class RootRegionContext(world: World) : RegionContext(world) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            not { kind(relations.id<ContainsRelation>()) }
        }
    }

    /**
     * 子区域查询上下文
     * 
     * @param world ECS世界实例
     * @param region 父区域
     */
    class SubregionContext(world: World, val region: Entity) : RegionContext(world) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Region>())
            relation(relations.relation<ContainsRelation>(region))
        }
    }
}
