package cn.jzl.datastructure.list

interface FloatMutableFastList : PrimitiveMutableFastList<Float> {
    fun insertLastAll(elements: FloatArray)
    fun insertAll(index: Int, elements: FloatArray)
}