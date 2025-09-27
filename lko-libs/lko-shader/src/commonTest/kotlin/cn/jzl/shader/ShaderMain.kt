package cn.jzl.shader

import kotlin.reflect.KClass
import kotlin.test.Test

class User(statementScope: StatementScope, name: String) : Struct<User>(statementScope, name) {
    val username by int
    val password by vec2

    companion object : StructConstructor<User> {
        override val structType: KClass<User> = User::class
        override val factory: (StatementScope, String) -> User = ::User
    }
}

fun ExpressionScope.test(user: User): Operand<VarType.Vec2> = func("test", user) {
    val test by user.arg
    returnValue(test.password)
}

class ShaderTest {

    @Test
    fun test() {
        val program = SimpleProgram()
        program.vertexShader {
            val user by User(1.lit, vec2(1f.lit))
            val angle by normalizeAngle(1000f.lit)
            val angle1 by normalizeAngle(test(user))
            glPosition = normalizeAngle(vec4(100f.lit))
        }
        program.vertexShader {
            val x by 20.lit + 15.lit

        }

        val glslVisitor = GlslVisitor()
        val indenter = Indenter()
        glslVisitor.visit(program.vertexShader, indenter)
        glslVisitor.visit(program.fragmentShader, indenter)
        val glsl = buildString { evaluate(indenter, false) { _, _ -> } }
        println(glsl)
    }
}
