package cn.jzl.ecs

import cn.jzl.di.DI
import cn.jzl.di.instance
import cn.jzl.ecs.observers.ObserveService
import cn.jzl.ecs.query.QueryService
import cn.jzl.ecs.system.Pipeline

class World(@PublishedApi internal val di: DI) {

    @PublishedApi
    internal val entityStore by di.instance<EntityStore>()

    @PublishedApi
    internal val entityService by di.instance<EntityService>()

    @PublishedApi
    internal val archetypeService by di.instance<ArchetypeService>()

    @PublishedApi
    internal val componentService by di.instance<ComponentService>()

    @PublishedApi
    internal val relationService by di.instance<RelationService>()

    @PublishedApi
    internal val familyService by di.instance<FamilyService>()

    @PublishedApi
    internal val queryService by di.instance<QueryService>()

    @PublishedApi
    internal val observeService by di.instance<ObserveService>()

    @PublishedApi
    internal val shadedComponentService by di.instance<ShadedComponentService>()

    @PublishedApi
    internal val components: Components get() = componentService.components

    @PublishedApi
    internal val pipeline: Pipeline by di.instance()

    @PublishedApi
    internal val relations by di.instance<RelationProvider>()
}