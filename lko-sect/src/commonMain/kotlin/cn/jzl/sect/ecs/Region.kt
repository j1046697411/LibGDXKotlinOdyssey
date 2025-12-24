package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityCreateContext
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

sealed class Region
sealed class ContainsRelation



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

class RegionService(world: World) : EntityRelationContext(world) {

    private val resourceProductionService by world.di.instance<ResourceProductionService>()

    private val regionQuery: Query<RegionContext> = world.query { RegionContext(this) }
    private val rootRegion: Query<RootRegionContext> = world.query { RootRegionContext(this) }

    private val subregionQueries = mutableMapOf<Entity, Query<SubregionContext>>()

    @ECSDsl
    fun createRegion(named: Named, parentRegion: Entity?, block: ResourceOutputConfig.() -> Unit): Entity {
        return world.entity {
            it.addTag<Region>()
            it.addComponent(named)
            if (parentRegion != null) it.addRelation<ContainsRelation>(parentRegion)
            resourceProductionService.configureOutput(this, it, block)
        }
    }

    fun getSubregion(region: Entity): Query<SubregionContext> {
        return subregionQueries.getOrPut(region) { world.query { SubregionContext(this, region) } }
    }

    fun getRootRegion(): Query<RootRegionContext> = rootRegion

    fun getAllRegions(): Query<RegionContext> = regionQuery

    open class RegionContext(world: World) : EntityQueryContext(world) {
        val named: Named by component<Named>()
        val parentRegion: Entity? get() = getRelationUp<ContainsRelation>()
        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Region>())
        }
    }

    open class RootRegionContext(world: World) : RegionContext(world) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            not { kind(relations.id<ContainsRelation>()) }
        }
    }

    class SubregionContext(world: World, val region: Entity) : RegionContext(world) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(relations.component<Region>())
            relation(relations.relation<ContainsRelation>(region))
        }
    }
}
