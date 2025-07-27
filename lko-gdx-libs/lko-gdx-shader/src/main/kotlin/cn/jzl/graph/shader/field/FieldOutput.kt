package cn.jzl.graph.shader.field

interface FieldOutput {
    val fieldType: ShaderFieldType<out FieldOutput>
    val representation: String
}