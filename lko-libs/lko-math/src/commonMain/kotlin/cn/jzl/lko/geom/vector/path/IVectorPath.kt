package cn.jzl.lko.geom.vector.path

interface IVectorPath : VectorBuilder {
    fun toSvgString(): String
}