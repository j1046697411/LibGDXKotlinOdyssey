package cn.jzl.shader

import kotlin.reflect.KProperty

interface PrimitiveScope : VarTypeAccessor {

    val Float.lit: Operand.Literal<Float, VarType.Float> get() = Operand.Literal.FloatLiteral(this)
    val Int.lit: Operand.Literal<Int, VarType.Integer> get() = Operand.Literal.IntLiteral(this)
    val Boolean.lit: Operand.Literal<Boolean, VarType.Boolean> get() = Operand.Literal.BooleanLiteral(this)


    operator fun <T : VarType> Operand<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>>

    operator fun <T : VarType> T.invoke(operand: Operand<T>): Operand<T> = operand
    operator fun VarType.Float.invoke(value: Float): Operand<VarType.Float> = value.lit
    operator fun VarType.Integer.invoke(value: Int): Operand<VarType.Integer> = value.lit
    operator fun VarType.Boolean.invoke(value: Boolean): Operand<VarType.Boolean> = value.lit

    operator fun <T : VarType.FloatComposite> T.invoke(vararg args: Operand<VarType.Float>): Operand<T> {
        return Operand.CompositeConstructor(this, args.toList())
    }

    operator fun <T : VarType.FloatComposite> T.invoke(vararg args: Float): Operand<T> {
        return Operand.CompositeConstructor(this, args.map { it.lit })
    }

    operator fun <T : VarType.IntegerType> T.invoke(vararg args: Operand<VarType.Integer>): Operand<T> {
        return Operand.CompositeConstructor(this, args.toList())
    }

    operator fun <T : VarType.IntegerType> T.invoke(vararg args: Int): Operand<T> {
        return Operand.CompositeConstructor(this, args.map { it.lit })
    }

    operator fun <T : VarType.BooleanType> T.invoke(vararg args: Operand<VarType.Boolean>): Operand<T> {
        return Operand.CompositeConstructor(this, args.toList())
    }

    operator fun <T : VarType.BooleanType> T.invoke(vararg args: Boolean): Operand<T> {
        return Operand.CompositeConstructor(this, args.map { it.lit })
    }
}