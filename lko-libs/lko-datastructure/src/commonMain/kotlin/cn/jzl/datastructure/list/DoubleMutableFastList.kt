package cn.jzl.datastructure.list

interface DoubleMutableFastList : MutableFastList<Double> {
    fun insertLastAll(elements: DoubleArray)
    fun insertAll(index: Int, elements: DoubleArray)
}