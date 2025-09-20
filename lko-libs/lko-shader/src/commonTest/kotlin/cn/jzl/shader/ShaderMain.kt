package cn.jzl.shader

import kotlin.math.E
import kotlin.math.PI
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

fun ProgramScope.ShaderScope.test(username: Operand<VarType.Integer>, password: Operand<VarType.Vec2>): Operand<VarType.Vec2> {
    return func("test") {
        val username by int.arg
        val password by vec2.arg
        returnValue(password)
    }(username, password)
}

class ShaderTest {

    @Test
    fun test() {
        val program = SimpleProgram()
        program.vertexShader {
            val pi by vec4(PI.toFloat()).define("PI")
            val e by E.toFloat().lit.constant("E")
            val pos by vec4.attribute("pos", precision = Precision.High, location = 0)
            val pos1 by vec4.varying("pos1")

            val test by pos le pos1

            val username by int(10)
            val password by vec2(20f, 20f)
            val user by User(username, password)
            val xx by test(username, password)
            val temp = xx + password

            ifStatement(test.any()) {
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
