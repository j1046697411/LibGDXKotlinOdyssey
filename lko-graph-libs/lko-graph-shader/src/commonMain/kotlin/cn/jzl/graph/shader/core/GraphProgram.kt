package cn.jzl.graph.shader.core

interface GraphProgram {

    fun begin(context: ShaderContext)

    fun render(context: ShaderContext)

    fun end()

    fun setUniform(location: Int, v: Int)
    fun setUniform(location: Int, v1: Int, v2: Int)
    fun setUniform(location: Int, v1: Int, v2: Int, v3: Int)
    fun setUniform(location: Int, v1: Int, v2: Int, v3: Int, v4: Int)

    fun setUniform(location: Int, v1: Float)
    fun setUniform(location: Int, v1: Float, v2: Float)
    fun setUniform(location: Int, v1: Float, v2: Float, v3: Float)
    fun setUniform(location: Int, v1: Float, v2: Float, v3: Float, v4: Float)

    fun setUniform1(location: Int, value: FloatArray, offset: Int)
    fun setUniform2(location: Int, value: FloatArray, offset: Int)
    fun setUniform3(location: Int, value: FloatArray, offset: Int)
    fun setUniform4(location: Int, value: FloatArray, offset: Int)

    fun setUniformMatrix4(location: Int, value: FloatArray, offset: Int)
}

fun test() {
    buildGraphProgram {
        val modelMatrixLocation = int.uniform("u_modelMatrix", global = true) { program.setUniform(it, 20) }
        vertexShader {
            val x by modelMatrixLocation
        }

    }

}