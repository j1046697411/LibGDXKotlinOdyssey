package cn.jzl.lko.geom.vector

interface IGenericVector<T> : Dimension {
    operator fun get(dimension: Int): T
}

inline val <T> IGenericVector<T>.components: Sequence<T>
    get() = sequence { for (i in 0 until dimensions) yield(get(i)) }

@Suppress("NOTHING_TO_INLINE")
@PublishedApi
internal inline fun Dimension.checkDimensions(dimensions: Int) {
    check(this.dimensions == dimensions) { "dimensions must be $dimensions" }
}

fun IGenericVector<Int>.toIntVector2(): IntVector2 {
    checkDimensions(2)
    return IntVector2(this[0], this[1])
}

fun IGenericVector<Int>.toIntVector3(): IntVector3 {
    checkDimensions(3)
    return IntVector3(this[0], this[1], this[2])
}

fun IGenericVector<Int>.toIntVector4(): IntVector4 {
    checkDimensions(4)
    return IntVector4(this[0], this[1], this[2], this[3])
}

fun IGenericVector<Float>.toVector2(): Vector2 {
    checkDimensions(2)
    return Vector2(this[0], this[1])
}

fun IGenericVector<Float>.toVector3(): Vector3 {
    checkDimensions(3)
    return Vector3(this[0], this[1], this[2])
}

fun IGenericVector<Float>.toVector4(): Vector4 {
    checkDimensions(4)
    return Vector4(this[0], this[1], this[2], this[3])
}

fun VectorArrayList<Int>.toIntVector2Sequence(): Sequence<IntVector2> = asSequence().map { it.toIntVector2() }
fun VectorArrayList<Int>.toIntVector3Sequence(): Sequence<IntVector3> = asSequence().map { it.toIntVector3() }
fun VectorArrayList<Int>.toIntVector4Sequence(): Sequence<IntVector4> = asSequence().map { it.toIntVector4() }

fun VectorArrayList<Float>.toVector2Sequence(): Sequence<Vector2> = asSequence().map { it.toVector2() }
fun VectorArrayList<Float>.toVector3Sequence(): Sequence<Vector3> = asSequence().map { it.toVector3() }
fun VectorArrayList<Float>.toVector4Sequence(): Sequence<Vector4> = asSequence().map { it.toVector4() }

fun VectorArrayList<Int>.getIntVector2(index: Int): IntVector2 {
    checkDimensions(2)
    return IntVector2(this[index, 0], this[index, 1])
}

fun VectorArrayList<Int>.getIntVector3(index: Int): IntVector3 {
    checkDimensions(3)
    return IntVector3(this[index, 0], this[index, 1], this[index, 2])
}

fun VectorArrayList<Int>.getIntVector4(index: Int): IntVector4 {
    checkDimensions(4)
    return IntVector4(this[index, 0], this[index, 1], this[index, 2], this[index, 3])
}

fun VectorArrayList<Float>.getVector2(index: Int): Vector2 {
    checkDimensions(2)
    return Vector2(this[index, 0], this[index, 1])
}

fun VectorArrayList<Float>.getVector3(index: Int): Vector3 {
    checkDimensions(3)
    return Vector3(this[index, 0], this[index, 1], this[index, 2])
}

fun VectorArrayList<Float>.getVector4(index: Int): Vector4 {
    checkDimensions(4)
    return Vector4(this[index, 0], this[index, 1], this[index, 2], this[index, 3])
}
