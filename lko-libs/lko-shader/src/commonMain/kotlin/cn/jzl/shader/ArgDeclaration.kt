package cn.jzl.shader

@JvmInline
value class ArgDeclaration<T : VarType>(val type: T)

@JvmInline
value class ArgStruct<T : VarType>(val type: T)
