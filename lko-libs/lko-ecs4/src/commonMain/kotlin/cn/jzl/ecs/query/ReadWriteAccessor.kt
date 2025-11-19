package cn.jzl.ecs.query

import kotlin.properties.ReadWriteProperty

interface ReadWriteAccessor<T> : ReadOnlyAccessor<T>, ReadWriteProperty<QueriedEntity, T>