package cn.jzl.shader

sealed interface VarType {

    interface Comparable : VarType
    interface Computable : VarType

    data object Integer : Comparable, Computable
    data object Float : Comparable, Computable

    data object Boolean : Comparable

    data object Void : VarType
}