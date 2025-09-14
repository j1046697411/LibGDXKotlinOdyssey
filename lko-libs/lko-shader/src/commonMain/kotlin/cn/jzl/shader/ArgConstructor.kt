package cn.jzl.shader

import cn.jzl.shader.VarType

@JvmInline
value class ArgConstructor<T : VarType>(val type: T)