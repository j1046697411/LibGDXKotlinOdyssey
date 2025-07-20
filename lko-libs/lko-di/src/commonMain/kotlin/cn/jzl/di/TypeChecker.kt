package cn.jzl.di

import org.kodein.type.TypeToken

internal sealed interface TypeChecker {

    val type: TypeToken<*>

    fun check(typeToken: TypeToken<*>): Boolean

    @JvmInline
    value class Up(override val type: TypeToken<*>) : TypeChecker {
        override fun check(typeToken: TypeToken<*>): Boolean {
            return type.isAssignableFrom(typeToken)
        }

        override fun toString(): String {
            return "Up[${type.simpleDispString()}]"
        }
    }

    @JvmInline
    value class Down(override val type: TypeToken<*>) : TypeChecker {
        override fun check(typeToken: TypeToken<*>): Boolean {
            return typeToken.isAssignableFrom(type)
        }

        override fun toString(): String {
            return "Down[${type.simpleDispString()}]"
        }
    }
}