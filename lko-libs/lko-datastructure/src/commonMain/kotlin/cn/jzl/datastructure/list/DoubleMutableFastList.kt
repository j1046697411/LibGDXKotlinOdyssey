package cn.jzl.datastructure.list

interface DoubleMutableFastList : PrimitiveMutableFastList<Double> {
    fun insertLastAll(elements: DoubleArray)
    fun insertAll(index: Int, elements: DoubleArray)
}