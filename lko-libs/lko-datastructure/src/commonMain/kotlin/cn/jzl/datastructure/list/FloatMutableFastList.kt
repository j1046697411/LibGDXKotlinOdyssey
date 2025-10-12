package cn.jzl.datastructure.list

interface FloatMutableFastList : MutableFastList<Float> {
    fun insertLastAll(elements: FloatArray)
    fun insertAll(index: Int, elements: FloatArray)
}