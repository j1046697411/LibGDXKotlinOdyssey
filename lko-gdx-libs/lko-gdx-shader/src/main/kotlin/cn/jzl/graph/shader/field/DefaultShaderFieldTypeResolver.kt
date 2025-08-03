package cn.jzl.graph.shader.field

import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.render.field.*

internal class DefaultShaderFieldTypeResolver : ShaderFieldTypeResolver {
    @Suppress("UNCHECKED_CAST")
    override fun <FO : FieldOutput> resolve(fieldType: FieldType<*>): ShaderFieldType<FO> {
        return when (fieldType) {
            PrimitiveFieldTypes.FloatFieldType -> FloatShaderFieldType
            Vector2Type -> Vector2ShaderFieldType
            Vector3Type -> Vector3ShaderFieldType
            Vector4Type -> Vector4ShaderFieldType
            ColorType -> ColorShaderFieldType
            Matrix4Type -> Matrix4ShaderFieldType
            TextureType -> TextureShaderFieldType
            else -> DefaultShaderFieldType(fieldType)
        } as ShaderFieldType<FO>
    }
}