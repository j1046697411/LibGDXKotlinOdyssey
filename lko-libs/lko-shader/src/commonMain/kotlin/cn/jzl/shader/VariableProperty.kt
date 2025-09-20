package cn.jzl.shader

import kotlin.reflect.KProperty

data class VariableProperty<T : VarType, O : Operand<T>>(
    private val statementScope: StatementScope,
    private val variable: O
) : Property<T, O> {

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: O) {
        statementScope.statement(Statement.Assignment(variable, value))
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): O = variable
}