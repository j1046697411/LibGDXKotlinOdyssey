package cn.jzl.shader.operand

import cn.jzl.shader.VarType

interface Variable<T : VarType> : Operand<T> {
    val name: String
}