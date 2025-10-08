package cn.jzl.lko.geom.vector

interface IMutableGenericVector<T> : IGenericVector<T> {
    operator fun set(dimension: Int, value: T)
}