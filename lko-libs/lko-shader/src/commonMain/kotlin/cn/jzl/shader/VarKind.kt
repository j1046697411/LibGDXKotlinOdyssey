package cn.jzl.shader

enum class VarKind(val bytesSize: Int) {
    Void(0),
    Bool(1),
    Byte(1),
    UByte(1),
    Short(2),
    UShort(2),
    Integer(4),
    Float(4),
    Struct(-1)
}