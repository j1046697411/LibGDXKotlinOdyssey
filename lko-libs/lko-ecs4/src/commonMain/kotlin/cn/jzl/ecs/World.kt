package cn.jzl.ecs

import cn.jzl.di.DI
import cn.jzl.di.instance
import cn.jzl.ecs.observers.ObserveService
import cn.jzl.ecs.query.QueryService

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
}