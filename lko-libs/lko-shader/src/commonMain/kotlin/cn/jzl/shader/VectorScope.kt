package cn.jzl.shader

interface VectorScope : PrimitiveScope {

    val <V : VarType> Operand<out VarType.Vector2<V, *, *, *>>.x: Operand<V> get() = Operand.Swizzle(this, "x", type.component1())
    val <V : VarType> Operand<out VarType.Vector2<V, *, *, *>>.y: Operand<V> get() = Operand.Swizzle(this, "y", type.component1())
    val <V : VarType> Operand<out VarType.Vector3<V, *, *, *>>.z: Operand<V> get() = Operand.Swizzle(this, "z", type.component1())
    val <V : VarType> Operand<out VarType.Vector4<V, *, *, *>>.w: Operand<V> get() = Operand.Swizzle(this, "w", type.component1())

    val <V : VarType> Operand<out VarType.Vector2<V, *, *, *>>.r: Operand<V> get() = Operand.Swizzle(this, "r", type.component1())
    val <V : VarType> Operand<out VarType.Vector2<V, *, *, *>>.g: Operand<V> get() = Operand.Swizzle(this, "g", type.component1())
    val <V : VarType> Operand<out VarType.Vector3<V, *, *, *>>.b: Operand<V> get() = Operand.Swizzle(this, "b", type.component1())
    val <V : VarType> Operand<out VarType.Vector4<V, *, *, *>>.a: Operand<V> get() = Operand.Swizzle(this, "a", type.component1())

    val <V : VarType> Operand<out VarType.Vector2<V, *, *, *>>.s: Operand<V> get() = Operand.Swizzle(this, "s", type.component1())
    val <V : VarType> Operand<out VarType.Vector2<V, *, *, *>>.t: Operand<V> get() = Operand.Swizzle(this, "t", type.component1())
    val <V : VarType> Operand<out VarType.Vector3<V, *, *, *>>.p: Operand<V> get() = Operand.Swizzle(this, "p", type.component1())
    val <V : VarType> Operand<out VarType.Vector4<V, *, *, *>>.q: Operand<V> get() = Operand.Swizzle(this, "q", type.component1())

    val <Vec3 : VarType> Operand<out VarType.Vector3<*, *, Vec3, *>>.xyz: Operand<Vec3> get() = Operand.Swizzle(this, "xyz", type.component3())
    val <Vec3 : VarType> Operand<out VarType.Vector3<*, *, Vec3, *>>.rgb: Operand<Vec3> get() = Operand.Swizzle(this, "rgb", type.component3())

    fun <Vec2 : VarType> Operand<out VarType.Vector<*, Vec2, *, *>>.swizzle2(swizzle: String): Operand<Vec2> {
        return Operand.Swizzle(this, swizzle, type.component2())
    }

    fun <Vec3 : VarType> Operand<out VarType.Vector<*, *, Vec3, *>>.swizzle3(swizzle: String): Operand<Vec3> {
        return Operand.Swizzle(this, swizzle, type.component3())
    }

    fun <Vec4 : VarType> Operand<out VarType.Vector<*, *, *, Vec4>>.swizzle4(swizzle: String): Operand<Vec4> {
        return Operand.Swizzle(this, swizzle, type.component4())
    }
}