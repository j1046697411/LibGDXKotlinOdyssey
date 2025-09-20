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

class ShaderTest {

    @Test
    fun test() {
        val program = SimpleProgram()
        program.vertexShader {
            val test = func("test") {
                val username by int.arg
                val password by vec2.arg

                returnValue(password)
            }
            val username by int(10)
            val password by vec2(20f, 20f)
            val user by User(username, password)

            val xx = user.password
            val temp = xx + password

            ifStatement(true.lit) {
            }.elseIfStatement(false.lit) {

            } elseStatement {
            }

            forStatement(1.lit, 10.lit) {
                glPosition = vec4(1f, 1f, 1f, 1f)
                ifStatement(loopVar le 5.lit) {
                    breakStatement()
                }
            }

            whileStatement(false.lit) {
                continueStatement()
            }
            val bTest by bvec2(true, false)

            ifStatement(bTest.any()) {
                glPosition = vec4(1f.lit, 1f.lit, 1f.lit, 1f.lit)
            }

            glPosition = vec4(1f.lit, 1f.lit, 1f.lit, 1f.lit)
            val mat by mat4(0f.lit)

        }

        program.fragmentShader {
            discardStatement()
        }
        val glslVisitor = GlslVisitor()
        val indenter = Indenter()
        glslVisitor.visit(program.vertexShader, indenter)
        glslVisitor.visit(program.fragmentShader, indenter)
        val glsl = buildString { evaluate(indenter, false) { _, _ -> } }
        println(glsl)
    }
}
